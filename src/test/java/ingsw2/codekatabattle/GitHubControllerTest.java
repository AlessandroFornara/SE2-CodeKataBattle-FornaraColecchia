package ingsw2.codekatabattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.Controllers.AuthorizationController;
import ingsw2.codekatabattle.Controllers.EducatorController.TournamentManagementController;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.AutomatedEvaluationDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.BattleCreationDTO;
import ingsw2.codekatabattle.Model.TeamDTOS.TeamCreationDTO;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
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
public class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentManagementController tournamentManagementController;
    @Autowired
    private AuthorizationController authorizationController;
    private String educatorToken;
    private String studentToken;
    private String teamKeyword;

    @BeforeAll
    public void registrationAndLogin() {
        RegisterDTO registerDTO = new RegisterDTO("gitHub1@gmail.com", "test", "test", "gitHub", "TEST", UserRole.EDUCATOR);
        ResponseEntity<?> registerResponse = this.authorizationController.register(registerDTO);
        String responseBody = (String) registerResponse.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", responseBody);

        ResponseEntity<?> loginResponse = this.authorizationController.login(new LoginDTO("gitHub", "TEST"));

        UserTokenDTO userTokenDTO = (UserTokenDTO) loginResponse.getBody();
        this.educatorToken = userTokenDTO.getToken();
        System.out.println(educatorToken);

        RegisterDTO registerDTO1 = new RegisterDTO("createTeamGitHubTest@gmail.com", "test", "test", "createTeamGitHub", "TEST", UserRole.STUDENT);
        ResponseEntity<?> registerResponse1 = this.authorizationController.register(registerDTO1);
        String registerResponseBody = (String) registerResponse1.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", registerResponseBody);

        ResponseEntity<?> loginResponse1 = this.authorizationController.login(new LoginDTO("createTeamGitHub", "TEST"));

        UserTokenDTO userTokenDTO1 = (UserTokenDTO) loginResponse1.getBody();
        this.studentToken = userTokenDTO1.getToken();
        System.out.println(studentToken);

    }
    @Test
    @Order(1)
    public void createBattle() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plus(2, ChronoUnit.MINUTES);
        Date futureDate = java.sql.Timestamp.valueOf(futureDateTime);

        TournamentCreationDTO tournamentCreationDTO = new TournamentCreationDTO("GitHubTournament", futureDate, true);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(tournamentCreationDTO);

        mockMvc.perform(post("/api/eduTnt/create")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("The PUBLIC tournament has been created"))
                .andExpect(status().isOk());

        LocalDateTime battleRegistrationTime = now.plus(3, ChronoUnit.MINUTES);
        Date battleRegistrationDate = java.sql.Timestamp.valueOf(battleRegistrationTime);
        LocalDateTime battleSubmissionTime = now.plus(4, ChronoUnit.MINUTES);
        Date battleSubmissionDate = java.sql.Timestamp.valueOf(battleSubmissionTime);
        ArrayList<String> input = new ArrayList<>();
        input.add("inputTest");
        ArrayList<String> output = new ArrayList<>();
        output.add("outputTest");
        CodeKata codeKata = new CodeKata(input, output, "Description", "Config");

        System.out.println(futureDate);
        System.out.println(battleRegistrationDate);
        System.out.println(battleSubmissionDate);

        BattleCreationDTO battleCreationDTO = new BattleCreationDTO("battle", "GitHubTournament", battleRegistrationDate, battleSubmissionDate, codeKata, 4, 2);
        String jsonBody1 = objectMapper.writeValueAsString(battleCreationDTO);

        mockMvc.perform(post("/api/eduBattle/create")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("The battle has been created"))
                .andExpect(status().isOk());

    }

    @Test
    @Order(2)
    public void subscribe() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeTntDTO subscribeTntDTO = new SubscribeTntDTO("GitHubTournament", true);
        String jsonBody1 = objectMapper.writeValueAsString(subscribeTntDTO);

        mockMvc.perform(post("/api/studTnt/subscribe")
                        .header("Authorization", "Bearer "+ this.studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("User successfully subscribed to the tournament"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(3)
    public void createTeam() throws Exception {

        TeamCreationDTO teamCreationDTO = new TeamCreationDTO("TEAM", "GitHubTournament-battle");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(teamCreationDTO);

        MvcResult result = mockMvc.perform(post("/api/studTeam/create")
                        .header("Authorization", "Bearer " + this.studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string(containsString("Team has been successfully created")))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();

        String keywordPrefix = "KEYWORD: ";
        int keywordIndex = responseString.indexOf(keywordPrefix);
        String keyword = null;
        if (keywordIndex != -1) {
            keyword = responseString.substring(keywordIndex + keywordPrefix.length());
        }

        System.out.println(keyword);
        this.teamKeyword = keyword;
    }

    @Test
    @Order(4)
    public void automatedEvaluation() throws Exception {
        ArrayList<String> output = new ArrayList<>();
        output.add("outputTest");
        AutomatedEvaluationDTO automatedEvaluationDTO = new AutomatedEvaluationDTO(this.teamKeyword, "GitHubTournament-battle", output);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(automatedEvaluationDTO);

        mockMvc.perform(post("/api/GitHub/automatedEvaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("Generic internal server error"))
                .andExpect(status().isUnprocessableEntity());
    }
}
