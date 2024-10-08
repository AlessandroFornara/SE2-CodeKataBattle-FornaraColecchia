package ingsw2.codekatabattle.Entities;

import ingsw2.codekatabattle.Entities.States.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

/**
 * This class is the representation of the User database entity.
 * @Document(collection = "User") - This annotation specifies that this entity is stored in the "User" collection in MongoDB.
 */
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

    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
