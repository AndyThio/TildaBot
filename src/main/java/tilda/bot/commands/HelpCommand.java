package tilda.bot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;


public class HelpCommand extends Command {

    private static TreeMap<String, Command> loaded = new TreeMap<>();

    private final String NO_NAME = "No name has been provided for this command.";
    private final String NO_DESCRIPTION = "No description has been provided for this command.";
    private final String NO_USAGE = "No usage instructions has been provided for this command.";

    private final String U_NAME = "__**Name:**__ ";
    private final String U_DESC = "__**Description:**__ ";
    private final String U_ALIAS = "__**Aliases:**__ ";
    private final String U_USAGE = "__**Usage:**__ ";


    @Override
    public String getName() {
        return "Help Command";
    }

    @Override
    public List<String> getAlias() {
        return Arrays.asList("~help" , "~h" , "~?", "~commands", "~command");
    }

    @Override
    public String getDescription() {
        return "List of commands and how to use them";
    }

    @Override
    public List<String> getUsage() {
        return Collections.singletonList(
                "`~help` or `~h` or `~?`\n"
                + "**~help**: List the name and description of all the commands\n"
                + "**~help <command>**: List the name, aliases and usage information of a command\n"
                + "\t__Note__: This command can use aliases of commands as well\n"
                + "__Example__: ~help help"
        );
    }

    @Override
    public void onCommand(MessageReceivedEvent e, List<String> args) {
        if(args.size() < 2){
            StringBuilder s = new StringBuilder();
            s.append("Type `~help [COMMAND]` for detailed information about that COMMAND\n\n");
            s.append("__**Commands Supported**__\n");

            for(Command c : loaded.values()){
                String d = c.getDescription();
                d = (d == null || d.isEmpty()) ? NO_DESCRIPTION : d;

                s.append("**").append(c.getAlias().get(0)).append("** - ");
                s.append(d).append("\n");
            }
            //Music bot commands... I was too lazy to restructure it
            s.append("\n__**Music Player Commands**__\n");
            s.append("**~join** -  Tells Tilda to join the channel mentioned\n");
            s.append("**~leave** - Tells Tilda to leave the voice channel\n");
            s.append("**~play** - Plays song at url (plays default if no url mentioned)\n");
            s.append("**~pplay** - Loads and plays playlist at url (plays default if no url mentioned)\n");
            s.append("**~skip** - Skips current song\n");
            s.append("**~pause** - Pauses or resumes the player\n");
            s.append("**~stop** - Stops the player and clears its queue\n");
            s.append("**~volume** - sets volume to a number 10-100 or default (35) if no number is mentioned\n");
            s.append("**~restart** - Restarts the current track\n");
            s.append("**~reset** - Resets the player and clears the queue\n");
            s.append("**~nowplaying** - State currently playing song\n");
            s.append("**~list** - List the next 10 songs in queue\n");
            s.append("**~shuffle** - Shuffles up the queue\n");
            s.append("**~follow** - Follows the bot around when it changes voice channels\n");
            s.append("**~unfollow** - Unfollows the bot\n");

            sendMessage(e,s.toString());
        }
        else{
            String command = args.get(1).charAt(0) == '~' ? args.get(1) : "~" + args.get(1);
            for(Command c : loaded.values()){
                if(c.getAlias().contains(command)) {
                    //Getting information and replacing with stuff if the fields are missing
                    String n = c.getName();
                    n = (n == null || n.isEmpty())? NO_NAME : n;
                    String d = c.getDescription();
                    d = (d == null || d.isEmpty())? NO_DESCRIPTION : d;
                    List<String> u = c.getUsage();
                    u = (u == null || u.isEmpty())? Collections.singletonList(NO_USAGE) : u;

                    String m = U_NAME  + n + "\n"
                            + U_DESC + d + "\n"
                            + U_ALIAS + "`" + String.join( "` || `", c.getAlias()) + "`\n"
                            + U_USAGE + u.get(0);

                    sendMessage(e,m);

                    for (int i = 1; i < u.size(); ++i){
                        String mcont = "__" + n + " Usage Cont. (" +(i+1) + ")__\n"
                                + u.get(i);
                        sendMessage(e,mcont);
                    }
                    return;
                }
            }
            //check if it is a music command. If it isn't then it isn't a command.
            if(!musicUse(e, command)) {
                sendMessage(e, "**" + command + "** does not exist\nUse ~help to list all commands\n");
            }
        }
    }

    public Command registerCommand(Command c){
        loaded.put(c.getAlias().get(0), c);
        return c;
    }

    private boolean musicUse(MessageReceivedEvent e, String command){
        String m = U_NAME;
        switch (command){
            case "~join":
                m += "Join Channel\n"
                        + U_DESC + "Tells Tilda to join the channel mentioned\n"
                        + U_ALIAS + "~join\n"
                        + U_USAGE + "~join [channel]\n"
                        + "\t[channel] must be a voice channel\n"
                        + "\t__Example:__ ~join Lobby";
                break;

            case "~leave":
                m += "Leave Channel\n"
                        + U_DESC + "Tells Tilda to leave the channel mentioned\n"
                        + U_ALIAS + "~leave\n"
                        + U_USAGE + "~leave\n"
                        + "**~leave** - Makes Tilda leave the channel\n"
                        + "\t__Example:__ ~leave";
                break;

            case "~play":
                m += "Play Song\n"
                        + U_DESC + "Loads a song at URL\n"
                        + U_ALIAS + "~play\n"
                        + U_USAGE + "~play || ~play [URL]\n"
                        + "**~play** - Plays a default song if no song is playing. Otherwise unpauses player\n"
                        + "**~play [URL]** - Loads a song linked by [URL] and plays if no song is playing\n"
                        + "\t__Note:__ Can play YouTube, SoundCloud, BandCampAudio, and HTTP Links";
                break;

            case "~pplay":
                m += "Play Playlist\n"
                        + U_DESC + "Loads a playlist at the URL\n"
                        + U_ALIAS + "~pplay\n"
                        + U_USAGE + "~pplay || ~play [URL]\n"
                        + "**~pplay** - Plays a default playlist if no song is playing\n"
                        + "**~pplay [URL}** - Loads a playlist linked by [URL] and plays if no song is playing*";
                break;
            case "~skip":
                m += "Skip song\n"
                        + U_DESC + "Skips the current song\n"
                        + U_ALIAS + "~skip\n"
                        + U_USAGE + "~skip\n"
                        + "**~skip** - Skips the current song";
                break;
            case "~pause":
                m += "Pause Player\n"
                        + U_DESC + "Pauses the player or resumes it\n"
                        + U_ALIAS + "~pause\n"
                        + U_USAGE + "~pause\n"
                        + "**~pause** - Pauses the player or resumes it";
                break;
            case "~stop":
                m += "Stop Player\n"
                        + U_DESC + "Stops the player and clears its queue\n"
                        + U_ALIAS + "~stop\n"
                        + U_USAGE + "~stop\n"
                        + "**~stop** - Stops the player and clears the song queue";
                break;
            case "~volume":
                m += "Set Volume\n"
                        + U_DESC + "Sets the volume\n"
                        + U_ALIAS + "~set\n"
                        + U_USAGE + "~set || ~set [NUM]\n"
                        + "**~set**- Sets the volume to default volume of 35\n"
                        + "**~set [NUM]** Sets the volume to NUM\n" +
                        "\t__Note:__ NUM must be between 1-100";
                break;

            case "~restart":
                m+= "Restart Song\n"
                        + U_DESC + "Restarts the current Song\n"
                        + U_ALIAS + "~restart\n"
                        + U_USAGE + "~restart"
                        + "**~restart** - Restarts the current playing song";
                break;
            case "~reset":
                m += "Reset Player\n"
                        + U_DESC + "Resets the player and clears its queue\n"
                        + U_ALIAS + "~reset\n"
                        + U_USAGE + "~reset\n"
                        + "**~reset** - Resets the player and clears its queue";
                break;
            case "~nowplaying":
            case "~np":
                m += "Now Playing\n"
                        + U_DESC + "List information about the current playing song\n"
                        + U_ALIAS + "~np || ~nowplaying\n"
                        + U_USAGE + "~np || ~nowplaying\n"
                        + "**~np** - List information about the current playing song\n"
                        + "**~nowplaying** - List information about the current playing song";
                break;

            case "~list":
                m+= "List Songs"
                        + U_DESC + "List the next 10 songs in queue\n"
                        + U_ALIAS + "~list\n"
                        + U_USAGE + "~list\n"
                        + "**~list** - List the next 10 songs in queue";
                break;
            case "~shuffle":
                m += "Shuffle Queue\n"
                        + U_DESC + "Randomizes the queue\n"
                        + U_ALIAS + "~shuffle\n"
                        + U_USAGE + "~shuffle\n"
                        + "**~shuffle** - Shuffles the songs n the queue";
                break;
            case "~follow":
                m+= "Follow Player\n"
                        + U_DESC + "Follows the Music bot when it changes channels\n"
                        + U_ALIAS + "~follow\n"
                        + U_USAGE + "~follow\n"
                        + "**~follows** - Follows the Music bot/Player if it changes channels";
                break;

            case "~unfollow":
                m+= "Unfollow Player\n"
                        + U_DESC + "Unfollows the Music bot\n"
                        + U_ALIAS + "~unfollow\n"
                        + U_USAGE + "~unfollow\n"
                        + "**~unfollows** - Unfollows the Music bot/Player if it changes channels";
                break;

            default:
                return false;
        }
        sendMessage(e,m);
        return true;
    }

}