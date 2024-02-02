package ingsw2.codekatabattle.DAO;

import ingsw2.codekatabattle.Entities.States.UserRole;
import ingsw2.codekatabattle.Entities.User;
import ingsw2.codekatabattle.Model.ServerResponse;
import ingsw2.codekatabattle.Model.UserDTOS.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserDAO {

    private final MongoOperations mongoOperations;
    private final String collectionName = "User";

    public User getUserByUsername(String username){

        Query q = new Query();
        q.addCriteria(Criteria.where("_id").is(username));
        List<User> result = mongoOperations.find(q, User.class, collectionName);

        if(result.isEmpty()){
            return null;
        } else {
            return result.get(0);
        }
    }
    public ServerResponse registerUser(User user){

        try {
            mongoOperations.insert(user, collectionName);
            return ServerResponse.USER_REGISTRATION_OK;
        }catch (Exception e){
            return ServerResponse.EMAIL_OR_USERNAME_NOT_AVAILABLE;
        }
    }

    public List<UserDTO> getAllStudents(){
        Query q = new Query(Criteria.where("role").is(UserRole.STUDENT.toString()));
        return mongoOperations.find(q, UserDTO.class, collectionName);
    }

}
