package ingsw2.codekatabattle.Entities;

import ingsw2.codekatabattle.Entities.States.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "User")
@NoArgsConstructor
@Getter
public class User {

    @Indexed(unique = true)
    private String email;
    private String name;
    private String surname;
    @MongoId
    private String username;
    private String password;
    private UserRole role;

    public User(String email, String name, String surname, String username, String password, UserRole role) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
