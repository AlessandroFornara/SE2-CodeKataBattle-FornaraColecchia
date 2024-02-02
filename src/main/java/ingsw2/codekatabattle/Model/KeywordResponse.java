package ingsw2.codekatabattle.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Model class representing a response that includes a keyword and a server response status.
 */
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
