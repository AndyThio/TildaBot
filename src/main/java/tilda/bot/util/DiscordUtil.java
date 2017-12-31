package tilda.bot.util;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class DiscordUtil {

    public static Long getUserIdFromMessage(MessageReceivedEvent event) {
        return event.getAuthor().getIdLong();
    }
}
