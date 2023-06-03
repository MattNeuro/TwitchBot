package nl.fragfrog.tauntbot;

import nl.fragfrog.tauntbot.listeners.DisconnectListener;
import nl.fragfrog.tauntbot.listeners.PrefixListener;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;

import java.io.FileReader;
import java.io.IOException;

import java.util.Properties;
import nl.fragfrog.tauntbot.listeners.MessageListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
        

/**
 *
 * @author DrTauntsalot
 */
public class TauntBot {

    public      static final Logger       logger            = LogManager.getLogger(TauntBot.class);;
    public      static final Properties   configuration     = new Properties();
    private     static final String       CONFIG_FILE       = "configuration.properties";

    
    /**
     *  Attempt to start up our TauntBot.
     * 
     *  To launch, this will require certain conditions to be met:
     * 
     *  -   We need a present configuration file, with correct information.
     *  -   The Twitch network needs to be accessible.
     *  -   The listeners should be able to load the correct responses.
     * 
     *  If any of this fails, the bot will exit.
     * 
     *  @param args
     *  @throws IOException
     *  @throws InterruptedException 
     */
    public static void main (String[] args) throws IOException, InterruptedException {
        try {
            logger.info("Loading configuration...");
            configuration.load(new FileReader(CONFIG_FILE));
        } catch (IOException e) {
            logger.error("Failed to load configuration file. Make sure the file '" + CONFIG_FILE + "' exists and is readable.");
            System.exit(-1);
        }
        
        logger.info("Creating Twirk object");
        final Twirk twirk = new TwirkBuilder(configuration.getProperty("channel"),
                                             configuration.getProperty("nickname"),
                                             configuration.getProperty("passkey"))
                                                                .setVerboseMode(true)
                                                                .build();
        
        logger.info("Attaching IRC listeners");
        twirk.addIrcListener( new DisconnectListener(twirk) );      // Checks if the bot gets disconnected, then tries to reconnect.
        twirk.addIrcListener( new PrefixListener(twirk)     );      // Checks for special !commands.
        twirk.addIrcListener( new MessageListener(twirk)    );      // Periodically sends pre-defined messages to chat.
        
        logger.info("Connecting to Twitch network");
        twirk.connect();
    }
}
