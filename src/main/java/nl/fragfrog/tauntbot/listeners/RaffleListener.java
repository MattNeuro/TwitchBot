package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.util.HashMap;
import java.util.Random;
import nl.fragfrog.tauntbot.TauntBot;

/**
 *
 * @author DrTauntsalot
 */
public class RaffleListener {
    
    
    private static RaffleListener   rl;
    private static HashMap<Long, TwitchUser> users;
        
    
    protected RaffleListener () {
        this.users = new HashMap<>();
        users.clear();
    }
    

    
    public static String giveaway (TwitchUser sender, TwitchMessage message) {
        if (rl == null)
            rl = new RaffleListener();        
        
        String[] text = message.getContent().trim().split(" ");
        
        if (text.length == 1)
            return signup(sender);
        if (text[1].equalsIgnoreCase("draw") && sender.getUserType().value >= USER_TYPE.MOD.value )
            return draw();
        if (text[1].equalsIgnoreCase("clear") && sender.getUserType().value >= USER_TYPE.MOD.value )
            return clear();
        
        return "Unknown giveaway command.";
    }
    
    
    private static String draw () {
        TauntBot.logger.error("Drawing a winner from " + users.size() + " participants");
        
        Object[] values = users.values().toArray();
        TwitchUser winner = (TwitchUser) values[new Random().nextInt(values.length)];
        return "The winner is " + winner.getDisplayName() + ", congratulations!";
    }
    
    
    private static String clear () {
        users.clear();
        return "The giveaway has been reset.";
    }
    
    
    private static String signup (TwitchUser sender) {
        long userId = sender.getUserID();
        
        if (users.containsKey(userId))
            return "you already signed up!";

        users.put(userId, sender);
        return "you signed up for the giveaway!";
    }
}