package ingsw2.codekatabattle.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeywordResponse {

    private ServerResponse serverResponse;
    private String keyword;

    public KeywordResponse(ServerResponse serverResponse){
        this.serverResponse = serverResponse;
        this.keyword = null;
    }
}
