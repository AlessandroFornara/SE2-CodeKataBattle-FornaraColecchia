package ingsw2.codekatabattle.Controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling user authentication.
 * All endpoints are mapped under the "/api/auth" base URL.
 */
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthorizationController {

    @PostMapping("/login")
    public ResponseEntity<?> login()  {

        return null;
    }

    @PostMapping ("/register")
    public ResponseEntity<?> register(){

        return null;
    }
}
