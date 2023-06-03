package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import java.util.HashMap;
import nl.fragfrog.tauntbot.TauntBot;
import static nl.fragfrog.tauntbot.TauntBot.logger;

/**
 *
 * @author DrTauntsalot
 */
public class ChatGPTListener {
    
    
    private static ChatGPTListener   instance;
    private static HashMap<Long, TwitchUser> users;
        
    
    protected ChatGPTListener () {
        this.users = new HashMap<>();
        users.clear();
    }
    

    /**
     *  Generate an AI response.
     * 
     *  @param message
     *  @return The AI response to message.
     */
    public static String ai (TwitchMessage message) {
        String text             = message.getContent().trim().substring(3);     // Remove the "!ai" bit.
        String token            = TauntBot.configuration.getProperty("aiKey");
        OpenAiService service   = new OpenAiService(token);        
        logger.info("Generating AI response for message:\r\n    " + text );
    
        CompletionRequest completionRequest = CompletionRequest.builder()
                .prompt(text)
                .model("davinci")
                .echo(true)
                .build();

        String fullAnswer = "";
        for (CompletionChoice response : service.createCompletion(completionRequest).getChoices())
            fullAnswer += response.getText();
        
        return fullAnswer.trim();
    }
}