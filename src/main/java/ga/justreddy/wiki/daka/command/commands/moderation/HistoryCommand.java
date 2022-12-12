package ga.justreddy.wiki.daka.command.commands.moderation;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Punishment;
import ga.justreddy.wiki.daka.utils.pagination.ButtonPaginator;
import ga.justreddy.wiki.daka.utils.Utils;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HistoryCommand extends Command {

    public HistoryCommand() {
        super("history", "Show a members punishment history!", "/history [member]", "moderation", PermissionType.KICK_MEMBERS,
                new SlashCommandOptionBuilder()
                        .setRequired(false)
                        .setType(SlashCommandOptionType.USER)
                        .setName("member")
                        .setDescription("The member you wanna show the history of.")
        );
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        User victim = user;
        try {
            victim = options.get(0).getUserValue().get();
        } catch (IndexOutOfBoundsException ignored) {
        }

        List<Punishment> list = Main.getStorage().getPunishmentsForUser(server.getId(), victim.getId());

        List<String> stringList = new ArrayList<>();

        for (Punishment punishment : list) {
            String builder = "**ID:** " + punishment.getId() +
                    "\n" +
                    "**Moderator:** <@" + punishment.getModerator() + ">" +
                    "\n" +
                    "**Reason:** " + punishment.getReason() +
                    "\n" +
                    "**Type:** " + punishment.getType() +
                    "\n" +
                    "**Duration:** " + (punishment.getTime() == -1L ? "Permanent" : Utils.getDurationString(punishment.getTime())) +
                    "\n";
            stringList.add(builder);
        }

        User finalVictim = victim;

        interaction.createImmediateResponder().setContent("Checking data...")
                .respond().thenAcceptAsync(updater -> {
                    ButtonPaginator.Builder builder = new ButtonPaginator.Builder(Main.getClient().getApi());
                    builder.setColor(Color.GREEN);
                    builder.setFooter(Main.getRandomGoodResponse());
                    builder.setItems(stringList);
                    builder.setItemsPerPage(3);
                    builder.setUpdater(updater);
                    builder.setTitle("History of " + finalVictim.getDiscriminatedName());
                    builder.setTimeout(3, TimeUnit.HOURS);
                    ButtonPaginator paginator = builder.build();
                    updater.update().thenAcceptAsync(message -> {
                        paginator.paginate(message, 1);
                    });
                });


    }


}
