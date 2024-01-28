package ingsw2.codekatabattle.Model;

public enum ServerResponse {

    BATTLE_CREATION_ERROR,
    BATTLE_ALREADY_EXISTS,
    BATTLE_DOESNT_EXIST,
    BATTLE_SUCCESSFULLY_CREATED,
    EMAIL_OR_USERNAME_NOT_AVAILABLE,
    INVALID_CREDENTIALS,
    PRIVATE_TOURNAMENT_CREATED,
    PUBLIC_TOURNAMENT_CREATED,
    REGISTRATION_DEADLINE_NOT_VALID,
    SUBMISSION_DEADLINE_NOT_VALID,
    TOURNAMENT_ALREADY_CLOSED,
    TOURNAMENT_ALREADY_EXISTS,
    TOURNAMENT_DOESNT_EXIST,
    TOURNAMENT_SUCCESSFULLY_SAVED,
    UNSUCCESSFUL_UPDATE,
    USER_IS_NOT_REGISTERED,
    USER_IS_NOT_ADMIN,
    USER_REGISTRATION_OK,
    USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT,
    USER_IS_ALREADY_MODERATOR,
    USER_IS_ALREADY_ADMIN,
    USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR,
    USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED;


    public static String toString(ServerResponse response){
        switch(response){
            case BATTLE_CREATION_ERROR -> {return "There was an error in creating the battle. " +
                    "User might not be allowed to create battles in this tournament or the tournament is already closed. " +
                    "Make sure to also check the validity of deadlines:\n" +
                    "\t-The registration deadline of the battle must be after tournament one\n" +
                    "\t-All battles must be sequential, so check that the registration deadline isn't before " +
                    "the submission deadline of another battle";}
            case BATTLE_ALREADY_EXISTS -> {return "A battle with that name already exists in this tournament";}
            case BATTLE_DOESNT_EXIST -> {return "The battle doesn't exist or you don't have access to it. \n" +
                    "Make sure that you're subscribed to the battle and the tournament it belongs to";}
            case BATTLE_SUCCESSFULLY_CREATED -> {return "The battle has been created";}
            case EMAIL_OR_USERNAME_NOT_AVAILABLE -> {return "The provided email or username is already registered in our system";}
            case INVALID_CREDENTIALS -> {return "Invalid username or password";}
            case PRIVATE_TOURNAMENT_CREATED -> {return "The PRIVATE tournament has been created";}
            case PUBLIC_TOURNAMENT_CREATED -> {return "The PUBLIC tournament has been created";}
            case REGISTRATION_DEADLINE_NOT_VALID -> {return "Registration deadline not valid";}
            case SUBMISSION_DEADLINE_NOT_VALID -> {return "Submission deadline not valid";}
            case TOURNAMENT_ALREADY_CLOSED -> {return "The tournament has already been closed";}
            case TOURNAMENT_ALREADY_EXISTS -> {return "A tournament with that name already exists";}
            case TOURNAMENT_DOESNT_EXIST -> {return "The tournament doesn't exist";}
            case TOURNAMENT_SUCCESSFULLY_SAVED -> {return "The tournament has been successfully saved";}
            case UNSUCCESSFUL_UPDATE -> {return "Unsuccessful update";}
            case USER_IS_NOT_REGISTERED -> {return "Error: User is not registered";}
            case USER_IS_NOT_ADMIN -> {return "User is not the admin of the tournament";}
            case USER_REGISTRATION_OK -> {return "Registration has been completed. Please login to start enjoying CKB";}
            case USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT -> {return "User successfully subscribed to the tournament";}
            case USER_IS_ALREADY_MODERATOR -> {return "User is already Moderator of the tournament";}
            case USER_IS_ALREADY_ADMIN -> {return "User is already Admin of the tournament";}
            case USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR -> {return "User was successfully promoted to Moderator of the tournament";}
            case USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED -> {return "User is already subscribed to the tournament or the registration deadline has expired";}

            default -> {return "An unexpected error has occurred";}
        }
    }
}
