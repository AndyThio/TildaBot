package tilda.bot.commands.league;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tilda.bot.commands.Command;


public class RegisterCommand extends Command {
    private static AmazonDynamoDB awsDB;

    public RegisterCommand(AmazonDynamoDB db){
        awsDB = db;
    }

    @Override
    public String getName() {
        //The Actual Name of the command
        //Example: "Help"
        return "Register League IGN";
    }

    @Override
    public List<String> getAlias() {
        //The different names the command can be called by
        //Example: "~help" || "~?" || "~h"
        //Note: Place the command alias you want to show in the help list first
        return Arrays.asList("~register", "~reg");
    }

    @Override
    public String getDescription() {
        //Short description of the overall command
        //Example: "List of commands and how to use them"
        return "Registers a League of Legends IGN to your Discord username";
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
        return Collections.singletonList("`~reg` or `~register`\n"
            + "**~reg [League IGN]**: Registers your [League IGN] to your Discord Account\n"
            + "\t__Note__: Only one League IGN can be registered to a Discord Account\n"
            + "__Exmaple__: ~register FishCells");
    }

    @Override
    public void onCommand(MessageReceivedEvent e, List<String> args) {
        //The actions of the command
        //Regex to check if summoner name is a valid one according to Riot
        Pattern p = Pattern.compile("^[0-9\\p{L} _.]+$");
        String summonerName = args.get(1);
        Matcher m = p.matcher(summonerName);
        if(m.find()) {
            //TODO: Look into specific functions to figure if I should reduce table calls
            DynamoDB db = new DynamoDB(awsDB);
            Table table = db.getTable("TildaLoL");
            //Gets the ign from the database and takes the string out of the item
            Item ignItem = table.getItem(new GetItemSpec().withPrimaryKey("UserID",e.getAuthor().getIdLong())
                    .withAttributesToGet("ign"));

            String currIGN = new String();
            if(ignItem != null){
                currIGN = ignItem.getString("ign");
            }

            //Placing name into the database. Will replace the name inside the database if one is already loaded.
            try {
                table.putItem(new Item().withPrimaryKey("UserID", e.getAuthor().getIdLong()).withString("ign", summonerName));
            } catch (Exception x) {
                sendMessage(e, "Unable to add ign: " + summonerName + "\n" + x.getMessage());
            }

            if(currIGN.isEmpty()) {
                    sendMessage(e, "Registered summoner name **" + summonerName + "** to **" + e.getAuthor().getName() + "**");
            }

            else{
                sendMessage(e, "Changed register summoner name for **" + e.getAuthor().getName()+ "** from **"
                        + currIGN + "** to **" + summonerName + "**");
            }

        }
        else {
            sendMessage(e, "Error: Invalid summoner name: **" + summonerName + "**");
        }
    }

}