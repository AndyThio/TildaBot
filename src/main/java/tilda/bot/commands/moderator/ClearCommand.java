package tilda.bot.commands.moderator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.core.utils.PermissionUtil;
import tilda.bot.commands.Command;

public class ClearCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Clear Bot Messages";
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Arrays.asList("~clear", "~clr");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Clears the bots messages";
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
        String m = "~clear [OPTIONS]\n"
                + "**~clear [OPTIONS]**: Clears the channel of any of the bots responses\n";
        List<String> l = new ArrayList<>();
        l.add(m);
        m = "**__Options__**\n"
                + "**-a**: Clears all messages that include '~'";
        l.add(m);
        return l;
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
        List<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(e.getMember()));
        if(!perms.contains(Permission.MESSAGE_MANAGE)){
            sendMessage(e, "You do not have the permission to delete messages");
            return;
        }


    }

}