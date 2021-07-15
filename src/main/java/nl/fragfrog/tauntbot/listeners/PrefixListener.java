package nl.fragfrog.tauntbot.listeners;

import nl.fragfrog.tauntbot.TauntBot;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

import java.io.BufferedReader;
import java.util.TreeMap;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

/**
 *  Handle prefix commands
 * 
 *  Prefix commands search for a query at the start of a message only. A text
 *  response is then send in return to the person originally sending the request.
 * 
 *  @author DrTauntsalot
 */
public class PrefixListener extends CommandBase {

    private final Twirk twirk;

    public PrefixListener(Twirk twirk) {
        super(CommandType.PREFIX_COMMAND);
        this.twirk = twirk;
    }


    @Override
    protected void performCommand(String command, TwitchUser sender, TwitchMessage message) {
        TauntBot.logger.info("Looking for command: " + command);
        String name = sender.getDisplayName();
        
        if (command.equalsIgnoreCase("!commands"))
            twirk.channelMessage(name + " you can use the following commands: " + this.getCommands());
        else if (command.equalsIgnoreCase("!newquote"))
            twirk.channelMessage(name + ": " + QuoteListener.addQuote(sender, message));
        else if (command.equalsIgnoreCase("!quote"))
            twirk.channelMessage(name + ": " + QuoteListener.getQuote());
        else if (commands.containsKey(command.toLowerCase()))
            twirk.channelMessage(name + ": " + commands.get(command.toLowerCase()));
    }
    
    
    @Override
    protected void loadCommands () {
        TauntBot.logger.info("Loading prefix commands");
        if (this.commands == null)
            this.commands = new TreeMap();

        String line;
        String[] fields;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("responses.csv"));
            while ((line = reader.readLine()) != null) {
                fields = line.split(",");
                if (fields.length > 1)
                    commands.put(fields[0].toLowerCase(), fields[1]);
            }
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }
    }

    
    /**
     *  Returns a list of available commands.
     */
    private String getCommands () {
        Set commandKeys     = commands.keySet();
        return commandKeys.toString();
    }
    
    
    @Override
    protected USER_TYPE getMinUserPrevilidge() {
        return USER_TYPE.DEFAULT;
    }
}