package ga.justreddy.wiki.daka.tasks;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.data.Giveaway;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.interaction.ButtonInteraction;
import org.javacord.api.listener.interaction.ButtonClickListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GiveawayTask implements Runnable, ButtonClickListener {

    private final DiscordApi api;

    public GiveawayTask(DiscordApi api) {
        this.api = api;
        api.addButtonClickListener(this);
    }

    @Override
    public void run() {
        List<Giveaway> giveaways = Main.getStorage().getActiveGiveaways();
        for (Giveaway giveaway : giveaways) {
            if (giveaway.isEnded()) continue;
            if (System.currentTimeMillis() < giveaway.getTime()) continue;
            Server server = api.getServerById(giveaway.getGuildId()).orElse(null);
            if (server == null) {
                giveaway.setEnded(true);
                Main.getStorage().updateGiveaway(giveaway.getGuildId(), giveaway.getMessageId(), giveaway);
                continue;
            }
            TextChannel channel = server.getTextChannelById(giveaway.getChannelId()).orElse(null);
            if (channel == null) {
                giveaway.setEnded(true);
                Main.getStorage().updateGiveaway(giveaway.getGuildId(), giveaway.getMessageId(), giveaway);
                continue;
            }

            Message message = channel.getMessageById(giveaway.getMessageId()).join();
            List<Long> users = giveaway.getParticipants();
            List<String> winners = new ArrayList<>();

            if (message != null && users.size() <= 0) {
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
                continue;
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

            if (message != null) {
                message.edit(finishedEmbed)
                        .join();
            }

            EmbedBuilder messageIdEmbed = new EmbedBuilder()
                    .setDescription("Giveaway [↗️](https://discord.com/channels/$"+giveaway.getGuildId()+"/"+giveaway.getChannelId()+"/"+giveaway.getMessageId()+"')");

            channel.sendMessage("Congratulations "+String.join(",", winners)+" ! You won `"+giveaway.getPrize()+"`", messageIdEmbed);
            giveaway.setEnded(true);
            Main.getStorage().updateGiveaway(giveaway.getGuildId(), giveaway.getMessageId(), giveaway);
        }

        synchronized (this) {
            try {
                wait(5000);
            }catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        run();
    }


    @Override
    public void onButtonClick(ButtonClickEvent event) {
        final ButtonInteraction interaction = event.getButtonInteraction();
        if (interaction.getServer().isEmpty()) return;
        final String customId = interaction.getCustomId();
        final User clicker = interaction.getUser();
        final long messageId = interaction.getMessage().getId();
        final long guildId = interaction.getServer().get().getId();
        if (customId.equals("giveaway_enter")) {
            Giveaway giveaway = Main.getStorage().getGiveaway(guildId, messageId);
            if (giveaway == null) return;
            if (giveaway.isEnded()) return;
            if (giveaway.isParticipating(clicker.getId())) {
                interaction.createImmediateResponder()
                        .setContent("You've already entered this giveaway!")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .addComponents(ActionRow.of(Button.danger("giveaway_leave", "Leave Giveaway")))
                        .respond()
                        .thenAcceptAsync(updater -> {
                            updater.update().thenAcceptAsync(message -> {
                                message.addButtonClickListener(e -> {
                                    if (e.getButtonInteraction().getCustomId().equals("giveaway_leave")) {
                                        Giveaway newGiveaway = Main.getStorage().getGiveaway(guildId, messageId);
                                        if (newGiveaway == null) return;
                                        if (newGiveaway.isEnded()) return;
                                        if (!newGiveaway.isParticipating(clicker.getId())) return;
                                        newGiveaway.getParticipants().remove(clicker.getId());
                                        Giveaway updatedGiveaway = Main.getStorage().updateGiveaway(guildId, messageId, newGiveaway);
                                        interaction.getMessage().edit(
                                                new EmbedBuilder()
                                                        .setTitle(":tada: Giveaway :tada:")
                                                        .setDescription(
                                                                "**Hosted by:** <@" + updatedGiveaway.getHostId() + ">\n\n" +
                                                                        "**Participants:** " + updatedGiveaway.getParticipants().size() + "\n" +
                                                                        "**Prize:** " + updatedGiveaway.getPrize() + "\n" +
                                                                        "**Winners:** " + updatedGiveaway.getWinners() + "\n" +
                                                                        "**Ends:** <t:" + (updatedGiveaway.getTime() / 1000) + ":R>"
                                                        )
                                                        .setColor(Color.GREEN)
                                                        .setFooter("Click to button to join!")
                                        ).join();
                                        e.getInteraction().createImmediateResponder()
                                                .respond();
                                    }
                                });
                            });
                        });
                return;
            }

            giveaway.addParticipant(clicker.getId());
            Giveaway updatedGiveaway = Main.getStorage().updateGiveaway(guildId, messageId, giveaway);
            interaction.getMessage().edit(
                    new EmbedBuilder()
                            .setTitle(":tada: Giveaway :tada:")
                            .setDescription(
                                    "**Hosted by:** <@" + updatedGiveaway.getHostId() + ">\n\n" +
                                            "**Participants:** " + updatedGiveaway.getParticipants().size() + "\n" +
                                            "**Prize:** " + updatedGiveaway.getPrize() + "\n" +
                                            "**Winners:** " + updatedGiveaway.getWinners() + "\n" +
                                            "**Ends:** <t:" + (updatedGiveaway.getTime() / 1000) + ":R>"
                            )
                            .setColor(Color.GREEN)
                            .setFooter("Click to button to join!")
            ).join();
            interaction.createImmediateResponder()
                    .respond();

        }
    }
}
