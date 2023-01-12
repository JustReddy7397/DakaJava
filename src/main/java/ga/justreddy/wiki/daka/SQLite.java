package ga.justreddy.wiki.daka;

import com.google.gson.Gson;
import ga.justreddy.wiki.daka.data.*;
import ga.justreddy.wiki.daka.utils.Utils;
import lombok.SneakyThrows;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.HighLevelComponent;
import org.javacord.api.entity.message.component.HighLevelComponentBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.permission.Permissions;
import org.javacord.api.entity.permission.PermissionsBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.util.logging.ExceptionLogger;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLite {

    private Connection connection;

    @SneakyThrows
    public SQLite() {
        this.connection = DriverManager.getConnection("jdbc:sqlite:data.db");
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS punishments (guildId LONG(100), userId LONG(100), punishment LONGTEXT)");
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS reminders (guildId LONG(100), userId LONG(100), reminder LONGTEXT)");
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS giveaways (guildId LONG(100), messageId LONG(100), giveaway LONGTEXT)");
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS settings (guildId LONG(100), settings LONGTEXT)");
        connection.createStatement().executeUpdate("DROP TABLE tickets");
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS tickets (guildId LONG, ticket LONGTEXT)");
    }

    @SneakyThrows
    public void insertPunishment(Punishment punishment) {
        String stringPunishment = Utils.toJson(punishment, Punishment.class);
        connection.createStatement().executeUpdate("INSERT INTO punishments (guildId, userId, punishment) VALUES ('" + punishment.getGuildId() + "', '" + punishment.getUserId() + "', '" + stringPunishment + "')");
    }

    @SneakyThrows
    public List<Punishment> getPunishmentsForUser(long guildId, long userId) {
        List<Punishment> punishments = new ArrayList<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM punishments WHERE guildId='" + guildId + "' AND userId='" + userId + "'");
        while (resultSet.next()) {
            punishments.add(Utils.fromJson(resultSet.getString("punishment"), Punishment.class));
        }
        return punishments;
    }

    @SneakyThrows
    public List<Punishment> getPunishments() {
        List<Punishment> punishments = new ArrayList<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM punishments");
        while (resultSet.next()) {
            punishments.add(Utils.fromJson(resultSet.getString("punishment"), Punishment.class));
        }
        return punishments;
    }

    public Punishment getPunishmentForUser(int id, long guildId, long userId) {
        return getPunishmentsForUser(guildId, userId).stream().filter(punishment -> punishment.getId() == id).findFirst().orElse(null);
    }

    @SneakyThrows
    public void insertReminder(Reminder reminder) {
        String reminderString = Utils.toJson(reminder, Reminder.class);

        connection.createStatement().executeUpdate("INSERT INTO reminders (guildId, userId, reminder) VALUES ('" + reminder.getGuildId() + "', '" + reminder.getUserId() + "', '" + reminderString + "')");
    }

    @SneakyThrows
    public List<Reminder> getRemindersForUser(long guildId, long userId) {
        List<Reminder> reminders = new ArrayList<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM reminders WHERE guildId='" + guildId + "' AND userId='" + userId + "'");
        while (resultSet.next()) {
            reminders.add(Utils.fromJson(resultSet.getString("reminder"), Reminder.class));
        }
        return reminders;
    }

    public Reminder getReminderForUser(int id, long guildId, long userId) {
        return getRemindersForUser(guildId, userId).stream().filter(reminder -> reminder.getId() == id).findFirst().orElse(null);
    }

    @SneakyThrows
    public List<Reminder> getReminders() {
        List<Reminder> reminders = new ArrayList<>();
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM reminders");
        while (resultSet.next()) {
            reminders.add(Utils.fromJson(resultSet.getString("reminder"), Reminder.class));
        }
        return reminders;
    }

    @SneakyThrows
    public void removeReminder(Reminder reminder) {
        connection.createStatement().executeUpdate("DELETE FROM reminders WHERE reminder='" + Utils.toJson(reminder, Reminder.class) + "'");
    }

    @SneakyThrows
    public void insertGiveaway(Giveaway giveaway) {
        String giveawayString = Utils.toJson(giveaway, Giveaway.class);
        connection.createStatement().executeUpdate("INSERT INTO giveaways (guildId, messageId, giveaway) VALUES ('" + giveaway.getGuildId() + "', '" + giveaway.getMessageId() + "','" + giveawayString + "')");
    }

    public List<Giveaway> getActiveGiveaways(long guildId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> !giveaway.isEnded()).collect(Collectors.toList());
    }

    public List<Giveaway> getActiveGiveaways() {
        return getAllGiveaways().stream().filter(giveaway -> !giveaway.isEnded()).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<Giveaway> getAllGiveaways() {
        List<Giveaway> list = new ArrayList<>();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM giveaways");
        while (rs.next()) {
            list.add(new Gson().fromJson(rs.getString("giveaway"), Giveaway.class));
        }
        return list;
    }

    @SneakyThrows
    public List<Giveaway> getGiveawaysForGuild(long guildId) {
        List<Giveaway> list = new ArrayList<>();
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM giveaways WHERE guildId='" + guildId + "'");
        while (rs.next()) {
            list.add(new Gson().fromJson(rs.getString("giveaway"), Giveaway.class));
        }
        return list;
    }

    public Giveaway getGiveaway(long guildId, long messageId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> giveaway.getMessageId() == messageId).findFirst().orElse(null);
    }

    @SneakyThrows
    public Giveaway updateGiveaway(long guildId, long messageId, Giveaway giveaway) {
        String stringGiveaway = new Gson().toJson(giveaway, Giveaway.class);
        connection.createStatement().executeUpdate("UPDATE giveaways SET giveaway='" + stringGiveaway + "' WHERE guildId='" + guildId + "' AND messageId='" + messageId + "'");
        return getGiveaway(guildId, messageId);
    }

    public Giveaway getActiveGiveaway(long guildId, long messageId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> giveaway.getMessageId() == messageId && !giveaway.isEnded()).findFirst().orElse(null);
    }

    public void endGiveaway(long guildId, long messageId) {

    }

    @SneakyThrows
    public Guild getGuildSettings(Server server) {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM settings WHERE guildId='" + server.getId() + "'");
        if (!resultSet.next()) {
            Guild guild = new Guild();
            insertGuild(server, guild);
            return guild;
        }
        return Utils.fromJson(resultSet.getString("settings"), Guild.class);
    }

    @SneakyThrows
    public void updateGuildSettings(Server server, Guild guild) {
        String guildString = new Gson().toJson(guild, Guild.class);
        connection.createStatement().executeUpdate("UPDATE settings SET settings='" + guildString + "' WHERE guildId='" + server.getId() + "'");
    }

    @SneakyThrows
    private void insertGuild(Server server, Guild guild) {
        String guildString = new Gson().toJson(guild, Guild.class);
        connection.createStatement().executeUpdate("INSERT INTO settings (guildId, settings) VALUES ('" + server.getId() + "', '" + guildString + "')");
    }

    @SneakyThrows
    public Ticket getTicket(Server server, long userId) {
        ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM tickets WHERE guildId='"+server.getId()+"'");
        if (!resultSet.next()) return null;
        Ticket ticket = null;
        while (resultSet.next()) {
            Ticket newTick = new Gson().fromJson(resultSet.getString("ticket"), Ticket.class);;
            if (newTick.getUserId() == userId) ticket = newTick;
        }
        return ticket;
    }

    @SneakyThrows
    public boolean createTicket(Server server, User user) {
        if (hasTicket(server, user)) return false;
        Guild settings = getGuildSettings(server);
        Role role = server.getRolesByName("@everyone").get(0);
        PermissionsBuilder builder = new PermissionsBuilder()
                .setDenied(PermissionType.CREATE_INSTANT_INVITE, PermissionType.CREATE_PUBLIC_THREADS, PermissionType.CREATE_PRIVATE_THREADS)
                        .setAllowed(PermissionType.ATTACH_FILE, PermissionType.SEND_MESSAGES, PermissionType.VIEW_CHANNEL, PermissionType.ADD_REACTIONS);
        server.createTextChannelBuilder().setCategory(server.getChannelCategoryById(settings.getTicketCategory()).orElse(null)).setName("ticket-" + user.getName())
                .addPermissionOverwrite(user, builder.build())
                .addPermissionOverwrite(role, new PermissionsBuilder().setAllDenied().build())
                .create()
                .thenAcceptAsync(channel -> {
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Ticket Created");
                    embed.setDescription("Thank you for creating a ticket! Staff will help you as soon as possible.");
                    embed.setColor(Color.GREEN);
                    HighLevelComponent component = ActionRow.of(
                            Button.danger("close_ticket", "Close Ticket")
                    );
                    channel.sendMessage(embed, component).thenAcceptAsync(message -> {
                        Ticket ticket = new Ticket(server.getId(), user.getId(), System.currentTimeMillis() / 1000, channel.getId(), message.getId(), 0L);
                        String ticketString = new Gson().toJson(ticket, Ticket.class);
                        try {
                            connection.createStatement().executeUpdate("INSERT INTO tickets (guildId, ticket) VALUES ('"+server.getId()+"', '"+ticketString+"')");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }).exceptionally(ExceptionLogger.get());

                }).exceptionally(ExceptionLogger.get());
        return true;
    }

    public boolean hasTicket(Server server, User user) {
        return getTicket(server, user.getId()) != null;
    }



}
