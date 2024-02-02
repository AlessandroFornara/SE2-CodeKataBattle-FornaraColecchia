package ingsw2.codekatabattle.Services;

import ingsw2.codekatabattle.DAO.BattleDAO;
import ingsw2.codekatabattle.DAO.TournamentDAO;
import ingsw2.codekatabattle.DAO.UserDAO;
import ingsw2.codekatabattle.Entities.Battle;
import ingsw2.codekatabattle.Entities.States.TournamentVisibility;
import ingsw2.codekatabattle.Entities.Team;
import ingsw2.codekatabattle.Entities.Tournament;
import ingsw2.codekatabattle.Entities.User;
import ingsw2.codekatabattle.Model.UserDTOS.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {

    private final JavaMailSender emailSender;
    private final UserDAO userDAO;
    private final BattleDAO battleDAO;
    private final TournamentDAO tournamentDAO;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        //message.setFrom("noreply@codekatabattle.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Async
    public void notifyAllStudents(String tournamentName) {

        List<UserDTO> students = userDAO.getAllStudents();

        for(UserDTO u : students)
            sendSimpleMessage(u.getEmail(), "NEW TOURNAMENT", "A new PUBLIC tournament has been created: " +
                    tournamentName + "\nLog on the CKB platform to subscribe before the registration deadline expires!");

    }

    @Async
    public void notifyUpcomingBattles(String username, String tournamentNameOrKeyword, TournamentVisibility visibility){
        
        User user = userDAO.getUserByUsername(username);
        List<Tournament> tournaments = tournamentDAO.getTntByNameOrKeyword(tournamentNameOrKeyword, visibility);
        String name = tournaments.get(0).getName();
        List<Battle> battles = battleDAO.getTntBattles(name);
        String message = "";
        boolean flag = false;
        for(Battle b: battles){
            message += b.getName() +"\n";
            flag = true;
        }
        System.out.println(message.equals(" "));
        sendSimpleMessage(user.getEmail(), "UPCOMING BATTLES", flag ? "The tournament " + name + " has the following upcoming battles:\n"
                + message : "The tournament " + name + " doesn't have any upcoming battle yet." );
        
    }

    @Async
    public void notifyNewBattle(String battleName){
        int index = battleName.indexOf("-");
        List<Tournament> tournaments = tournamentDAO.getTntByNameOrKeyword(battleName.substring(0, index), TournamentVisibility.PUBLIC);

        for(String s: tournaments.get(0).getRank().keySet()){
            sendSimpleMessage(userDAO.getUserByUsername(s).getEmail(), "NEW BATTLE", "A new battle has been created in the tournament " + battleName.substring(0, index));}
    }

    @Async
    public void notifyNewModerator(String username, String tournamentName){
        User user = userDAO.getUserByUsername(username);
        sendSimpleMessage(user.getEmail(), "NEW PROMOTION", "You have been promoted to Moderator " +
                "in the tournament " + tournamentName);
    }

    @Async
    public void notifyBattleStart(String battleName, String repoLink){

        Battle battle = battleDAO.getBattleInfo(battleName);

        for(Team t: battle.getTeams()){
            for(String s: t.getMembers()){
                sendSimpleMessage(userDAO.getUserByUsername(s).getEmail(), "BATTLE IS STARTING", "The battle" + battleName + " is starting\n"
                + "You can find the Code Kata at the following link: " + repoLink);
            }
        }
    }

    @Async
    public void notifySubscribedStudents(String tournamentName){

        List<Tournament> tournament = tournamentDAO.getTntByNameOrKeyword(tournamentName, TournamentVisibility.PUBLIC);

        for(String s: tournament.get(0).getRank().keySet()){
            sendSimpleMessage(userDAO.getUserByUsername(s).getEmail(), "TOURNAMENT HAS BEEN CLOSED", "The tournament" + tournamentName + " has been closed\n"
                    + "Log on the CKB platform and check the section 'My Tournaments' to see the final rank!");
        }
    }

    @Async
    public void notifyCloseConsStage(String battleName){
        Battle battle = battleDAO.getBattleInfo(battleName);

        for(Team t: battle.getTeams()){
            for(String s: t.getMembers()){
                sendSimpleMessage(userDAO.getUserByUsername(s).getEmail(), "CONSOLIDATION STAGE HAS BEEN CLOSED", "The consolidation stage for battle"
                        + battleName + " has been closed\n"
                        + "Log on the CKB platform to see the battle final rank and the update tournament rank!");
            }
        }
    }

}
