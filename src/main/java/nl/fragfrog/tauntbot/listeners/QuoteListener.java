package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import nl.fragfrog.tauntbot.TauntBot;

/**
 *
 * @author DrTauntsalot
 */
public class QuoteListener {
    
    
    private static  QuoteListener   ql;
    private         List<String>    quotes;
    private final   String          filename = "quotes.txt";
    
    
    protected QuoteListener () {
        this.quotes = new ArrayList<String>();
        this.loadQuotes();
    }
    
    
    /**
     *  Return a random quote from the list of quotes. This assumes there are
     *  some quotes. 
     * 
     *  @return A random quote entry from this.quotes list.
     */
    public static String getQuote () {
        if (ql == null)
            ql = new QuoteListener();
        
        if (ql.quotes.isEmpty())
            return "Unfortunately, there are no quotes (yet)!";
        
        return ql.quotes.get(new Random().nextInt(ql.quotes.size()));
    }
    

    public static String addQuote(TwitchUser sender, TwitchMessage message) {
        TauntBot.logger.info("Adding quote: " + message.getContent());
        if (ql == null)
            ql = new QuoteListener();
        
        String content = message.getContent();
        content = content.substring(9);  // remove the "!newQuote" portion

        content = content.trim(); // Trim whitespace
        ql.quotes.add(content);
        ql.storeQuotes();
        return "your quote was added!";
    }     
    
    
    /**
     *  Store quotes in the designated quotes.txt file
     * 
     *  Will warn the user if this fails. First attempt to create the file; if
     *  the file already exists, this will fail, and instead this will try to
     *  open the existing file. 
     * 
     *  Writes each quote in the current buffer plus a new-line character to
     *  the quotes file, then closes it. This is relatively expensive, so we
     *  should be careful this is not called too often. Ideally we would only
     *  call this at shutdown, but this risks losing all newly added quotes
     *  if the bot closes unexpectedly. Alternatively, we could try to only 
     *  add new quotes as they come in; this would complicate how we check for
     *  the files' existence though.
     * 
     *  This is probably fine. Probably.
     */
    protected void storeQuotes () {
        TauntBot.logger.info("Storing quotes");

        FileWriter writer;
        
        try {
            File quotesFile = new File(filename);
            if (quotesFile.createNewFile()) {
                writer = new FileWriter(quotesFile);
                TauntBot.logger.info("Created new file to store quotes.");
            } else {
                writer = new FileWriter(filename);
                TauntBot.logger.info("Opened quotes file to store quotes.");
            }
            
            for (String quote : quotes)
                writer.write(quote + System.lineSeparator());

            writer.close();
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }           
    }
    
    
    /**
     *  Load existing quotes from the [filename] file. This will probably fail
     *  if the file does not exist or is empty. In that case, just catch the
     *  exception, report on it, and shrug.
     */
    private void loadQuotes () {
        TauntBot.logger.info("Loading quotes");
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null)
                quotes.add(line);
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }
    }
}