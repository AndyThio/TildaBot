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
            s.append("__**Commands Supported**__");

            for(Command c : loaded){
                String d = c.getDescription();
                d = (d == null || d.isEmpty()) ? NO_DESCRIPTION : d;

                s.append("**").append(c.getAlias().get(0)).append("** - ");
                s.append(d).append("\n");
            }
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
            sendMessage(e, "**" + command + "** does not exist\nUse ~help to list all commands");
        }
    }

    public Command registerCommand(Command c){
        loaded.add(c);
        return c;
    }

}