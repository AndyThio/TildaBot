package tilda.bot.commands.moderator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import tilda.bot.commands.Command;

public class MoveCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Move Voice Channels";
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
                + "__Example:__ ~move Lobby General Other"
        );
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
        List<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(e.getMember()));
        if(!perms.contains(Permission.VOICE_MOVE_OTHERS)){
            sendMessage(e, "You do not have the permission to move others");
            return;
        }
        if(args.length == 1){
            sendMessage(e,"Please include the DEST and FROM channels");
            return;
        }
        else if(args.length < 2){
            sendMessage(e, "Please include FROM channel(s)");
            return;
        }
        //First channel will be the channel that is the DEST channel
        Guild guild = e.getGuild();
        List<VoiceChannel> channels = new ArrayList<>();
        for(int i = 1; i < args.length; ++i){
            List<VoiceChannel> temp = guild.getVoiceChannelsByName(args[i],true);
            if(!temp.isEmpty()){
                channels.addAll(temp);
            }
            else{
                sendMessage(e,"Error: **" + args[i] + "** does not exist as a channel");
                return;
            }
        }
        List<Member> members = new ArrayList<>();
        for(int i = 1; i < channels.size(); ++i){
            members.addAll(channels.get(i).getMembers());
        }

        GuildController gc = guild.getController();
        for(Member mem : members){
            if(!mem.getUser().isBot()){
                gc.moveVoiceMember(mem, channels.get(0)).queue();
            }
        }
    }

}