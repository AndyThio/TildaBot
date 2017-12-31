package tilda.bot.commands.league;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import tilda.bot.commands.Command;
import tilda.bot.util.APIUtil;
import tilda.bot.util.AWSUtil;
import tilda.bot.util.DiscordUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelCommand extends Command {

    @Override
    public void onCommand(MessageReceivedEvent e, List<String> args) {
        try{
            if (args.size() == 1) {
                Table table = AWSUtil.getTable("TildaLoL");
                Item ignItem = table.getItem(new GetItemSpec()
                        .withPrimaryKey("UserID", DiscordUtil.getUserIdFromMessage(e))
                        .withAttributesToGet("ign"));
                if (ignItem != null) {
                    String ign = ignItem.getString("ign");
                    sendMessage(e, getLevelMessage(ign));
                } else {
                    sendMessage(e, "You are not registered yet. Please register your discord name by using the ~register command");
                }
            } else if (args.size() == 2) {
                String ign = args.get(1);
                sendMessage(e, getLevelMessage(ign));
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private String getLevelMessage(String ign) {
        Map<String, Object> response = APIUtil.execute(APIUtil.API_ENDPOINT.SUMMONERID,
                Collections.singletonList(ign));
        if (response == null || response.isEmpty()) {
            return "Summoner name not recognized.";
        } else {
            return processResponse(response);
        }

    }

    /**
     * Takes the response from the api and returns the desired message to the user
     */
    private String processResponse(Map<String, Object> response) {
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
        return "Summoner Level";
    }

    @Override
    public List<String> getUsage() {
        return Collections.singletonList(
                          "~level [SUMMONER] or ~l [SUMMONER]\n"
                        + "\t__Example__: ~level Fishcells"
                        + "\t__Note__: If registered, then you can omit your summoner name when entering the command."
        );
    }
}
