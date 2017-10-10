package tilda.bot.commands.league;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.utils.PermissionUtil;
import tilda.bot.commands.Command;

public class TeamCommand extends Command {

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Create Teams";
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Arrays.asList("~team", "~teams");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Sorts people from a voice channel into two teams";
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
        //TODO: Finish expanding usage.
        List<String> l = new ArrayList<>();
        String m = "~team [OPTIONS] [CHANNEL]\n"
                + "**~teams [OPTIONS] [CHANNELS]**: Will create teams from the mention Channels\n"
                + "\t__Note:__ Can support multiple OPTIONS and CHANNELS\n"
                + "\t__Example:__ ~teams General\n"
                + "\t__Example:__ ~teams -m General\n";
        l.add(m);

        m = "**__Options__**\n"
                + "**-m**: Moves people to respective team channels after creating the teams\n";

        l.add(m);

        return l;
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
        List<Member> members = new ArrayList<>();
        boolean move = false;

        //parses through the flags and also finds the voice channel that we want
        for(int i = 1; i < args.length; ++i){
            if (args[i].startsWith("-")){
                switch(args[i]){
                    case "-m":
                        //Gets the permissions of the member in this server
                        List<Permission> perms = Permission.getPermissions(PermissionUtil.getEffectivePermission(e.getMember()));
                        if(perms.contains(Permission.VOICE_MOVE_OTHERS)){
                            move = true;
                        }
                        else{
                            sendMessage(e, "You do not have the permission to move others");
                        }
                        break;
                    default:
                        sendMessage(e, "Error: No Flag **" + args[i] + "** found");
                        return;
                }
            }
            //If there is no '-' flag then it must be a voice channel
            //Add memebers of the voice channel to a list of members to create  teams from
            else if(!e.getGuild().getVoiceChannelsByName(args[i], true).isEmpty()){
                //get list of members fro the voice channel
                VoiceChannel v = e.getGuild().getVoiceChannelsByName(args[i],true).stream().findFirst().orElse(null);
                //Allows for multiple channels to be called
                members.addAll(v.getMembers());
            }
            else{
                sendMessage(e, "Error: No Flag **" + args[i] + "** found");
                return;
            }
        }
        List<List<Member>> teams = new ArrayList<>();
        teams.add(new ArrayList<>());
        teams.add(new ArrayList<>());


        Collections.shuffle(members);
        //offset to account for bots in the channel
        int offset = 0;
        for(int j = 0; j < members.size(); ++j){
            if(!members.get(j).getUser().isBot()) {
                teams.get((j + offset) % 2).add(members.get(j));
            }
            else{
                ++offset;
            }
        }

        sendMessage(e,teamsMessage(teams));
        if(move){
            moveTeams(teams, e);
        }
    }

    private String teamsMessage(List<List<Member>> teams){
        String m = "__**Team 1**__\n";
        for(Member mem: teams.get(0)){
            m += mem.getEffectiveName() + "\n";
        }

        m += "\n__**Team 2**__\n";
        for(Member mem: teams.get(1)){
            m += mem.getEffectiveName() + "\n";
        }
        return m;
    }

    private void moveTeams(List<List<Member>> teams, MessageReceivedEvent e){
        Guild guild = e.getGuild();
        GuildController gc = guild.getController();
        //TODO: Create a catagory for league teams if it doesn't exist
        Category cat = guild.getCategoriesByName("LEAGUE TEAMS",true ).get(0);
        List<VoiceChannel> vcs = cat.getVoiceChannels();

        int max = Math.max(teams.get(0).size(),teams.get(1).size());
        int teamnum = 0;
        for(VoiceChannel i : vcs){
            //Make sure that the channel can fit everyone and that it is empty
            if(i.getUserLimit() >= max && i.getMembers().size() == 0){
                for(Member m : teams.get(teamnum)){
                    gc.moveVoiceMember(m,i).queue();
                }
                if(++teamnum >= 2){
                    return;
                }
            }
        }

        //if voice channels are full
        sendMessage(e, "\nUnable to find empty team channels. Defaulting to General channels");

        if(teamnum == 0) {
            VoiceChannel v = guild.getVoiceChannelsByName("General", true).get(0);
            for (Member m : teams.get(teamnum)) {
                gc.moveVoiceMember(m, v);
            }
            ++teamnum;
        }

        //TODO: Fix if uploaded to another server where this channel doesn't exist
        VoiceChannel v = guild.getVoiceChannelsByName("General 2.0", true).get(0);
        for (Member m : teams.get(teamnum)) {
            gc.moveVoiceMember(m, v);
        }

    }
}