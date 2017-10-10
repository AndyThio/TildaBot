package tilda.bot.commands.moderator;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tilda.bot.commands.Command;

public class MoveCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Move Voice Channels"
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Arrays.asList("~move","~mv");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Moves members in a voice channel to another voice channel";
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
        return Collections.singletonList(
                "~move || ~mv\n"
                + "**~move [DEST] [FROM]**: Moves people from voice channels FROM to DEST\n"
                + "__Note:__ Multiple FROM voice channels are allowed, but only 1 DEST \n"
                + "__Note:__ DEST channel must be the first listed Channel\n"
                + "__Example:__ ~move Lobby General General 2.0"
        );
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
    }

}