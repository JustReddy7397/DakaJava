package ga.justreddy.wiki.daka.command.commands.giveaways;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.SQLite;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Giveaway;
import ga.justreddy.wiki.daka.utils.Utils;
import org.javacord.api.entity.channel.ChannelType;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.*;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.awt.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

public class GStartCommand extends Command {

    public GStartCommand() {
        super("gstart", "Start a giveaway", "/gstart <channel> <time> <prize> <winners>", "giveaways", PermissionType.MANAGE_CHANNELS,
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.CHANNEL)
                        .setChannelTypes(Arrays.asList(ChannelType.getTextChannelTypes()))
                        .setName("channel")
                        .setDescription("The channel to host the giveaway in.")
                        .setRequired(true),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("time")
                        .setDescription("How long the giveaway has to last for. Example: 1d12h5m")
                        .setRequired(true),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.STRING)
                        .setName("prize")
                        .setDescription("The prize of the giveaway.")
                        .setRequired(true),
                new SlashCommandOptionBuilder()
                        .setType(SlashCommandOptionType.DECIMAL)
                        .setName("winners")
                        .setDescription("The amount of winners that can win.")
                        .setDecimalMaxValue(100)
                        .setDecimalMinValue(1)
                        .setRequired(true)
        );
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        TextChannel channel = options.get(0).getChannelValue().get().asTextChannel().get();
        long ms = Utils.getDurationMS(options.get(1).getStringValue().orElse("0s"));
        if (ms == 0) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setFooter(Main.getRandomDumbResponse())
                                    .setColor(Color.RED)
                                    .setDescription("Invalid time format! Examples: `1h20m`, `20m10s`, `49s`")
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        String prize = options.get(2).getStringValue().get();
        int winners = (int) Math.round(options.get(3).getDecimalValue().get());
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(":tada: Giveaway :tada:")
                .setDescription(
                        "**Hosted by:** " + user.getMentionTag() + "\n\n" +
                                "**Participants:** " + 0 + "\n" +
                                "**Prize:** " + prize + "\n" +
                                "**Winners:** " + winners + "\n" +
                                "**Ends:** <t:" + (ms / 1000) + ":R>"
                )
                .setColor(Color.GREEN)
                .setFooter("Click to button to join!");

        channel.sendMessage(embed, ActionRow.of(Button.primary("giveaway_enter", "Join Giveaway", "\uD83C\uDF89")))
                        .thenAcceptAsync(message -> {
                            Main.getStorage().insertGiveaway(
                                    new Giveaway(
                                            server.getId(),
                                            channel.getId(),
                                            false,
                                            ms,
                                            message.getId(),
                                            prize,
                                            winners,
                                            user.getId()
                                    )
                            );
                        });
        interaction.createImmediateResponder().setContent("Successfully created a giveaway in the channel: <#" + channel.getId() + ">").respond();
    }
}
