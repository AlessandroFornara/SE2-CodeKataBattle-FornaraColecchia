package ingsw2.codekatabattle;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Model.UserDTOS.LoginDTO;
import ingsw2.codekatabattle.Model.UserDTOS.RegisterDTO;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
public class AuthorizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void registrationAndLogin() throws Exception {
        RegisterDTO registerDTO = new RegisterDTO("authorizationTest@gmail.com", "test", "test", "authorizationTest", "TEST", UserRole.EDUCATOR);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(registerDTO);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(content().string("Registration has been completed. Please login to start enjoying CKB"))
                .andExpect(status().isOk());

        LoginDTO loginDTO = new LoginDTO("authorizationTest", "TEST");
        String jsonBody1 = objectMapper.writeValueAsString(loginDTO);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody1))
                .andExpect(status().isOk())
                .andReturn();

    }
}
