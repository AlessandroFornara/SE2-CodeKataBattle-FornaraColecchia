package ingsw2.codekatabattle.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.DAO.BattleDAO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class GitHubService {

    private final String createRepoUrl = "https://api.github.com/user/repos";
    private final String uploadFileUrl = "https://api.github.com/repos/USER_NAME/REPO_NAME/contents/FILE_PATH";
    private final String accessToken = "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";
    private final String username = "AlessandroFornara";
    private final BattleDAO battleDAO;

    private String createRepository(String name) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{\"name\":\"" + name + "\", \"private\": false}";
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(createRepoUrl, HttpMethod.POST, request, String.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode root = mapper.readTree(response.getBody());
                String repoUrl = root.get("html_url").asText();
                log.info("GitHub Repository " + name + " has been successfully created. URL: " + repoUrl);
                return repoUrl;
            } catch (Exception e) {
                log.error("Failed to parse GitHub repository creation response: " + e.getMessage());
            }
        } else {
            log.error("There was an error in creating the GitHub repository " + name);
        }
        return null;
    }

    private void uploadFilesToGitHub(HashMap<String, String> files, String repoName, String s) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();

        for (Map.Entry<String, String> file : files.entrySet()) {
            String filePath = file.getKey();
            String content = file.getValue();
            String encodedContent = Base64.getEncoder().encodeToString(content.getBytes());

            String requestBody = "{\"message\": \" uploading CodeKata\", \"content\": \"" + encodedContent + "\"}";

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

            String apiUrlWithParams = uploadFileUrl
                    .replace("USER_NAME", username)
                    .replace("REPO_NAME", repoName)
                    .replace("FILE_PATH", filePath);

            ResponseEntity<String> response = restTemplate.exchange(apiUrlWithParams, HttpMethod.PUT, request, String.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("File " + filePath + " has been successfully uploaded!");
            } else {
                log.info("There was an error in uploading the file " + filePath);
            }
        }
    }

    @Async
    public void createRepositoryAndUploadFiles(String repoName, HashMap<String, String> files) {
        String repoUrl = createRepository(repoName);

        if (repoUrl != null && !repoUrl.isEmpty()) {
            uploadFilesToGitHub(files, repoName, repoUrl);
        } else {
            log.error("Failed to create repository " + repoName + ". Files were not uploaded.");
        }
        battleDAO.saveRepoLink(repoUrl, repoName);
        battleDAO.deleteInvalidTeams(repoName);
        //notificationService.notifyBattleStart();
    }

    public int computePoints(Date registrationDeadline, Date submitDate, List<String> educatorOutputs, List<String> outputs){

        int totalPoints = 0;
        int maxPoints = 0;

        for (int i = 0; i < outputs.size(); i++) {
            String output = outputs.get(i);
            String educatorOutput = educatorOutputs.get(i);

            maxPoints += Math.max(educatorOutput.length(), output.length());

            for (int j = 0; j < Math.min(educatorOutput.length(), output.length()); j++) {
                if (output.charAt(j) == educatorOutput.charAt(j)) {
                    totalPoints++;
                    System.out.println(totalPoints);
                }
            }

        }

        totalPoints = maxPoints > 0 ? (totalPoints * 80) / maxPoints : 0;

        long currentTime = new Date().getTime();
        long submitTime = submitDate.getTime();
        long registrationTime = registrationDeadline.getTime();

        double percentageTimeElapsed = (double)(currentTime - registrationTime) / (submitTime - registrationTime);

        int timelinessPoints = (int)(20 * (1 - percentageTimeElapsed));

        totalPoints += timelinessPoints;

        System.out.println(totalPoints);

        return Math.min(totalPoints, 100);
    }

}
