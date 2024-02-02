package ingsw2.codekatabattle.Controllers;

import ingsw2.codekatabattle.DAO.UserDAO;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.User;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.UserDTOS.LoginDTO;
import ingsw2.codekatabattle.Model.UserDTOS.RegisterDTO;
import ingsw2.codekatabattle.Model.UserDTOS.UserDTO;
import ingsw2.codekatabattle.Model.UserDTOS.UserTokenDTO;
import ingsw2.codekatabattle.Security.JwtUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * Controller handling user authentication.
 * All endpoints are mapped under the "/api/auth" base URL.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthorizationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user based on the provided login credentials.
     * @param loginDTO The login request containing user credentials (stored in a {@link LoginDTO} object).
     *
     * @return {@link ResponseEntity} containing the authentication result:
     *         If authentication is successful, returns a 200 OK status with a JWT token in the response body.
     *         If authentication fails due to invalid credentials, returns an Unprocessable Entity status
     *         with an error message indicating invalid credentials.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO)  {

        try {
            Authentication authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword()));
            String name = authentication.getName();
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String grantedAuthority = null;
            for (GrantedAuthority authority : authorities) {
                grantedAuthority = authority.getAuthority();
            }

            User user = new User(name,"", grantedAuthority.equals("ROLE_EDUCATOR") ? UserRole.EDUCATOR : UserRole.STUDENT);
            String token = jwtUtil.createToken(user);
            UserTokenDTO userTokenDTO = new UserTokenDTO(name, grantedAuthority.equals("ROLE_EDUCATOR") ? UserRole.EDUCATOR : UserRole.STUDENT, token);

            log.info("User: " + name + " has logged in");
            return ResponseEntity.ok(userTokenDTO);

        }catch (BadCredentialsException e){
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(ServerResponse.INVALID_CREDENTIALS));
        }catch (Exception e){
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        }
    }

    /**
     * Registers a new user based on the provided registration request.
     * @param registerDTO The registration request containing user details.
     *                    The details are stored in a {@link RegisterDTO} object.
     *
     * @return {@link ResponseEntity} indicating the success or failure of the user registration:
     *         If registration is successful, returns a 200 OK status with a success message.
     *         If registration fails, returns an Unprocessable Entity status with an error message
     *         indicating the reason for the failure.
     */
    @PostMapping ("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDTO){

        String encryptedPassword = passwordEncoder.encode(registerDTO.getPassword());

        User u = new User(registerDTO.getEmail(), registerDTO.getName(), registerDTO.getSurname(), registerDTO.getUsername(), encryptedPassword, registerDTO.getRole());
        ServerResponse result = userDAO.registerUser(u);

        if(result == ServerResponse.USER_REGISTRATION_OK){
            log.info(registerDTO.getUsername() + " has registered to CKB as " + (registerDTO.getRole().equals(UserRole.STUDENT) ? "a Student" : "an Educator"));
            return ResponseEntity.ok(ServerResponse.toString(result));
        }else {
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(result));
        }
    }

    /**
     * Retrieves user information based on the currently authenticated user.
     * Requires the user to have either 'EDUCATOR' or 'STUDENT' role for access.
     * @return {@link ResponseEntity} containing user information in the body if the user is found,
     *         otherwise returns an Unprocessable Entity status with an error message indicating
     *         that the user is not registered.
     *         The user information is stored in a {@link UserDTO} object.
     */
    @PreAuthorize("hasAnyRole('EDUCATOR', 'STUDENT')")
    @GetMapping("/userInformation")
    public ResponseEntity<?> getUserInformation(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        User user = userDAO.getUserByUsername(username);

        if(user == null){
            return ResponseEntity.unprocessableEntity().body(ServerResponse.toString(ServerResponse.USER_IS_NOT_REGISTERED));
        }else {
            UserDTO userDTO = new UserDTO(user.getEmail(),
                    user.getName(),
                    user.getSurname(),
                    user.getUsername(),
                    user.getRole());
            return ResponseEntity.ok().body(userDTO);
        }
    }
}
