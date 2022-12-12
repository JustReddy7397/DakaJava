package ga.justreddy.wiki.daka.tasks;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.data.Reminder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.awt.*;

public class ReminderTask implements Runnable {

    @Override
    public void run() {
        for (Reminder reminder : Main.getStorage().getReminders()) {
            if (reminder == null) continue;
            if (System.currentTimeMillis() <= reminder.getTime()) continue;
            Server server = Main.getClient().getApi().getServerById(reminder.getGuildId()).orElse(null);
            if (server == null) continue;
            User user = server.getMemberById(reminder.getUserId()).orElse(null);
            if (user == null) continue;
            EmbedBuilder builder = new EmbedBuilder()
                    .setAuthor(user.getDiscriminatedName(), null, user.getAvatar().getUrl().toString())
                    .setTitle("Reminder")
                    .setDescription("Don't forget to:\n " + "```" + reminder.getReminder() + "```")
                    .setColor(Color.GREEN)
                    .setFooter("ID: " + reminder.getId());
            TextChannel channel = server.getTextChannelById(reminder.getChannelId()).orElse(null);
            if (channel == null) channel = server.getTextChannels().get(0).asTextChannel().orElse(null);
            if (channel == null) continue;
            channel.sendMessage(builder);
            Main.getStorage().removeReminder(reminder);
        }
        synchronized (this) {
            try {
                wait(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        run();
    }
}
