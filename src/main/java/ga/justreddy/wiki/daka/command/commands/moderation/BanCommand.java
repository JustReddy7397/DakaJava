package ga.justreddy.wiki.daka.command.commands.moderation;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.SQLite;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Guild;
import ga.justreddy.wiki.daka.data.Punishment;
import ga.justreddy.wiki.daka.utils.Utils;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.util.event.ListenerManager;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BanCommand extends Command {
    public BanCommand() {
        super("ban", "Ban a member.", "/ban <member> [reason]", "moderation", PermissionType.BAN_MEMBERS,
                new SlashCommandOptionBuilder()
                        .setName("member")
                        .setDescription("The member you want to ban!")
                        .setType(SlashCommandOptionType.USER)
                        .setRequired(true)
                ,
                new SlashCommandOptionBuilder()
                        .setName("reason")
                        .setDescription("The reason for this ban!")
                        .setType(SlashCommandOptionType.STRING)

        );
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        User victim = options.get(0).getUserValue().get();
        String reason = "Reason Not Specified";
        try {
            reason = options.get(1).getStringValue().get();
        } catch (IndexOutOfBoundsException ignored) {
        }
        if (victim.getId() == user.getId()) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setDescription("You can't ban yourself dummy!")
                                    .setColor(Color.RED)
                                    .setFooter(Main.getRandomDumbResponse())
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }


        if (Utils.compareRoles(server, user, victim) <= 0) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setDescription("You can't ban a member that has a higher or the same role as you dummy!")
                                    .setColor(Color.RED)
                                    .setFooter(Main.getRandomDumbResponse())
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        Guild settings = Main.getStorage().getGuildSettings(server);

        if (settings == null) {
            interaction.createImmediateResponder().addEmbed(new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Error")
                    .setDescription("An error has occurred: `GuildSettings is null.`")
                    .setFooter("Please report this to Reddy#4766")
            ).respond();
            return;
        }

        if (settings.isConfirmModerationAction()) {

            String finalReason = reason;
            interaction.createImmediateResponder().addEmbed(
                            new EmbedBuilder()
                                    .setTitle("Confirmation")
                                    .setDescription("Do you wanna ban this user? (" + victim.getMentionTag() + ")")
                                    .setColor(Color.ORANGE)
                                    .setFooter("Disable confirmation -> /config set confirmation false")
                    ).addComponents(ActionRow.of(Button.success("confirm_moderation", "✅"), Button.danger("deny_moderation", "❌")))
                    .respond().thenAcceptAsync(updater -> {
                        updater.update().thenAcceptAsync(message -> {
                            ListenerManager<ButtonClickListener> listener = message.addButtonClickListener(e -> {
                                if (e.getButtonInteraction().getUser().getId() != user.getId()) return;
                                switch (e.getButtonInteraction().getCustomId()) {
                                    case "confirm_moderation" -> {
                                        punish(server, user, e.getInteraction(), victim, finalReason);
                                    }
                                    case "deny_moderation" -> {
                                        e.getInteraction().createImmediateResponder()
                                                .addEmbed(new EmbedBuilder()
                                                        .setColor(Color.RED)
                                                        .setDescription("Successfully cancelled."))
                                                .respond();
                                        e.getButtonInteraction().getMessage().delete();
                                    }
                                }
                            });
                            listener.removeAfter(30, TimeUnit.SECONDS);
                        });
                    });

            return;
        }

        punish(server, user, interaction, victim, reason);
    }

    private void punish(Server server, User user, Interaction interaction, User victim, String reason) {
        Main.getStorage().insertPunishment(new Punishment((int) (Math.floor(Math.random() * 999) + 1), server.getId(), victim.getId(), user.getId(), reason, "Ban", -1L));
        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setDescription("Successfully banned " + victim.getMentionTag() + " for: " + reason)
                                .setColor(Color.GREEN)
                                .setFooter(Main.getRandomGoodResponse())
                )
                .respond();
        server.banUser(victim);
    }
}
