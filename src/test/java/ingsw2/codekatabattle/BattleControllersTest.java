package ingsw2.codekatabattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.Controllers.AuthorizationController;
import ingsw2.codekatabattle.Controllers.EducatorController.TournamentManagementController;
import ingsw2.codekatabattle.Entities.CodeKata;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.BattleDTOS.BattleCreationDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.CloseConsolidationStageDTO;
import ingsw2.codekatabattle.Model.BattleDTOS.EvaluateDTO;
import ingsw2.codekatabattle.Model.TeamDTOS.JoinTeamDTO;
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
public class BattleControllersTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TournamentManagementController tournamentManagementController;
    @Autowired
    private AuthorizationController authorizationController;
    private String educatorToken;
    private String studentToken1;
    private String studentToken2;
    private String teamKeyword;

    @Test
    @Order(1)
    public void contextLoads() {
        Assertions.assertNotNull(tournamentManagementController);
        Assertions.assertNotNull(authorizationController);
    }

    @BeforeAll
    public void registrationAndLogin() {
        RegisterDTO registerDTO = new RegisterDTO("createBattle@gmail.com", "test", "test", "createBattle", "TEST", UserRole.EDUCATOR);
        ResponseEntity<?> registerResponse = this.authorizationController.register(registerDTO);
        String responseBody = (String) registerResponse.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", responseBody);

        ResponseEntity<?> loginResponse = this.authorizationController.login(new LoginDTO("createBattle", "TEST"));

        UserTokenDTO userTokenDTO = (UserTokenDTO) loginResponse.getBody();
        this.educatorToken = userTokenDTO.getToken();
        System.out.println(educatorToken);

        RegisterDTO registerDTO1 = new RegisterDTO("createTeamTest@gmail.com", "test", "test", "createTeam", "TEST", UserRole.STUDENT);
        ResponseEntity<?> registerResponse1 = this.authorizationController.register(registerDTO1);
        String registerResponseBody = (String) registerResponse1.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", registerResponseBody);

        ResponseEntity<?> loginResponse1 = this.authorizationController.login(new LoginDTO("createTeam", "TEST"));

        UserTokenDTO userTokenDTO1 = (UserTokenDTO) loginResponse1.getBody();
        this.studentToken1 = userTokenDTO1.getToken();
        System.out.println(studentToken1);

        RegisterDTO registerDTO2 = new RegisterDTO("joinTeamTest@gmail.com", "test", "test", "joinTeam", "TEST", UserRole.STUDENT);
        ResponseEntity<?> registerResponse2 = this.authorizationController.register(registerDTO2);
        String registerResponseBody1 = (String) registerResponse2.getBody();
        Assertions.assertEquals("Registration has been completed. Please login to start enjoying CKB", registerResponseBody1);

        ResponseEntity<?> loginResponse2 = this.authorizationController.login(new LoginDTO("joinTeam", "TEST"));

        UserTokenDTO userTokenDTO2 = (UserTokenDTO) loginResponse2.getBody();
        this.studentToken2 = userTokenDTO2.getToken();
        System.out.println(studentToken2);
    }

    @Test
    @Order(2)
    public void createBattle() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDateTime = now.plus(2, ChronoUnit.MINUTES);
        Date futureDate = java.sql.Timestamp.valueOf(futureDateTime);

        TournamentCreationDTO tournamentCreationDTO = new TournamentCreationDTO("createBattleTournament", futureDate, true);
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

        BattleCreationDTO battleCreationDTO = new BattleCreationDTO("battle", "createBattleTournament", battleRegistrationDate, battleSubmissionDate, codeKata, 4, 2);
        String jsonBody1 = objectMapper.writeValueAsString(battleCreationDTO);

        mockMvc.perform(post("/api/eduBattle/create")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("The battle has been created"))
                .andExpect(status().isOk());

        CloseConsolidationStageDTO closeConsolidationStageDTO = new CloseConsolidationStageDTO("createBattleTournament-battle", true);
        String jsonBody2 = objectMapper.writeValueAsString(closeConsolidationStageDTO);

        mockMvc.perform(post("/api/eduBattle/close")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody2))
                .andExpect(content().string("There was an error in closing the consolidation stage. The battle may still be ongoing or doesn't exist"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @Order(3)
    public void subscribe() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SubscribeTntDTO subscribeTntDTO = new SubscribeTntDTO("createBattleTournament", true);
        String jsonBody1 = objectMapper.writeValueAsString(subscribeTntDTO);

        mockMvc.perform(post("/api/studTnt/subscribe")
                        .header("Authorization", "Bearer "+ this.studentToken1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("User successfully subscribed to the tournament"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/studTnt/subscribe")
                        .header("Authorization", "Bearer "+ this.studentToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(content().string("User successfully subscribed to the tournament"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void createTeam() throws Exception {

        TeamCreationDTO teamCreationDTO = new TeamCreationDTO("TEAM", "createBattleTournament-battle");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(teamCreationDTO);

        MvcResult result = mockMvc.perform(post("/api/studTeam/create")
                        .header("Authorization", "Bearer " + this.studentToken1)
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
    @Order(5)
    public void joinTeam() throws Exception {

        JoinTeamDTO joinTeamDTO = new JoinTeamDTO(teamKeyword, "createBattleTournament-battle");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(joinTeamDTO);

        mockMvc.perform(post("/api/studTeam/join")
                        .header("Authorization", "Bearer "+ this.studentToken2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("User successfully joined the team"))
                .andExpect(status().isOk());

    }

    @Test
    @Order(6)
    public void evaluate() throws Exception {

        String[] usernames = new String[2];
        usernames[0] = "createTeam";
        usernames[1] = "joinTeam";

        int[] points = new int[2];
        points[0] = 20;
        points[1] = 15;
        EvaluateDTO evaluateDTO = new EvaluateDTO("createBattleTournament-battle", usernames, points);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(evaluateDTO);

        mockMvc.perform(post("/api/eduBattle/evaluate")
                        .header("Authorization", "Bearer "+ this.educatorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("Evaluation failed. The battle may not be in the consolidation stage"))
                .andExpect(status().isUnprocessableEntity());

    }

}
