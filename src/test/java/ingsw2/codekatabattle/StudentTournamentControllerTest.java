package ingsw2.codekatabattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.Controllers.AuthorizationController;
import ingsw2.codekatabattle.Controllers.EducatorController.TournamentManagementController;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.TournamentDTOS.SubscribeTntDTO;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentCreationDTO;
import ingsw2.codekatabattle.Model.UserDTOS.LoginDTO;
import ingsw2.codekatabattle.Model.UserDTOS.RegisterDTO;
import ingsw2.codekatabattle.Model.UserDTOS.UserTokenDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("local")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentTournamentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentManagementController tournamentManagementController;
    @Autowired
    private AuthorizationController authorizationController;
    private String educatorToken;
    private String studentToken;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(tournamentManagementController);
        Assertions.assertNotNull(authorizationController);
    }

    @BeforeAll
    public void registrationAndLogin() {
        RegisterDTO registerDTO = new RegisterDTO("createTournament@gmail.com", "test", "test", "createTournament", "TEST", UserRole.EDUCATOR);
        ResponseEntity<?> registerResponse = this.authorizationController.register(registerDTO);
        String responseBody = (String) registerResponse.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", responseBody);

        ResponseEntity<?> loginResponse = this.authorizationController.login(new LoginDTO("createTournament", "TEST"));

        UserTokenDTO userTokenDTO = (UserTokenDTO) loginResponse.getBody();
        this.educatorToken = userTokenDTO.getToken();
        System.out.println(educatorToken);

        RegisterDTO registerDTO1 = new RegisterDTO("subscribeTest@gmail.com", "test", "test", "subscribeTest", "TEST", UserRole.STUDENT);
        ResponseEntity<?> registerResponse1 = this.authorizationController.register(registerDTO1);
        String registerResponseBody = (String) registerResponse1.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", registerResponseBody);

        ResponseEntity<?> loginResponse1 = this.authorizationController.login(new LoginDTO("subscribeTest", "TEST"));

        UserTokenDTO userTokenDTO1 = (UserTokenDTO) loginResponse1.getBody();
        this.studentToken = userTokenDTO1.getToken();
        System.out.println(studentToken);
    }

    @Test
    public void subscribe() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plus(5, ChronoUnit.MINUTES);
        Date futureDate = java.sql.Timestamp.valueOf(futureDateTime);

        TournamentCreationDTO tournamentCreationDTO = new TournamentCreationDTO("subscribeTournament", futureDate, true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(tournamentCreationDTO);

        mockMvc.perform(post("/api/eduTnt/create")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("The PUBLIC tournament has been created"))
                .andExpect(status().isOk());

        SubscribeTntDTO subscribeTntDTO = new SubscribeTntDTO("subscribeTournament", true);
        String jsonBody1 = objectMapper.writeValueAsString(subscribeTntDTO);

        mockMvc.perform(post("/api/studTnt/subscribe")
                        .header("Authorization", "Bearer "+ this.studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("User successfully subscribed to the tournament"))
                .andExpect(status().isOk());
    }

}
