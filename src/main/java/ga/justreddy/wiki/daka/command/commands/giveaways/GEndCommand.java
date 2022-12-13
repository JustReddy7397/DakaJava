package ga.justreddy.wiki.daka.command.commands.giveaways;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Giveaway;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.exception.UnknownMessageException;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GEndCommand extends Command {

    public GEndCommand() {
        super("gend", "End a giveaway", "/gend <messageId>", "giveaways", PermissionType.MANAGE_CHANNELS,
                new SlashCommandOptionBuilder()
                        .setRequired(true)
                        .setType(SlashCommandOptionType.STRING)
                        .setName("messageId")
                        .setDescription("The messageId of the giveaway"));
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        long messageId = 0;
        try {
            messageId = Long.parseLong(options.get(0).getStringValue().orElse("0"));
        } catch (NumberFormatException exception) {

        }
        TextChannel channel = interaction.getChannel().orElse(null);
        if (channel == null) return;
        Message message = null;
        try {
             message = channel.getMessageById(messageId).join();
        }catch (Exception ignored) {

        }
        if (message == null) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setDescription("A message with this ID does not exists!")
                                    .setColor(Color.RED)
                                    .setFooter(Main.getRandomDumbResponse())
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        Giveaway giveaway = Main.getStorage().getGiveaway(server.getId(), messageId);
        if (giveaway == null) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setDescription("This message is not a giveaway!")
                                    .setColor(Color.RED)
                                    .setFooter(Main.getRandomDumbResponse())
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        if (giveaway.isEnded()) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setDescription("This giveaway has already ended!")
                                    .setColor(Color.RED)
                                    .setFooter(Main.getRandomDumbResponse())
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        List<Long> users = giveaway.getParticipants();
        List<String> winners = new ArrayList<>();

        if (users.size() <= 0) {
            giveaway.setEnded(true);
            Main.getStorage().updateGiveaway(giveaway.getGuildId(), giveaway.getMessageId(), giveaway);
            message.edit(
                    new EmbedBuilder()
                            .setTitle(":tada: Giveaway Ended :tada:")
                            .setDescription(
                                    "**Hosted by:** <@" + giveaway.getHostId() + ">\n\n" +
                                            "**Prize:** " + giveaway.getPrize() + "\n" +
                                            "**Winners:** None\n" +
                                            "**Reason:** Not enough people participated."
                            )
                            .setColor(Color.RED)

            ).join();
        }
        int count = 0;
        for (long userId : users) {
            if (winners.contains("<@" + userId + ">")) continue;
            count++;
            winners.add("<@" + userId + ">");
            if (count >= giveaway.getWinners()) break;
        }

        EmbedBuilder finishedEmbed = new EmbedBuilder();
        finishedEmbed.setTitle(":tada: Giveaway Ended :tada:");
        finishedEmbed.setDescription(
                        "**Hosted by:** <@" + giveaway.getHostId() + ">\n\n" +
                                "**Prize:** " + giveaway.getPrize() + "\n" +
                                "**Winners:** " + String.join(", ", winners)
                )
                .setColor(Color.GREEN);

        message.edit(finishedEmbed)
                .join();


        EmbedBuilder messageIdEmbed = new EmbedBuilder()
                .setDescription("Giveaway [↗️](https://discord.com/channels/$" + giveaway.getGuildId() + "/" + giveaway.getChannelId() + "/" + giveaway.getMessageId() + "')");

        channel.sendMessage("Congratulations " + String.join(",", winners) + " ! You won `" + giveaway.getPrize() + "`", messageIdEmbed);
        giveaway.setEnded(true);
        Main.getStorage().updateGiveaway(giveaway.getGuildId(), giveaway.getMessageId(), giveaway);
        interaction.createImmediateResponder().respond();

    }
}
