package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.events.TwirkListener;
import java.io.IOException;

/**
 *  Listen for disconnects. Try to reconnect if possible.
 * 
 *  @author DrTauntsalot
 */
public class DisconnectListener implements TwirkListener {
    
    private static Twirk twirk;

    
    public DisconnectListener (Twirk twirk) {
        this.twirk = twirk;
    }
    
    
    /**
     *      Twitch might sometimes disconnects us from chat. 
     * 
     *      If that happens, try to reconnect. If that fails, give up.
     */
    @Override
    public void onDisconnect() {
        try { 
            if( !twirk.connect() )
                twirk.close();
        } 
        catch (IOException e) { 
            twirk.close(); 
        } 
        catch (InterruptedException ignored) {  }
    }
}
