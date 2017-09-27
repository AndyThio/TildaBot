package tilda.bot.commands.league;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tilda.bot.commands.Command;

public class RegisterCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Register League IGN";
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Arrays.asList("~register", "~reg");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Registers a League of Legends IGN to your Discord username";
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
        return null;
    }

    @Override
    public String[] getArgs(Message m){
        String[] ret = m.getRawContent().split(" " ,2);
        //Case shouldn't be an issue in the command
        //Don't lower everything as it could affect URLs
        ret[0] = ret[0].toLowerCase();
        return ret;
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
        //Regex to check if summoner name is a valid one according to Riot
        Pattern p = Pattern.compile("^[0-9\\p{L} _.]+$");
        Matcher m = p.matcher(args[1]);
        if(m.find()) {
            sendMessage(e, "Registered summoner name **" + args[1] + "** to **" +e.getAuthor().getName()+"**");
        }
        else {
            sendMessage(e, "Error: Invalid summoner name: **" + args[1] + "**");
        }
    }

}