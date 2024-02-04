package ingsw2.codekatabattle.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.Entities.Battle;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.IntStream;

/**
 * Service class for handling GitHub related operations in the Code Kata Battle application.
 * This service includes methods for creating GitHub repositories, uploading files to these repositories, and computing points based on submissions.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GitHubService {

    private final String createRepoUrl = "https://api.github.com/user/repos";
    private final String uploadFileUrl = "https://api.github.com/repos/USER_NAME/REPO_NAME/contents/FILE_PATH";
    @Value("${gitHub.accessToken}")
    private String accessToken;
    @Value("${gitHub.username}")
    private String username;
    private final BattleDAO battleDAO;
    private final NotificationService notificationService;
    private final TaskScheduler taskScheduler;

    @PostConstruct
    public void printTokenAndUsername(){
        System.out.println(this.accessToken);
        System.out.println(this.username);
    }

    /**
     * Schedules creation of GitHub repositories associated to battles already present in the database
     */
    @PostConstruct
    public void scheduleBattles(){
        List<Battle> battles = battleDAO.getAllRegistrationBattles();

        for (Battle b: battles) {
            HashMap<String, String> files = new HashMap<>();
            IntStream.range(0, b.getCodeKata().getInput().size())
                    .forEach(i -> files.put("input" + (i + 1) + ".txt", b.getCodeKata().getInput().get(i)));

            IntStream.range(0, b.getCodeKata().getOutput().size())
                    .forEach(i -> files.put("output" + (i + 1) + ".txt", b.getCodeKata().getOutput().get(i)));

            files.put("description.txt", b.getCodeKata().getDescription());
            files.put("configuration.yaml", b.getCodeKata().getConfigurationFile());
            taskScheduler.schedule(() -> createRepositoryAndUploadFiles(b.getName(), files), b.getRegistrationDeadline());
            System.out.println("Scheduled creation of " + b.getName());
        }
    }

    /**
     * Creates a new GitHub repository with the specified name.
     * The repository is created as a private repository under the configured GitHub account.
     * @param name The name of the GitHub repository to be created.
     * @return The URL of the newly created repository or null if the creation failed.
     */
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

    /**
     * Uploads a set of files to a specified GitHub repository.
     * This method takes a map of filenames to their contents and uploads each to the GitHub repository.
     * @param files A HashMap containing the filenames and their respective content to be uploaded.
     * @param repoName The name of the repository where the files will be uploaded.
     */
    private void uploadFilesToGitHub(HashMap<String, String> files, String repoName) {
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

    /**
     * Asynchronously creates a GitHub repository and uploads provided files to it.
     * After creating the repository and uploading the files, it updates the battle's information with the repository link and sends a notification.
     * @param repoName The name of the repository to be created.
     * @param files A HashMap of filenames and their contents to be uploaded to the repository.
     */
    @Async
    public void createRepositoryAndUploadFiles(String repoName, HashMap<String, String> files) {
        String repoUrl = createRepository(repoName);

        if (repoUrl != null && !repoUrl.isEmpty()) {
            uploadFilesToGitHub(files, repoName);
        } else {
            log.error("Failed to create repository " + repoName + ". Files were not uploaded.");
        }
        battleDAO.saveRepoLink(repoUrl, repoName);
        battleDAO.deleteInvalidTeams(repoName);
        notificationService.notifyBattleStart(repoName, repoUrl);
    }

    /**
     * Computes the points for a battle submission.
     * The method compares the submitted outputs with the expected outputs and calculates points based on accuracy and timeliness.
     * @param registrationDeadline The deadline for registration for the battle.
     * @param submitDate The deadline for submitting the solution.
     * @param educatorOutputs The expected outputs provided by the educator.
     * @param outputs The outputs submitted by the participants.
     * @return The calculated points for the submission, capped at 100.
     */
    public int computePoints(Date registrationDeadline, Date submitDate, List<String> educatorOutputs, List<String> outputs){

        int totalPoints = 0;
        int maxPoints = 0;

        for (int i = 0; i < outputs.size(); i++) {
            String output = normalizeLineEndings(outputs.get(i));
            String educatorOutput = normalizeLineEndings(educatorOutputs.get(i));

            maxPoints += Math.max(educatorOutput.length(), output.length());

            for (int j = 0; j < Math.min(educatorOutput.length(), output.length()); j++) {
                if (output.charAt(j) == educatorOutput.charAt(j)) {
                    totalPoints++;
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

    private String normalizeLineEndings(String text) {
        return text.replace("\r\n", "\n").replace("\r", "\n");
    }

}
