package tilda.bot;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //checks if message is from a bot

        if(event.getAuthor().isBot()) return;
        Message message = event.getMessage();
        String content = message.getRawContent();

        MessageChannel channel = event.getChannel();

        if(content.equals("~ping")){

            //apparently important to call queue on the RestAction returned by sendMessage(...)
            channel.sendMessage("Pong!").queue();
        }
        else if(content.equals("~kill")){
            if(event.getMember().isOwner()){
                //apparently important to call queue on the RestAction returned by sendMessage(...)
                channel.sendMessage("Shutting Down").queue();

                System.exit(0);
            }
            else{
                //apparently important to call queue on the RestAction returned by sendMessage(...)
                channel.sendMessage("You have no power here!").queue();
            }
        }
        else if(content.equals("~help")){
            channel.sendMessage("Sorry... No help yet\nHere is the Tilda Docs: https://goo.gl/dtjFUk"+
                "\nSource Code: https://github.com/AndyThio/TildaBot").queue();
        }
    }
}
