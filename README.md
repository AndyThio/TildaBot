# Tilda Bot
Your average Discord bot using [JDA](https://github.com/DV8FromTheWorld/JDA)! Updated never.

Prefix: `~`

Type `~help` for a list of commands

This README was written for Josh (You could have just listened to my explanation :unamused: )

## Adding a Command
Tilda bot has no actual command to create a new command. Instead you must code each command.


Each command is has its own source file, e.g. the command `~help` has a Java class file called `HelpCommand.java`. When creating a new command you will be extending the abstract class `Command`. This abstract class contains the following methods:

| Method|Return Type| Explanation| Is Abstract|
|---|---|---|:---:|
|getName()|String| Gets the actual name of the command| :heavy_check_mark:|
|getAlias() | List\<String\>| Gets the different commands it can be called by, e.g. `~help`|:heavy_check_mark:|
|getDescription()| String| Gets the description of the command| :heavy_check_mark:|
|getUsage()| List\<String\> | Gets specific usage and examples of the command's use| :heavy_check_mark:|
|onCommand(MessageReceivedEvent,args)| void| Where the actions of the command happen | :heavy_check_mark:|
|onMessageReceived(MessageReceivedEvent)|void| Handles checking for when the command is called. Don't need to touch this| |
|containsCommand(Message m) | boolean | checks if message matches a command alias| |
|getArgs(Message m) | String[]| Splits message by " " (space) and returns array string containing all but the first element (which is the command)
|sendMessage(MessageReceivedEvent, String)| void| Sends the String message to the discord channel contained in MessageReceivedEvent. **Use this to send messages!!**||
|sendWhisper(MessageReceivedEvent, String)| void| Sends the String message as a private whisper to the user contained in MessageReceivedEvent.||

Although this looks like a lot, the only important ones to note are the abstract methods and `sendMessage`.

### Getting Started
To start, fill out the basic information about your command by implementing `getName`, `getAlias`, `getDescription`, and `getUsage`. Completing this will automatically populate the help info on this command once it is registered.

### Adding Actions
To add actions and function to your command, implement the `onCommand` method with the actions you want it to perform. In order to send messages use the `sendMessage` method for a response (will automatically send responses back privately if the command was whispered to the bot)  or send the response privately using `sendWhisper`.

Note: `MessageReceivedEvent` is a JDA class that contains the User who sent the message, the channel it was from, the message, and other things. For more information see the [JDA documentation](http://home.dv8tion.net:8080/job/JDA/javadoc/).

### Registering Your Command
Once you have finished everything, you can now register the command. Open up `Tilda.java` and look in the `main` function. Find a comment that states: `//Register commands here!!` and register you command by adding: `api.addEventListener(help.registerCommand( new [Your Command]));`

**Example:**
```
		//Register commands here!!
        api.addEventListener(help.registerCommand(new HelpCommand()));
        api.addEventListener(help.registerCommand(new InfoCommand()));
        api.addEventListener(help.registerCommand(new TeamCommand()));
```
Once your complete this step, your command is done and will be live when you deploy the new version.

### Command Template
Here is a template, which is meant for use in Intellij: <https://pastebin.com/eLArwxLX>

To add this template, goto `File` \> `Settings` \> `Editor` \> `File and Code Templates` and click the `+` and copypasta in the template. To use it, when creating a new Java class, click the drop down in `Kind` and select the template.

## Bot Testing
I've set up a little test server where the bot is already invited to and set up in. Feel free to ask for an invite and the bot token.

Alternatively you can add this bot to your own discord server and get your own tokens: [Guide](https://github.com/Just-Some-Bots/MusicBot/wiki/FAQ)

Once you have a token and access to the server the bot has been added to you can start testing. Run the bot (make sure you have a console for the jar to output to) and you will be asked for a token. Input the token and it will create a token file called `tildaToken.txt` (**IMPORTANT: Do NOT ever upload this file or share it or the token**). The bot should now be active on the test server waiting for your commands

## Deployment
This section is specific to League 2.0 server deployment. All you have to do is push to the master branch. So only push to the master branch if you are finished (means testing, testing and ensuring everything works without crashing).

**Note:** Since this is deployed on Heroku, the bot may build successfully in your IDE, but fail in Heroku. If the build does fail, either the bot will stop working and responding OR the bot will not be updated (meaning previous working version will be online). In either case, don't panic it isn't a big deal and check the build logs on Heroku. If you aren't a collaborator ask me to add you as one or send your the build logs.

## Questions??
Feel free to message me questions and such, but if I am not on, considering joining the Discord API server and asking people in the `#java_jda` channel or the JDA server. These people helped me a lot, even with my stupid questions.


Invite to Discord API server: [Unofficial Discord API Guild](https://discord.gg/discord-api)

Invite to JDA server: [JDA (Java Discord API)](https://discord.gg/0hMr4ce0tIl3SLv5)