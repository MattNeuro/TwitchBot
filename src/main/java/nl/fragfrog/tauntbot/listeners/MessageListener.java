package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.fragfrog.tauntbot.TauntBot;


/**
 *  The message listener keeps track of how many messages are being send.
 * 
 *  This can then perform actions every N messages, such as advertising
 *  the discord server or alike.
 *  
 *  @author DrTauntsalot
 */
public class MessageListener implements TwirkListener {
    
    private final   Twirk               twirk;    
    private final   String              filename        = "adds.txt";    
    private final   List<String>        responses       = new ArrayList();
    private final   int                 interval;
    private         int                 responseIndex   = 0;
    private         int                 messageCount    = 0;
    

    public MessageListener(Twirk twirk, int interval) {
        this.twirk      = twirk;
        this.interval   = interval;
        loadResponses();
    }
    
    
    @Override
    public void onPrivMsg( TwitchUser sender, TwitchMessage message) {
        messageCount++;
        responseIndex++;
        if (responseIndex >= responses.size())
            responseIndex = 0;

        if (messageCount < interval)
            return;
        
        if (responses.isEmpty())
            return;
            
        TauntBot.logger.info("Received " + messageCount + " messages since last action. Acting.");
        twirk.channelMessage(responses.get(responseIndex));
        messageCount = 0;
    }    
    

    /**
     *  Load responses from the [filename] file. This will probably fail
     *  if the file does not exist or is empty. In that case, just catch the
     *  exception, report on it, and shrug.
     */
    private void loadResponses () {
        TauntBot.logger.info("Loading random message responses.");
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null)
                responses.add(line);
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }
    }
}