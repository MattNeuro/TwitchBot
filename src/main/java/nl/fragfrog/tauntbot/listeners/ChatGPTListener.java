package nl.fragfrog.tauntbot.listeners;

import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import nl.fragfrog.tauntbot.TauntBot;
import static nl.fragfrog.tauntbot.TauntBot.logger;

/**
 *
 * @author DrTauntsalot
 */
public class ChatGPTListener {
  
    private static String response = "";
    

    /**
     *  Generate an AI response.
     * 
     *  @param message
     *  @return The AI response to message.
     */
    public static String ai (TwitchUser sender, TwitchMessage message) {
        if (sender.getUserType().value < USER_TYPE.SUBSCRIBER.value)
            return "The AI is only available to subscribers and above.";
        
        String text             = message.getContent().trim().substring(3);     // Remove the "!ai" bit.
        String token            = TauntBot.configuration.getProperty("aiKey");
        OpenAiService service   = new OpenAiService(token);        
        logger.info("Generating AI response for message:\r\n    " + text );
    
        return getChatResponse(service, text);
//        return getCompletionResponse(service, text);
    }
    
    
    private static String getChatResponse (OpenAiService service, String text) {
        final List<ChatMessage> messages  = new ArrayList<>();
        final ChatMessage userMessage     = new ChatMessage(ChatMessageRole.USER.value(), text);
        messages.add(userMessage);
                
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .n(1)
                .maxTokens(50)
                .logitBias(new HashMap<>())
                .build();

        response = "";
        service.streamChatCompletion(chatCompletionRequest).doOnError(Throwable::printStackTrace).blockingForEach(ChatGPTListener::addToResponse);
        return response;
    }

    
    private static void addToResponse (ChatCompletionChunk input) {
        for (ChatCompletionChoice choice : input.getChoices()) {
            String content = choice.getMessage().getContent();
            if (content != null)
                response += content.trim() + " ";
        }
    }
    
    
    
    private static String getCompletionResponse (OpenAiService service, String text) {
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