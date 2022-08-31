# TwitchBot
Simple JAVA-based twitch messaging bot. I created this bot with the sole purpose of having a bot around for the few commands I really want to have implemented, without giving some external company full access to my Twitch data. Could not find something easy and simple to run, so wrote this. If all you want is a bot to tell your viewers where to find your discord server, or occasionally advertise your youtube channel etc., then this might be the bot for you. 

The backbone of this bot is the excellent @Gikkman Twirk library: https://github.com/Gikkman/Java-Twirk 

# How to use
## Install the JAVA runtime
Since this bot is written in JAVA, you probably want to install JAVA. Probably. You only need the runtime to run the bot, which you can find here: https://www.java.com/en/download/manual.jsp

## Download and configure the bot
The latest build of the bot can be found under, you guessed it, /build. Download the contents of this folder. This should include the TauntBot.jar program, as well as three configuration files: configuration.properties, adds.txt and responses.csv. The purpose of these is as follows:

### configuration.properties
Open this file with a simple text editor, like notepad. Here you enter the details for your channel. The file should be self-explanatory: at minimum you need to enter the name of your channel (your twitch username), the nickname of your bot, and the passkey, which you can get from: https://twitchapps.com/tmi/. I recommend creating a new Twitch account for your twitch bot; that way, your viewers can easily differentiate between messages from you and messages from your bot. Or don't, I'm just a readme, not the Twitch-police. 

Note: when creating the passkey, you MUST be logged in as the account for your bot. If you use your streaming account, this should not be a problem, but if you use a separate account for your bot, make sure you are logged into that account before generating the passkey. Your passkey always starts with "oauth:" and a random string of characters. Never share your passkey with anyone. Except for me of course, you can totally trust me. Totally.

The 'addInterval' setting allows you to specify how often your bot should pipe up with a random message. This is in *number of received messages*. So if you set *addInterval = 50*, this means every 50 messages, your bot will say one of the lines in *adds.txt*. To disable this, simply remove the *adds.txt* file, or make it empty. Which brings me to the next file:

### adds.txt
Every line in this file will be send out, in order, to your chat, at the above-specified interval. You can use this to advertise your discord server and youtube channel for example, to tell your viewers to get subscribed, or simply to remind everyone periodically to sit up straight, you banana-shaped quasimodo. Leave this file empty (or delete it) to disable this feature.

### responses.csv 
Edit this file with a simple text editor like notepad. You can use Excel, but you risk Excel messing up the file because Excel hates CSV files. I am not salty at all. No sir. 

Anyhow, these are the commands and corresponding responses that you want your bot to reply to. Each line is one command/response combination, separated by, you guessed it, a comma. Hence the "comma seperated values" thing. Programmers are hella smart.

You can add as many commands as you like, and these do not necessarily have to start with an exclamation mark. You want your both to respond whenever someone starts a sentence with "I"? Go right ahead and create the line:

>I,nobody cares about what you think.

Easy as pie!

There are a few special commands, marked in the example responses with a [placeholder]. If you remove these, the corresponding special command will not be available. Available commands are currently as follows:

- !quote: produce a random quote from the quotes.txt file, if it exists. Each line is one quote.
- !newQuote: add a new quote to the quotes.txt file (and create the file if it does not exist). I opted not to put a limit on who can use this, so if you'd prefer if your viewers cannot add new quotes, you may want to remove this command.
- !giveaway: signs a viewer up for a giveaway.
-- !giveaway draw: picks one viewer from those that signed up. 
-- !giveway clear: empties the list of viewers who signed up for your giveaway.
- !commands: lists all available commands to the user. This will automatically update with all commands you place in this file. Again, if you do not want your viewers to know which commands they can use (bit weird, but I'm not judging), simply remove this line from the respones.csv file.

## Running the bot
This bot will run the same way as any JAVA application: simply use the following command on your command-line of choice

> java -jar TauntBot.jar

The bot will start up and keep running indefinitely (or until it crashes because some doofus misconfigured it. But I'm not judging). Personally I run my bot on a raspberry pi and keep it running more or less always. No harm in it, really. If you want to edit the configuration, change commands or manually change which quotes the bot can access, you must restart the bot. There is no on-line reloading of any info. I mean, come on, what do you take this for, a closed source project made by actually competent people?!

I will add new features as and when I need them, but really the whole purpose of this bot is to have something really simple and straightforward to use, so, yeah. Don't expect magic bot-castles in the sky. 

# Fixing stuff' 
If you want to muck about with this yourself, you should be able to download the whole affair and import it as a maven project into Netbeans. That's what I did anyway and it seems to work for me. Fair wairning: you can expect as much support from me as you would from a particularly soggy leave of grass. Come on man, you're a programmer, google your problems like the rest of us.

# Credits
Again, mad props to Gikkman et al for creating the underlying library, hell'a easy to use mates, bra jobbet. If you like this, come over to my channel and say hi: DrTauntsalot at https://www.twitch.tv/drtauntsalot. 
