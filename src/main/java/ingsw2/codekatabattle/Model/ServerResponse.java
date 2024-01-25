package ingsw2.codekatabattle.Model;

public enum ServerResponse {

    EMAIL_OR_USERNAME_NOT_AVAILABLE,
    INVALID_CREDENTIALS,
    PRIVATE_TOURNAMENT_CREATED,
    PUBLIC_TOURNAMENT_CREATED,
    REGISTRATION_DEADLINE_NOT_VALID,
    TOURNAMENT_ALREADY_EXISTS,
    TOURNAMENT_SUCCESSFULLY_SAVED,
    UNSUCCESSFUL_UPDATE,
    USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT,
    USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED,
    USER_REGISTRATION_OK;

    public static String toString(ServerResponse response){
        switch(response){


            case EMAIL_OR_USERNAME_NOT_AVAILABLE -> {return "The provided email or username is already registered in our system";}
            case INVALID_CREDENTIALS -> {return "Invalid username or password";}
            case PRIVATE_TOURNAMENT_CREATED -> {return "The PRIVATE tournament has been created";}
            case PUBLIC_TOURNAMENT_CREATED -> {return "The PUBLIC tournament has been created";}
            case REGISTRATION_DEADLINE_NOT_VALID -> {return "Registration deadline not valid";}
            case TOURNAMENT_ALREADY_EXISTS -> {return "A tournament with that name already exists";}
            case TOURNAMENT_SUCCESSFULLY_SAVED -> {return "The tournament has been successfully saved";}
            case UNSUCCESSFUL_UPDATE -> {return "Unsuccessful update";}
            case USER_SUCCESSFULLY_SUBSCRIBED_TO_TOURNAMENT -> {return "User successfully subscribed to the tournament";}
            case USER_ALREADY_SUBSCRIBED_OR_REG_DEADLINE_EXPIRED -> {return "User is already subscribed to the tournament or the registration deadline has expired";}
            case USER_REGISTRATION_OK -> {return "Registration has been completed. Please login to start enjoying CKB";}

            default -> {return "An unexpected error has occurred";}
        }
    }
}
