package ga.justreddy.wiki.daka.command.commands.moderation;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Punishment;
import ga.justreddy.wiki.daka.utils.Utils;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.awt.*;
import java.util.List;

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
        }catch (IndexOutOfBoundsException ignored) {}
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
