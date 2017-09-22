package tilda.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.*;
import java.util.logging.Level;

public class musicCommand extends ListenerAdapter{
    /* List of Commands
    *   ~join <Voice Channel>   Joins the channel mentioned
    *   ~leave                  Leaves the voice channel
    *   ~play <url>             Plays song at url
    *   ~pplay <url>            Loads and plays playlist at url
    *   ~skip                   Skips current song
    *   ~pause                  Pauses the player or resumes it
    *   ~stop                   Stops the player and clears its queue
    *   ~volume <int>           Sets volume to <int> or default if no volume is mentioned
    *   ~restart                Restarts the current track
    *   ~reset                  Completely resets the player
    *   ~nowplaying | ~np       States currently playing song
    *   ~list                   List the next 10 songs in queue
    *   ~shuffle                Shuffles up the queue
    *   ~follow                 Follows the bot around when it changes voice channels
    *   ~unfollow               Unfollows the bot
    */

    public static final int DEFAULT_VOLUME = 35;

    private final AudioPlayerManager playerManager;
    private final Map<String, GuildMusicManager> musicManagers;
    private static List<Member> followers = new ArrayList<Member>();

    public musicCommand(){
        java.util.logging.Logger.getLogger("org.apache.http.client.protocol.ResponseProcessCookies").setLevel(Level.OFF);

        this.playerManager = new DefaultAudioPlayerManager();
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new SoundCloudAudioSourceManager());
        playerManager.registerSourceManager(new BandcampAudioSourceManager());
        playerManager.registerSourceManager(new HttpAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());

        musicManagers = new HashMap<String, GuildMusicManager>();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //make sure that the message isn't from a bot
        if(event.getAuthor().isBot()) return;

        String message = event.getMessage().getRawContent();

        //make sure that the message starts with the correct prefix or we can ignore it
        if(!message.startsWith("~")) return;

        //split the command into two parts so we can use the two parts later
        String[] command = message.split(" " , 2);
        Guild server = event.getGuild();
        MessageChannel orgin_chan = event.getChannel();

        GuildMusicManager mng = getMusicManager(server);
        TrackScheduler scheduler = mng.scheduler;
        AudioPlayer player = mng.player;

        if(command[0].equals("~join")){
            //check if there is a channel that is mentioned
            if(command.length == 1){
                orgin_chan.sendMessage("Please include the voice channel you want to join.").queue();
            }
            else{

                VoiceChannel voice = null;

                try{
                    voice = server.getVoiceChannelById(command[1]);
                } catch (NumberFormatException ignored){}

                if(voice == null){
                    //TODO: Apparently this has a lot of overhead... Change it to get rid of stream
                    voice = server.getVoiceChannelsByName(command[1],true).stream().findFirst().orElse(null);
                }

                if(voice == null){
                    orgin_chan.sendMessage("Could not find voice channel by name: " + command[1]).queue();
                }
                else{
                    server.getAudioManager().setSendingHandler(mng.sendHandler);

                    try{
                        server.getAudioManager().openAudioConnection(voice);
                        for (Member e : followers) {
                            if (e.getVoiceState().getChannel() != null) {
                                server.getController().moveVoiceMember(e, voice).queue();
                            }
                        }
                    }catch (PermissionException e){
                        if(e.getPermission() == Permission.VOICE_CONNECT){
                            orgin_chan.sendMessage("Tilda doesn't have permissions to connect to: " + voice.getName()).queue();
                        }
                    }
                }
            }
        }

        else if(command[0].equals("~leave")){
            server.getAudioManager().setSendingHandler(null);
            server.getAudioManager().closeAudioConnection();
        }

        else if(command[0].equals("~play")){
            if(command.length == 1){
                if(player.isPaused()){
                    player.setPaused(false);
                    orgin_chan.sendMessage("Player resumed").queue();
                }
                else if(player.getPlayingTrack() != null){
                    orgin_chan.sendMessage("Player is already playing").queue();
                }
                else if(scheduler.queue.isEmpty()){
                    loadAndPlay(mng, orgin_chan, "https://www.youtube.com/playlist?list=PLEgNqLmZpLuI9ajUy3Hg97NrpssG4repu",false);
                    orgin_chan.sendMessage("Player queue was empty\nPlaying default song").queue();
                }
            }
            else{
                //TODO: Add -p for playlist flag
                loadAndPlay(mng, orgin_chan, command[1],false);
            }
        }

        else if(command[0].equals("~pplay")){
            if(command.length == 1){
                loadAndPlay(mng, orgin_chan, "https://www.youtube.com/playlist?list=PLEgNqLmZpLuI9ajUy3Hg97NrpssG4repu",true);
            }
            loadAndPlay(mng, orgin_chan, command[1],true);
        }

        else if(command[0].equals("~skip")){
            scheduler.nextTrack();
            orgin_chan.sendMessage("Song skipped").queue();
        }

        else if(command[0].equals("~pause")){
            if(player.getPlayingTrack() == null){
                orgin_chan.sendMessage("Player is not currently playing").queue();
            }
            else{
                player.setPaused(!player.isPaused());
                if(player.isPaused()){
                    orgin_chan.sendMessage("Player paused").queue();
                }
                else{
                    orgin_chan.sendMessage("Player resumed").queue();
                }
            }
        }

        else if(command[0].equals("~stop")){
            scheduler.queue.clear();
            player.stopTrack();
            player.setPaused(false);
            orgin_chan.sendMessage("Player stopped and queue cleared").queue();
        }

        else if(command[0].equals("~volume")){
            if(command.length == 1){
                player.setVolume(DEFAULT_VOLUME);
                orgin_chan.sendMessage("Player set to default volume").queue();
            }
            else{
                try{
                    int newVol = Math.max(10, Math.min(100, Integer.parseInt(command[1])));
                    int oldVol = player.getVolume();

                    player.setVolume(newVol);
                    orgin_chan.sendMessage("Player volume changed from: `" + oldVol + "` to `" + newVol + "`").queue();
                } catch (NumberFormatException e){
                    orgin_chan.sendMessage("`" + command[1] + "` is not a valid integer");
                }
            }
        }

        else if(command[0].equals("~restart")){
            AudioTrack track = player.getPlayingTrack();
            if(track == null){
                track = scheduler.lastTrack;
            }
            if (track == null) {
                orgin_chan.sendMessage("No previous track started").queue();
            }
            else{
                orgin_chan.sendMessage("Restarting track: " + track.getInfo().title).queue();
                player.playTrack(track.makeClone());
            }
        }

        else if( command[0].equals("~reset")){
            synchronized (musicManagers){
                scheduler.queue.clear();
                player.destroy();
                server.getAudioManager().setSendingHandler(null);
                musicManagers.remove(server.getId());
            }

            mng = getMusicManager(server);
            server.getAudioManager().setSendingHandler(mng.sendHandler);
            orgin_chan.sendMessage("Player completely reset!").queue();
        }

        //TODO: add Timestamp of current position
        else if (command[0].equals("~nowplaying") || command[0].equals("~np")){
            AudioTrack track = player.getPlayingTrack();
            if(track == null){
                orgin_chan.sendMessage("No track currently playing").queue();
            }
            else {
                String title = track.getInfo().title;
                String author = track.getInfo().author;
                orgin_chan.sendMessage("Now Playing\nTitle: **" + title + "**\nBy: **" + author + "**").queue();
            }
        }

        else if (command[0].equals("~list")){
            Queue<AudioTrack> queue = scheduler.queue;
            synchronized (queue){
                if(queue.isEmpty()){
                    orgin_chan.sendMessage("Play queue is currently empty").queue();
                }
                else{
                    int trackCount = 0;
                    long queueLength = 0;

                    StringBuilder sb = new StringBuilder();
                    sb.append("Current Queue:").append("\n");
                    for (AudioTrack track : queue)
                    {
                        queueLength += track.getDuration();
                        if (trackCount < 10)
                        {
                            sb.append("`[").append(getTimestamp(track.getDuration())).append("]` ");
                            sb.append(track.getInfo().title).append("\n");
                            trackCount++;
                        }
                    }
                    sb.append("\n").append("Queue Size: ").append(queue.size()).append("\nTotal Queue Time Length: ").append(getTimestamp(queueLength));

                    event.getChannel().sendMessage(sb.toString()).queue();
                }
            }
        }

        else if (command[0].equals("~shuffle")){
            if (scheduler.queue.isEmpty()){
                orgin_chan.sendMessage("Queue is currently empty").queue();
            }
            else{
                scheduler.shuffle();
                orgin_chan.sendMessage("Queue shuffled").queue();
            }
        }

        else if (command[0].equals("~follow")){
            Member user = event.getMember();
            VoiceChannel botloc = server.getAudioManager().getConnectedChannel();
            if(user.getVoiceState().getChannel() != botloc){
                try {
                    if (user.getVoiceState().getChannel() != null) {
                        server.getController().moveVoiceMember(user, botloc).queue();
                    }
                } catch (NullPointerException ignore){}
            }
            if(!followers.contains(user)) {
                followers.add(user);
                orgin_chan.sendMessage("**" + user.getEffectiveName() + "** is now following Tilda").queue();
            }
            else {
                orgin_chan.sendMessage("**" + user.getEffectiveName() + "** is already following Tilda").queue();
            }
        }

        else if (command[0].equals("~unfollow")){
            Member user = event.getMember();
            followers.remove(user);
            orgin_chan.sendMessage("**" + user.getEffectiveName() + "** is no longer following Tilda").queue();
        }

    }
    private void loadAndPlay(GuildMusicManager mng, final MessageChannel channel, String url, final boolean addPlaylist)
    {
        final String trackUrl;

        //Strip <>'s that prevent discord from embedding link resources
        if (url.startsWith("<") && url.endsWith(">"))
            trackUrl = url.substring(1, url.length() - 1);
        else
            trackUrl = url;

        playerManager.loadItemOrdered(mng, trackUrl, new AudioLoadResultHandler()
        {
            @Override
            public void trackLoaded(AudioTrack track)
            {
                String msg = "Adding to queue: " + track.getInfo().title;
                if (mng.player.getPlayingTrack() == null)
                    msg += "\nPlayer has started playing";

                mng.scheduler.queue(track);
                channel.sendMessage(msg).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist)
            {
                AudioTrack firstTrack = playlist.getSelectedTrack();
                List<AudioTrack> tracks = playlist.getTracks();


                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                if (addPlaylist)
                {
                    channel.sendMessage("Adding **" + playlist.getTracks().size() +"** tracks to queue from playlist: " + playlist.getName()).queue();
                    tracks.forEach(mng.scheduler::queue);
                }
                else
                {
                    channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();
                    mng.scheduler.queue(firstTrack);
                }
            }

            @Override
            public void noMatches()
            {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception)
            {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private GuildMusicManager getMusicManager(Guild guild)
    {
        String guildId = guild.getId();
        GuildMusicManager mng = musicManagers.get(guildId);
        if (mng == null)
        {
            synchronized (musicManagers)
            {
                mng = musicManagers.get(guildId);
                if (mng == null)
                {
                    mng = new GuildMusicManager(playerManager);
                    mng.player.setVolume(DEFAULT_VOLUME);
                    musicManagers.put(guildId, mng);
                }
            }
        }
        return mng;
    }

    private static String getTimestamp(long milliseconds)
    {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours   = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        if (hours > 0)
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format("%02d:%02d", minutes, seconds);
    }

}
