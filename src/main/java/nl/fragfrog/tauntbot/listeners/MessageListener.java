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
public class MessageListener extends TauntBot implements TwirkListener {
    
    private final   Twirk               twirk;    
    private final   String              filename        = "adds.txt";    
    private final   List<String>        responses       = new ArrayList();
    private final   int                 interval;
    private         int                 responseIndex   = 0;
    private         int                 messageCount    = 0;
    

    public MessageListener(Twirk twirk) {
        this.interval   = Integer.parseInt(TauntBot.configuration.getProperty("addInterval"));
        this.twirk      = twirk;
        loadResponses();
    }
    
    
    /**
     *  Every time a message comes in this will increase the counter of messages
     *  received. Once this hits the "addInterval" configuration value, one of the
     *  advertisements (pre-determined messages) is posted in chat.
     *  
     *  If there are no advertisements listed, this will simply return without 
     *  posting a message. Otherwise, it will display the current add and increment
     *  the response counter to send the next response the next time this limit 
     *  is hit.
     *      
     *  @param sender   The entity who send the last message. Not used.
     *  @param message  The last message received. Not used.
     */
    @Override
    public void onPrivMsg( TwitchUser sender, TwitchMessage message) {
        messageCount++;
        if (responseIndex >= responses.size())                      // If we reached the last add, reset from the beginning.
            responseIndex = 0;

        if (messageCount < interval)                                // Only act once every [addInterval] messages
            return;
        
        if (responses.isEmpty())                                    // If there are no recorded adds, skip.
            return;
            
        TauntBot.logger.info("Received " + messageCount + " messages since last action. Acting.");
        twirk.channelMessage(responses.get(responseIndex));         // Send a message to chat.
        responseIndex++;                                            // Prepare the next response.
        messageCount = 0;                                           // Reset the counter.
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