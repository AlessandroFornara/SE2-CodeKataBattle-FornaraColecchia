package ingsw2.codekatabattle.Controllers.EducatorController;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/eduBattle")
public class BattleManagementController {

    @PostMapping("/create")
    public ResponseEntity<?> createBattle(){
        return null;
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeConsolidationStage(){
        return null;
    }


    @PostMapping("/evaluate")
    public String evaluate(){
        return "";
    }

}
