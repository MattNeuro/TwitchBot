package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.io.PrintWriter;
import java.net.Socket;
import nl.fragfrog.tauntbot.TauntBot;

/**
 *
 * @author DrTauntsalot
 */
public class WoWListener {
    
    
    private static      WoWListener     wl;
    private int         guid;
    private Socket      commandSocket   = null;
    private PrintWriter out             = null;
    
    
    protected WoWListener () {
        try {
            String WoWIP    = TauntBot.configuration.getProperty("wowIP");
            commandSocket   = new Socket(WoWIP, 8888);
            out             = new PrintWriter(commandSocket.getOutputStream(), true);
        } catch (Exception e) {
        }
    }

    
    public static void sayInWoW (String message) {
        if (wl == null)
            return;
        
        message = message.replace(",", ""); // Cannot use commas unfortunately.
        wl.out.println("say " + message + "," + wl.guid);
    }
    

    public static String setBotGuid(TwitchUser sender, TwitchMessage message) {
        TauntBot.logger.info("Setting WoW bot GUID: " + message.getContent());
        String content = message.getContent();
        content = content.substring(9);  // remove the "!linkwow" portion

        // Always reset the listener, since we likely need to reconnect to the server.
        wl      = new WoWListener();        
        wl.guid = Integer.parseInt(content.trim());
        return "Update bot GUID to " + content.trim();
    }
}