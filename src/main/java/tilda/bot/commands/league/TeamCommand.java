package tilda.bot.commands.league;

import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        String m = "~team [OPTIONS] [CHANNEL]\n"
                + "options for filling out what is needed";
        return Collections.singletonList(m);
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        //The actions of the command
        List<Member> members = new ArrayList<>();

        //parses through the flags and also finds the voice channel that we want
        for(int i = 1; i < args.length; ++i){
            if (args[i].startsWith("-")){
                switch(args[i]){
                    default:
                        sendMessage(e, "Error: No Flag **" + args[i] + "** found");
                        return;
                }
            }
            else if(!e.getGuild().getVoiceChannelsByName(args[i], true).isEmpty()){
               if(members.isEmpty()){
                   //get list of members fro the voice channel
                  VoiceChannel v = e.getGuild().getVoiceChannelsByName(args[i],true).stream().findFirst().orElse(null);
                  //making the list modifiable
                  members = new ArrayList<>(v.getMembers());
               }
               else{
                   sendMessage(e, "Error: No Flag **" + args[i] + "** found");
                   return;
               }
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

}