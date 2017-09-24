package tilda.bot.commands;

import com.sun.deploy.util.StringUtils;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class HelpCommand extends Command {

    private static List<Command> loaded = new ArrayList<>();

    private final String NO_NAME = "No name has been provided for this command.";
    private final String NO_DESCRIPTION = "No description has been provided for this command.";
    private final String NO_USAGE = "No usage instructions has been provided for this command.";

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
    public void onCommand(MessageReceivedEvent e, String[] args) {
        if(args.length < 2){
            StringBuilder s = new StringBuilder();
            s.append("__**Commands Supported**__\n");

            for(Command c : loaded){
                String d = c.getDescription();
                d = (d == null || d.isEmpty()) ? NO_DESCRIPTION : d;

                s.append("**").append(c.getAlias().get(0)).append("** - ");
                s.append(d).append("\n");
            }
            //Music bot commands... I was too lazy to restructure it
            s.append("\n__**Music Player Commands**__\n");
            s.append("**~join** - Joins the channel mentioned\n");
            s.append("**~leave** - Leaves the voice channel\n");
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
            String command = args[1].charAt(0) == '~' ? args[1] : "~" + args[1];
            for(Command c : loaded){
                if(c.getAlias().contains(command)) {
                    //Getting information and replacing with stuff if the fields are missing
                    String n = c.getName();
                    n = (n == null || n.isEmpty())? NO_NAME : n;
                    String d = c.getDescription();
                    d = (d == null || d.isEmpty())? NO_DESCRIPTION : d;
                    List<String> u = c.getUsage();
                    u = (u == null || u.isEmpty())? Collections.singletonList(NO_USAGE) : u;

                    String m = "**Name:** " + n + "\n"
                            + "**Description:** " + d + "\n"
                            + "**Aliases:** `" + StringUtils.join(c.getAlias(), "` || `") + "`\n"
                            + "**Usage:**" + u.get(0);

                    sendMessage(e,m);

                    for (int i = 1; i < u.size(); ++i){
                        String mcont = "__" + n + " Usage Cont. (" +(i+1) + ")__\n"
                                + u.get(i);
                        sendMessage(e,mcont);
                    }
                }
                return;
            }
            sendMessage(e, "**" + command + "** does not exist\nUse ~help to list all commands\n"
                + "__Note:__ Music Player Commands usage help has not been implemented yet");
        }
    }

    public Command registerCommand(Command c){
        loaded.add(c);
        return c;
    }

}