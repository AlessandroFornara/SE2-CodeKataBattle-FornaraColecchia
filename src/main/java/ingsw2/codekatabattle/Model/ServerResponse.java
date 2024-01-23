package ingsw2.codekatabattle.Model;

public enum ServerResponse {

    GENERIC_ERROR,
    PRIVATE_TOURNAMENT_CREATED,
    PUBLIC_TOURNAMENT_CREATED,
    REGISTRATION_DEADLINE_NOT_VALID,
    TOURNAMENT_ALREADY_EXISTS,
    TOURNAMENT_SUCCESSFULLY_SAVED;

    public static String toString(ServerResponse response){
        switch(response){
            case GENERIC_ERROR -> {return "Generic internal server error";}
            case PRIVATE_TOURNAMENT_CREATED -> {return "The PRIVATE tournament has been created";}
            case PUBLIC_TOURNAMENT_CREATED -> {return "The PUBLIC tournament has been created";}
            case REGISTRATION_DEADLINE_NOT_VALID -> {return "Registration deadline not valid";}
            case TOURNAMENT_ALREADY_EXISTS -> {return "A tournament with that name already exists";}
            case TOURNAMENT_SUCCESSFULLY_SAVED -> {return "The tournament has been successfully saved";}

            default -> {return "An unexpected error has occurred";}
        }
    }
}
