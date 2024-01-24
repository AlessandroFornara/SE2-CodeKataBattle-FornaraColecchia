package ingsw2.codekatabattle.Controllers.GitHubController;

import ingsw2.codekatabattle.Services.GitHubService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/GitHub")
@AllArgsConstructor
public class GitHubManager {

    private final GitHubService gitHubService;

    //TODO: Just for testing
    @PostMapping("/createRepo")
    public void createRepositoryAndUploadFiles(String newRepoName) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("test.txt", "CIAO");
        hashMap.put("test2.txt", "CIAOOO");
        gitHubService.createRepositoryAndUploadFiles(newRepoName, hashMap);
    }

    @PostMapping("/automatedEvaluation")
    public void updateScore(){

    }

}
