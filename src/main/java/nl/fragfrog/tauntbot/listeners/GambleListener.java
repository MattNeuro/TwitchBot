package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import nl.fragfrog.tauntbot.TauntBot;

/**
 *
 * @author DrTauntsalot
 */
public class GambleListener {
    
    
    private static GambleListener               gl;
    private static Twirk                        twirk;
    private static HashMap<Long, TwitchUser>    users;
    private static TreeMap<Long, Integer>       scores;
    private static int                          messageCount;
    
    private final String                        fileName = "fridgeScores.csv";
        
    
    protected GambleListener () {
        TauntBot.logger.info("Creating Gamble Listener");
        this.users          = new HashMap<>();
        this.scores         = new TreeMap<>();
        this.messageCount   = 0;
        users.clear();
        scores.clear();
        loadScores();
    }
    
    
    /**
     *  Try to load gambling scores from file.
     * 
     *  This should only be used during start-up.
     */
    private void loadScores () {
        TauntBot.logger.info("Loading gambling scores.");
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                String[] entry  = line.trim().split(",");
                Long userId     = Long.parseLong(entry[0]);
                int score       = Integer.parseInt(entry[1]);
                scores.put(userId, score);
            }
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }        
    }
    
    
    /**
     *  Store gambling scores.
     * 
     *  For simplicity reasons, this only scores an array of userIds -> scores
     *  This *should* be enough to trace scores back to users, once they actually
     *  start to gamble again, without having to store TwitchUser objects.
     * 
     *  Attempts to create a gambling scores file, or open it if it does not
     *  exist. Also resets the messageCount, so we only perform this relatively
     *  heavy operation once every X messages.
     */
    private void storeScores () {
        TauntBot.logger.info("Storing gambling scores");        
        GambleListener.messageCount = 0;
        
        FileWriter writer;
        
        try {
            File scoreFile = new File(fileName);
            if (scoreFile.createNewFile()) {
                writer = new FileWriter(scoreFile);
                TauntBot.logger.info("Created new file to store gambling scores.");
            } else {
                writer = new FileWriter(fileName);
                TauntBot.logger.info("Opened gambling score file.");
            }
            for (Map.Entry<Long,Integer> entry : scores.entrySet())
                writer.write(entry.getKey() + "," + entry.getValue() + System.lineSeparator());
            writer.close();
        } catch (IOException ex) {
            TauntBot.logger.error(ex);
        }         
    }
    
    
    /**
     *  Add a 1-FridgeCoin reward for each message send by a gambler. 
     * 
     *  This method is called by MessageListener for every message it receives.
     * 
     *  @param sender 
     */
    public static void addMessageReward (TwitchUser sender) {
        if (gl == null)
            gl = new GambleListener();
        
        if (gl.messageCount++ > 50)
            gl.storeScores();
        
        long userId = sender.getUserID();

        // If this is not a gambling user, do nothing.
        if (!users.containsKey(userId))
            return;
        
        int account = scores.get(userId);
        if (sender.getUserType().value >= USER_TYPE.SUBSCRIBER.value)
            scores.put(userId, account + 2);    // Add two coins for subs.
        else
            scores.put(userId, account + 1);    // Add one coin for regular users.
    }

    
    public static String gamble (TwitchUser sender, TwitchMessage message, Twirk twirk) {
        if (gl == null)
            gl = new GambleListener();        

        // We need access to the Twirk object to know how many users are online
        gl.twirk = twirk;
        
        String[] text = message.getContent().trim().split(" ");
        
        if (text.length == 1)   // No other commands, only "!gamble".
            return signup(sender);
        if (text[1].equalsIgnoreCase("leaderboard"))
            return leaderboard();
        if (text[1].equalsIgnoreCase("clear") && sender.getUserType().value >= USER_TYPE.MOD.value )
            return clear();
        
        // If the user wants to gamble [x] points, do it:
        int toGamble = Integer.parseInt(text[1]);
        if (toGamble != 0)
            return gambleBucks(sender, toGamble);
        
        return "Unknown gamble command.";
    }
 

    /** 
     *  Clear everything. Should only be called by a mod or admin.
     * 
     *  @return Closing message.
     */
    private static String clear () {
        users.clear();
        scores.clear();
        return "The casino is now closed.";
    }
    

    /**
     *  We need to make our scores HashMap sortable on its values. This is annoying,
     *  and requires a custom comparator. So we add that here. Which is kind'a wrong,
     *  but frankly my dear, I don't give a darn. 
     * 
     *  @param <K>
     *  @param <V>
     *  @param map
     *  @return     A value-sorted entryset.
     */
    static <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1;
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }    
    
    
    /**
     *  Output a list of gamblers, sorted by score.
     * 
     *  @return Text string indicating scores.
     */
    private static String leaderboard () {
        String leaders = "Our best gamblers so far: ";
        
        // Sort by score
        for (Map.Entry<Long,Integer> userScore : entriesSortedByValues(scores)) {
            
            // Check if we have a user
            TwitchUser user = users.get(userScore.getKey());
            if (user == null)
                continue;
            
            String displayName = user.getDisplayName();
            leaders += displayName + ": " + userScore.getValue() + ",\t         ";
        }
        return leaders;
    }
    
    
    /**
     *  Gamble the specified amount of T-bucks. This only works if the sender
     *  has at least that amount of T-bucks. 
     *  
     * @param sender    The user sending the request.
     * @param bucks     The amount of bucks this user is gambling.
     * @return          outcome of the gamble.
     */
    private static String gambleBucks (TwitchUser sender, int bucks) {
        long userId = sender.getUserID();
       
        // Check if this user has an account already. If not, create one.
        if (!users.containsKey(userId))
            signup(sender);

        int account = scores.get(userId);            
        
        if (account < bucks)
            return "Insufficient funds. You have " + account + " FridgeCoins available.";
        
        if (bucks < 0) {
            scores.put(userId, 0);
            return "Trying to cheat, you scoundrel? Your score has been set to 0! Hah!";
        }
        
        // Roll. Make it so the user has a SLIGHTLY bigger chance of winning.
        int outcome = new Random().nextInt(100);
        if (outcome < 55) {
            int newAmount = account + bucks;
            scores.put(userId, newAmount);
            return "Succes: you won " + bucks + " FridgeCoins! You now have " + newAmount
                    + " FridgeCoins in your account.";
        } else {
            int newAmount = account - bucks;
            scores.put(userId, newAmount);
            return "Loser! You lost " + bucks + " FridgeCoins. You now have " + newAmount
                    + " FridgeCoins in your account.";
        }
    }
    
    
    /**
     *  The user just tried the Gamble command without further options. Either start 
     *  them off with a fresh account (give them 100 T-bucks), or if they already have
     *  an account, give them a small chance to earn more T-bucks. 
     * 
     *  @param sender
     *  @return 
     */
    private static String signup (TwitchUser sender) {
        long userId = sender.getUserID();
        
        if (users.containsKey(userId))
            return rollForExtraBucks(sender);

        users.put(userId, sender);
        
        // If we have a score, this user gambled previously.
        if (scores.containsKey(userId))
            return rollForExtraBucks(sender);
        
        scores.put(userId, 100);
        return "Welcome gambler " + sender.getDisplayName() + "! We have opened an account "
                + "for you, and deposited 100 FridgeCoins into it. To gamble, simply type "
                + "!gamble [number] to gamble that many FridgeCoins. To see where you rank, "
                + "type !gamble leaderboard.";
    }
    
    
    /**
     *  Users can roll for a chance to win extra T-bucks. The amount of T-bucks they
     *  can win is dependent on the amount of users in the voice-chat. We should 
     *  prevent them from spamming this command, somehow.
     * 
     *  
     */
    private static String rollForExtraBucks (TwitchUser sender) {
        long userId = sender.getUserID();
        int account = scores.get(userId);
        int outcome = new Random().nextInt(100);
        int bonus   = 1 + twirk.getUsersOnline().size();
       
        if (outcome < 10) {
            int newAmount = account + bonus;
            scores.put(userId, newAmount);
            return "Lucky you! You won, and received 1 FridgeCoin for every user in "
                    + "chat. You now have " + newAmount + " FridgeCoins in your account.";
        }
        return "Unlucky: no extra FridgeCoins for you! You have " + account + " FridgeCoins.";
    }
}