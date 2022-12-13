package ga.justreddy.wiki.daka.logger;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.auditlog.AuditLog;
import org.javacord.api.entity.auditlog.AuditLogEntry;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.emoji.CustomEmoji;
import org.javacord.api.entity.emoji.Emoji;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;
import java.util.Map;

public class DiscordLogger {

    public void start(DiscordApi api) {

        api.addServerChannelCreateListener(event -> {

            if (event.getChannel().asServerThreadChannel().isPresent()) return;

            AuditLogEntry entry = event.getServer().getAuditLog(1).join().getEntries().get(0);

            Map<ChannelType, String> channelTypes = Map.of(
                    ChannelType.SERVER_TEXT_CHANNEL, "Text Channel",
                    ChannelType.SERVER_VOICE_CHANNEL, "Voice Channel",
                    ChannelType.UNKNOWN, "No Type",
                    ChannelType.SERVER_NEWS_CHANNEL, "News Channel",
                    ChannelType.SERVER_STORE_CHANNEL, "Store Channel",
                    ChannelType.SERVER_STAGE_VOICE_CHANNEL, "Stage Channel",
                    ChannelType.CHANNEL_CATEGORY, "Category"
            );

            sendLog(event.getServer(),
                    new EmbedBuilder()
                            .setTitle("Channel Created")
                            .setDescription(
                                    "**Channel**\n" +
                                            "Name: `" + event.getChannel().getName() + "`\n" +
                                            "ID: `" + event.getChannel().getId() + "`\n" +
                                            "Type: `" + channelTypes.get(event.getChannel().getType()) + "`\n" +
                                            "\n\n" +
                                            "**User**\n" +
                                            "Name: `" + entry.getUser().join().getDiscriminatedName() + "`\n" +
                                            "ID: `" + entry.getUser().join().getId() + "`"
                            )
                            .setColor(Color.GREEN)
                            .setThumbnail(entry.getUser().join().getAvatar())
            );

        });

        api.addServerChannelDeleteListener(event -> {

            if (event.getChannel().asServerThreadChannel().isPresent()) return;

            AuditLogEntry entry = event.getServer().getAuditLog(1).join().getEntries().get(0);

            Map<ChannelType, String> channelTypes = Map.of(
                    ChannelType.SERVER_TEXT_CHANNEL, "Text Channel",
                    ChannelType.SERVER_VOICE_CHANNEL, "Voice Channel",
                    ChannelType.UNKNOWN, "No Type",
                    ChannelType.SERVER_NEWS_CHANNEL, "News Channel",
                    ChannelType.SERVER_STORE_CHANNEL, "Store Channel",
                    ChannelType.SERVER_STAGE_VOICE_CHANNEL, "Stage Channel",
                    ChannelType.CHANNEL_CATEGORY, "Category"
            );

            sendLog(event.getServer(),
                    new EmbedBuilder()
                            .setTitle("Channel Deleted")
                            .setDescription(
                                    "**Channel**\n" +
                                            "Name: `" + event.getChannel().getName() + "`\n" +
                                            "ID: `" + event.getChannel().getId() + "`\n" +
                                            "Type: `" + channelTypes.get(event.getChannel().getType()) + "`\n" +
                                            "\n\n" +
                                            "**User**\n" +
                                            "Name: `" + entry.getUser().join().getDiscriminatedName() + "`\n" +
                                            "ID: `" + entry.getUser().join().getId() + "`"
                            )
                            .setColor(Color.RED)
                            .setThumbnail(entry.getUser().join().getAvatar())
            );
        });

        api.addKnownCustomEmojiCreateListener(event -> {

            CustomEmoji emoji = event.getEmoji();
            User user = event.getEmoji().getCreator().join().get();

            sendLog(event.getServer(),
                    new EmbedBuilder()
                            .setTitle("Emoji Created")
                            .setDescription(
                                    "**Emoji**\n" +
                                            "Name: `" + emoji.getName() + "`\n" +
                                            "ID: `" + emoji.getId() + "`\n" +
                                            "\n\n" +
                                            "**User**\n" +
                                            "Name: `" + user.getDiscriminatedName() + "`\n" +
                                            "ID: `" + user.getId() + "`"
                            )
                            .setColor(Color.GREEN)
                            .setThumbnail(user.getAvatar())
            );

        });



    }

    public void sendLog(Server server, EmbedBuilder embedBuilder) {
        TextChannel channel = server.getTextChannelById(878285082564132917L).orElse(null);
        System.out.println(channel);
        if (channel == null) return;
        channel.sendMessage(embedBuilder).join();
    }

}
