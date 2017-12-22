package tilda.bot.commands.league;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import tilda.bot.commands.Command;
import tilda.bot.util.APIUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelCommand extends Command {
    private static AmazonDynamoDB awsDB;

    public LevelCommand (AmazonDynamoDB db){
        awsDB = db;
    }

    @Override
    public void onCommand(MessageReceivedEvent e, String[] args) {
        try{
           List<String> apiArgs = Arrays.asList(args).subList(1, args.length);
           Map<String, Object> response = APIUtil.execute(APIUtil.API_ENDPOINT.SUMMONERID, apiArgs);
           if (response == null || response.isEmpty()) {
               return;
           } else {
               sendMessage(e, createMessage(response));
           }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private String createMessage(Map<String, Object> response) {
        Integer level = (Integer) response.get("summonerLevel");
        return "You are currently level " + level.toString();
    }


    @Override
    public List<String> getAlias() {
        return Arrays.asList("~level", "~l");
    }

    @Override
    public String getDescription() {
        return "Finds the level of the summoner";
    }

    @Override
    public String getName() {
        return "Summoner Info";
    }

    @Override
    public List<String> getUsage() {
        return Collections.singletonList(
                          "~level [SUMMONER] or ~l [SUMMONER]\n"
                        + "\t__Example__: ~level Fishcells"
        );
    }
}
