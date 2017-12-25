package tilda.bot.commands.General;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;

import tilda.bot.commands.Command;

public class PingCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Ping Pong";
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Collections.singletonList("~ping");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Tilda's First Command Revamped!";
    }

    @Override
    public List<String> getUsage() {
        //Detailed description of how to use the command and differnt versions of it
        //Include examples and such
        /* Example:
         * ~help OR ~help <command>
         * ~help - List the name and description of all the commands
         * ~help <command> - List the name, aliases and usage information of a specific command
         * Example: ~help help
        */
        return Collections.singletonList("WHO KNOWS WHAT WILL HAPPEN");
    }

    @Override
    public void onCommand(MessageReceivedEvent e, List<String> args) {
        //The actions of the command
        sendMessage(e,"Pong!");
    }

}