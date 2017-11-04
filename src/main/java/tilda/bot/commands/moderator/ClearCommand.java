package tilda.bot.commands.moderator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.time.OffsetDateTime;
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
        return "Clears all bots messages";
    }

    @Override
    public List<String> getUsage() {
        //Detailed description of how to use the command and differnt versions of it
        //Include examples and such
        /* Example:
         * ~help OR ~help <command>
         * ~help - List the name and description of all the commandsFishCells
#3261
         * ~help <command> - List the name, aliases and usage information of a specific command * Example: ~help help */ String m = "~clear [OPTIONS]\n" + "**~clear [OPTIONS]**: Clears the channel of any of the bots responses\n";
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
        //used to determine to delete user messages or not
        boolean delUser = false;
        for(String s : args){
            switch (s){
                case "-a":
                    delUser = true;
                    break;
            }
        }
        List<Message> messageList = e.getTextChannel().getHistory().retrievePast(30).complete();
        List<Message> toDelete = new ArrayList<>();

        //TODO: Parallelize (FINALLY SOMETHING TO TEST ON JAVA PARALLELIZATION)
        for(int i = 0; i < messageList.size(); ++i){
            if(messageList.get(i).getAuthor().isBot() || (delUser && messageList.get(0).getRawContent().startsWith("~"))){
                if(messageList.get(i).getCreationTime().isAfter(OffsetDateTime.now().minusWeeks(2))){
                    toDelete.add(messageList.get(i));
                }
            }
        }
        if(toDelete.size() > 1) {
            e.getTextChannel().deleteMessages(toDelete).queue();
        }
        else if (toDelete.size() == 1){
            toDelete.get(0).delete().queue();
        }
        else{
            sendMessage(e, "No messages to delete");
        }


    }

}