package ga.justreddy.wiki.daka.command.commands.reminders;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import ga.justreddy.wiki.daka.data.Reminder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import java.util.List;

public class ReminderListCommand extends Command {

    public ReminderListCommand() {
        super("reminderlist", "Shows your reminders", "/reminderlist", "reminders", null);
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        List<Reminder> reminders = Main.getStorage().getRemindersForUser(server.getId(), user.getId());



    }
}
