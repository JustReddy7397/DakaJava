package ga.justreddy.wiki.daka.command.commands.reminders;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Reminder;
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

public class RemindMeCommand extends Command {

    public RemindMeCommand() {
        super("remindme", "Let the bot remind you after some time.", "/remindme <time> <reminder>", "reminders", null,
                new SlashCommandOptionBuilder()
                        .setRequired(true)
                        .setName("time")
                        .setType(SlashCommandOptionType.STRING)
                        .setDescription("Reminder Time. Example: 1d3h20m50s"),
                new SlashCommandOptionBuilder()
                        .setRequired(true)
                        .setName("reminder")
                        .setType(SlashCommandOptionType.STRING)
                        .setDescription("What the bot needs to remind you of")
                );
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        if (server == null) return;
        long ms = Utils.getDurationMS(options.get(0).getStringValue().orElse("0s"));
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

        String reminder = options.get(1).getStringValue().get();

        if (reminder.length() > 248) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setFooter(Main.getRandomDumbResponse())
                                    .setColor(Color.RED)
                                    .setDescription("Sorry, but for embed limiting reasons your reminder can't be longer than 248 words!")
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        int id = (int) Math.floor(Math.random() * 999) + 1;

        int reminders = Main.getStorage().getRemindersForUser(server.getId(), user.getId()).size();

        if (reminders >= 3) {
            interaction.createImmediateResponder()
                    .addEmbed(
                            new EmbedBuilder()
                                    .setFooter(Main.getRandomDumbResponse())
                                    .setColor(Color.RED)
                                    .setDescription("You've exceeded your limit of **3** reminders.")
                    )
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }

        Main.getStorage().insertReminder(new Reminder(id, server.getId(), user.getId(), reminder, ms, interaction.getChannel().get().getId()));

        interaction.createImmediateResponder()
                .addEmbed(
                        new EmbedBuilder()
                                .setTitle("Reminder set")
                                .setDescription("I will remind you in " + Utils.getDurationString(ms + 1))
                                .setColor(Color.GREEN)
                                .setFooter(Main.getRandomGoodResponse())
                )
                .respond();

    }


}
