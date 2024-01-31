package ingsw2.codekatabattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.Controllers.AuthorizationController;
import ingsw2.codekatabattle.Controllers.EducatorController.TournamentManagementController;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.TournamentDTOS.PromoteToModeratorDTO;
import ingsw2.codekatabattle.Model.TournamentDTOS.TournamentClosureDTO;
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

import static org.hamcrest.core.StringContains.containsString;
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
public class TournamentManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentManagementController tournamentManagementController;
    @Autowired
    private AuthorizationController authorizationController;
    private String token;

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(tournamentManagementController);
        Assertions.assertNotNull(authorizationController);
    }

    @BeforeAll
    public void registrationAndLogin() {
        RegisterDTO registerDTO = new RegisterDTO("tournamentManagementControllerTest@gmail.com", "test", "test", "TournamentManagementControllerTest", "TEST", UserRole.EDUCATOR);
        ResponseEntity<?> registerResponse = this.authorizationController.register(registerDTO);
        String responseBody = (String) registerResponse.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", responseBody);

        ResponseEntity<?> loginResponse = this.authorizationController.login(new LoginDTO("TournamentManagementControllerTest", "TEST"));

        UserTokenDTO userTokenDTO = (UserTokenDTO) loginResponse.getBody();
        this.token = userTokenDTO.getToken();
        System.out.println(token);

        RegisterDTO registerDTO1 = new RegisterDTO("promoteToModerator@gmail.com", "test", "test", "PromoteToModeratorTest", "TEST", UserRole.EDUCATOR);
        ResponseEntity<?> registerResponse1 = this.authorizationController.register(registerDTO1);
        String registerResponseBody = (String) registerResponse1.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", registerResponseBody);
    }

    @Test
    @Order(1)
    public void createPublicTournamentAndPromoteModerator() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plus(5, ChronoUnit.MINUTES);
        Date futureDate = java.sql.Timestamp.valueOf(futureDateTime);

        TournamentCreationDTO tournamentCreationDTO = new TournamentCreationDTO("publicTournament", futureDate, true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(tournamentCreationDTO);

        mockMvc.perform(post("/api/eduTnt/create")
                        .header("Authorization", "Bearer "+ this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("The PUBLIC tournament has been created"))
                .andExpect(status().isOk());

        PromoteToModeratorDTO promoteToModeratorDTO = new PromoteToModeratorDTO("publicTournament", "PromoteToModeratorTest");
        String jsonBody1 = objectMapper.writeValueAsString(promoteToModeratorDTO);

        mockMvc.perform(post("/api/eduTnt/promote")
                        .header("Authorization", "Bearer "+ this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("User was successfully promoted to Moderator of the tournament"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(2)
    public void createPrivateTournament() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plus(5, ChronoUnit.MINUTES);
        Date futureDate = java.sql.Timestamp.valueOf(futureDateTime);

        TournamentCreationDTO tournamentCreationDTO = new TournamentCreationDTO("privateTournament", futureDate, false);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(tournamentCreationDTO);

        mockMvc.perform(post("/api/eduTnt/create")
                        .header("Authorization", "Bearer "+ this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string(containsString("The PRIVATE tournament has been created. KEYWORD: ")))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void closeTournament() throws Exception {
        TournamentClosureDTO tournamentCreationDTO = new TournamentClosureDTO("privateTournament", false);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(tournamentCreationDTO);

        mockMvc.perform(post("/api/eduTnt/close")
                        .header("Authorization", "Bearer "+ this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("The tournament has been successfully closed"))
                .andExpect(status().isOk());

        TournamentClosureDTO tournamentCreationDTO1 = new TournamentClosureDTO("publicTournament", false);
        String jsonBody1 = objectMapper.writeValueAsString(tournamentCreationDTO1);

        mockMvc.perform(post("/api/eduTnt/close")
                        .header("Authorization", "Bearer "+ this.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("The tournament has been successfully closed"))
                .andExpect(status().isOk());
    }

}
