package ingsw2.codekatabattle.Model;

public enum ServerResponse {

    AUTOMATED_EVALUATION_OK,
    BATTLE_CREATION_ERROR,
    BATTLE_ALREADY_EXISTS,
    BATTLE_DOESNT_EXIST,
    BATTLE_SUCCESSFULLY_CREATED,
    EMAIL_OR_USERNAME_NOT_AVAILABLE,
    GENERIC_ERROR,
    INVALID_CREDENTIALS,
    PRIVATE_TOURNAMENT_CREATED,
    PUBLIC_TOURNAMENT_CREATED,
    REGISTRATION_DEADLINE_NOT_VALID,
    SUBMISSION_DEADLINE_NOT_VALID,
    TEAM_NAME_NOT_AVAILABLE,
    TEAM_SUCCESSFULLY_CREATED,
    TOURNAMENT_ALREADY_CLOSED,
    TOURNAMENT_ALREADY_EXISTS,
    TOURNAMENT_BATTLES_IN_PROGRESS,
    TOURNAMENT_CLOSED_OK,
    TOURNAMENT_DOESNT_EXIST,
    TOURNAMENT_SUCCESSFULLY_SAVED,
    TOURNAMENT_IS_ALREADY_CLOSED_OR_USER_NOT_ADMIN,
    UNSUCCESSFUL_UPDATE,
    UNSUCCESSFUL_INSERT,
    USER_IS_NOT_REGISTERED,
    USER_IS_NOT_ADMIN,
    USER_REGISTRATION_OK,
    USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT,
    USER_IS_ALREADY_MODERATOR,
    USER_IS_ALREADY_ADMIN,
    USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR,
    USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED,
    USER_IS_NOT_SUBSCRIBED_TO_TOURNAMENT,
    JOIN_TEAM_SUCCESS,
    JOIN_TEAM_FAILED,
    EVALUATION_SUCCESSFUL,
    EVALUATION_FAILED,
    CONS_STAGE_CLOSED_SUCCESSFULLY,
    CONS_STAGE_CLOSED_FAILED;

    public static String toString(ServerResponse response){
        switch(response){
            case AUTOMATED_EVALUATION_OK -> {return "Automated evaluation successful";}
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
            case GENERIC_ERROR -> {return "Generic internal server error";}
            case INVALID_CREDENTIALS -> {return "Invalid username or password";}
            case PRIVATE_TOURNAMENT_CREATED -> {return "The PRIVATE tournament has been created";}
            case PUBLIC_TOURNAMENT_CREATED -> {return "The PUBLIC tournament has been created";}
            case REGISTRATION_DEADLINE_NOT_VALID -> {return "Registration deadline not valid";}
            case SUBMISSION_DEADLINE_NOT_VALID -> {return "Submission deadline not valid";}
            case TEAM_NAME_NOT_AVAILABLE -> {return "Team name is not available or user is already in another team or the registration deadline of the battle has expired";}
            case TEAM_SUCCESSFULLY_CREATED -> {return "Team has been successfully created";}
            case TOURNAMENT_ALREADY_CLOSED -> {return "The tournament has already been closed";}
            case TOURNAMENT_ALREADY_EXISTS -> {return "A tournament with that name already exists";}
            case TOURNAMENT_BATTLES_IN_PROGRESS -> {return "The tournament has still one or more battles in progress";}
            case TOURNAMENT_CLOSED_OK -> {return "The tournament has been successfully closed";}
            case TOURNAMENT_DOESNT_EXIST -> {return "The tournament doesn't exist";}
            case TOURNAMENT_SUCCESSFULLY_SAVED -> {return "The tournament has been successfully saved";}
            case TOURNAMENT_IS_ALREADY_CLOSED_OR_USER_NOT_ADMIN -> {return "The tournament is already closed or you're not the admin";}
            case UNSUCCESSFUL_UPDATE -> {return "Unsuccessful update";}
            case UNSUCCESSFUL_INSERT -> {return "Unsuccessful insert";}
            case USER_IS_NOT_REGISTERED -> {return "Error: User is not registered";}
            case USER_IS_NOT_ADMIN -> {return "User is not the admin of the tournament";}
            case USER_REGISTRATION_OK -> {return "Registration has been completed. Please login to start enjoying CKB";}
            case USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT -> {return "User successfully subscribed to the tournament";}
            case USER_IS_ALREADY_MODERATOR -> {return "User is already Moderator of the tournament";}
            case USER_IS_ALREADY_ADMIN -> {return "User is already Admin of the tournament";}
            case USER_SUCCESSFULLY_PROMOTED_TO_MODERATOR -> {return "User was successfully promoted to Moderator of the tournament";}
            case USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED -> {return "User is already subscribed to the tournament or the registration deadline has expired";}
            case USER_IS_NOT_SUBSCRIBED_TO_TOURNAMENT -> {return "User is not subscribed to the tournament";}
            case JOIN_TEAM_FAILED -> {return "User is already part of another team or the team is full or the registration deadline of the battle has expired";}
            case JOIN_TEAM_SUCCESS -> {return "User successfully joined the team";}
            case EVALUATION_SUCCESSFUL -> {return "Evaluation Successful";}
            case EVALUATION_FAILED -> {return "Evaluation failed. The battle may not be in the consolidation stage";}
            case CONS_STAGE_CLOSED_SUCCESSFULLY -> {return "The consolidation stage for this battle was closed successfully";}
            case CONS_STAGE_CLOSED_FAILED -> {return "There was an error in closing the consolidation stage. The battle may still be ongoing or doesn't exist";}

            default -> {return "An unexpected error has occurred";}
        }
    }
}
