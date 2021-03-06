package tilda.bot;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import tilda.bot.commands.General.PingCommand;
import tilda.bot.commands.HelpCommand;
import tilda.bot.commands.InfoCommand;
import tilda.bot.commands.league.LevelCommand;
import tilda.bot.commands.league.RegisterCommand;
import tilda.bot.commands.league.TeamCommand;
import tilda.bot.commands.moderator.ClearCommand;
import tilda.bot.commands.moderator.MoveCommand;
import tilda.bot.music.musicCommand;
import tilda.bot.util.APIUtil;

import javax.security.auth.login.LoginException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class Tilda extends ListenerAdapter{

    private static String token = "";
    private static  AmazonDynamoDB awsDB;

    //Edit this to change location where the token is stored
    //Warning!!! If you change the file name, don't forget to add it to the gitignore so you don't add it to github!
    private static String token_path = "./tildaToken.txt";

    public static void main(String[] args) throws Exception{
        System.out.println("Starting up Tilda Bot!");
        System.out.println("Retrieving token...");

        findToken();
        setup();

        HelpCommand help = new HelpCommand();


        JDA api = null;
        try {
            //check your token file if the token is invalid!
            //Delete it if you want to reset it
            api = new JDABuilder(AccountType.BOT)
                    .setToken(token)
                    .setStatus(OnlineStatus.ONLINE)
                    .buildBlocking();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        api.addEventListener(new musicCommand());


        //Register commands here!!
        //General Commands
        api.addEventListener(help.registerCommand(new HelpCommand()));
        api.addEventListener(help.registerCommand(new InfoCommand()));
        api.addEventListener(help.registerCommand(new PingCommand()));

        //League Commands
        api.addEventListener(help.registerCommand(new TeamCommand()));
        api.addEventListener(help.registerCommand(new RegisterCommand(awsDB)));

        //Moderator Commands
        api.addEventListener(help.registerCommand(new MoveCommand()));
        api.addEventListener(help.registerCommand(new ClearCommand()));
        api.addEventListener(help.registerCommand(new LevelCommand(awsDB)));
    }

    private static void setup(){
        awsDB = AmazonDynamoDBClient.builder().build();

        //setting up riot api
        String riotKey = System.getenv("RIOT_KEY");
        if(riotKey == null){
            System.out.println("RIOT_KEY environment variable does not exist");
            System.exit(1);
        }
        APIUtil.setApiKey(riotKey);
    }

    private static void findToken() throws Exception{
        //getting the token
        File token_file = new File(token_path);
        token = System.getenv("BOT_TOKEN");

        if(token == null) {
            if (token_file.isFile()) {
                //reads in the token from the file
                BufferedReader br = new BufferedReader(new FileReader(token_file));
                token = br.readLine();
            } else {
                //Token File doesn't exist, so it must be created

                //Users have to input the token
                System.out.println("Token file not found!\n");
                System.out.println("Enter in the Bot Token:");
                BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
                token = cin.readLine();

                //Saving the token so user doesn't need to keep re-entering it
                byte data[] = token.getBytes();
                Path p = Paths.get(token_path);

                try (OutputStream out = new BufferedOutputStream(
                        Files.newOutputStream(p, CREATE_NEW))) {
                    out.write(data, 0, data.length);
                } catch (IOException x) {
                    System.err.println(x);
                }
                System.out.println("Token File successfully created!");
            }
        }

    }
}
