package tilda.bot.commands;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public abstract class Command extends ListenerAdapter {

    //TODO: make a config file....
    //boolean to control whether or not we respond to other bots
    private boolean allowBotResponse = false;

    public abstract void onCommand(MessageReceivedEvent e, String[] args);
    public abstract List<String> getAlias();
    public abstract String getDescription();
    public abstract String getName();
    public abstract List<String> getUsage();

    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getAuthor().isBot() && !allowBotResponse){
            return;
        }

        if(containsCommand(e.getMessage())){
            onCommand(e, getArgs(e.getMessage()));
        }
    }

    public boolean containsCommand(Message m){
        return getAlias().contains(getArgs(m)[0]);
    }

    //TODO: fix double space errors
    public String[] getArgs(Message m){
        String[] ret = m.getRawContent().split(" " );
        //Case shouldn't be an issue in the command
        //Don't lower everything as it could affect URLs
        ret[0] = ret[0].toLowerCase();
        return ret;
    }

    //just qol change to shorten this call
    protected void sendMessage(MessageReceivedEvent e, String message){
        if(e.isFromType(ChannelType.PRIVATE)){
            e.getPrivateChannel().sendMessage(message).queue();
        }
        else{
            e.getTextChannel().sendMessage(message).queue();
        }
    }

    //Used to specifically send a private message
    protected void sendWhisper (MessageReceivedEvent e, String message){
        e.getAuthor().openPrivateChannel().complete().sendMessage(message).queue();
    }

}
