package ga.justreddy.wiki.daka;

import com.google.gson.Gson;
import ga.justreddy.wiki.daka.data.Giveaway;
import ga.justreddy.wiki.daka.data.Punishment;
import ga.justreddy.wiki.daka.data.Reminder;
import ga.justreddy.wiki.daka.utils.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLite {

    private Connection connection;

    public SQLite() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:data.db");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS punishments (guildId LONG(100), userId LONG(100), punishment LONGTEXT)");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS reminders (guildId LONG(100), userId LONG(100), reminder LONGTEXT)");
            connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS giveaways (guildId LONG(100), messageId LONG(100), giveaway LONGTEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertPunishment(Punishment punishment) {
        String stringPunishment = Utils.toJson(punishment, Punishment.class);
        try {
            connection.createStatement().executeUpdate("INSERT INTO punishments (guildId, userId, punishment) VALUES ('" + punishment.getGuildId() + "', '" + punishment.getUserId() + "', '" + stringPunishment + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Punishment> getPunishmentsForUser(long guildId, long userId) {
        List<Punishment> punishments = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM punishments WHERE guildId='" + guildId + "' AND userId='" + userId + "'");
            while (resultSet.next()) {
                punishments.add(Utils.fromJson(resultSet.getString("punishment"), Punishment.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public List<Punishment> getPunishments() {
        List<Punishment> punishments = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM punishments");
            while (resultSet.next()) {
                punishments.add(Utils.fromJson(resultSet.getString("punishment"), Punishment.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishments;
    }

    public Punishment getPunishmentForUser(int id, long guildId, long userId) {
        return getPunishmentsForUser(guildId, userId).stream().filter(punishment -> punishment.getId() == id).findFirst().orElse(null);
    }

    public void insertReminder(Reminder reminder) {
        String reminderString = Utils.toJson(reminder, Reminder.class);
        try {
            connection.createStatement().executeUpdate("INSERT INTO reminders (guildId, userId, reminder) VALUES ('" + reminder.getGuildId() + "', '" + reminder.getUserId() + "', '" + reminderString + "')");
        } catch (SQLException e) {
            e.printStackTrace();
            ;
        }
    }

    public List<Reminder> getRemindersForUser(long guildId, long userId) {
        List<Reminder> reminders = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM reminders WHERE guildId='" + guildId + "' AND userId='" + userId + "'");
            while (resultSet.next()) {
                reminders.add(Utils.fromJson(resultSet.getString("reminder"), Reminder.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public Reminder getReminderForUser(int id, long guildId, long userId) {
        return getRemindersForUser(guildId, userId).stream().filter(reminder -> reminder.getId() == id).findFirst().orElse(null);
    }

    public List<Reminder> getReminders() {
        List<Reminder> reminders = new ArrayList<>();
        try {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM reminders");
            while (resultSet.next()) {
                reminders.add(Utils.fromJson(resultSet.getString("reminder"), Reminder.class));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reminders;
    }

    public void removeReminder(Reminder reminder) {
        try {
            connection.createStatement().executeUpdate("DELETE FROM reminders WHERE reminder='" + Utils.toJson(reminder, Reminder.class) + "'");
        } catch (SQLException e) {
            e.printStackTrace();
            ;
        }
    }

    public void insertGiveaway(Giveaway giveaway) {
        try {
            String giveawayString = Utils.toJson(giveaway, Giveaway.class);
            connection.createStatement().executeUpdate("INSERT INTO giveaways (guildId, messageId, giveaway) VALUES ('" + giveaway.getGuildId() + "', '" + giveaway.getMessageId() + "','" + giveawayString + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Giveaway> getActiveGiveaways(long guildId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> !giveaway.isEnded()).collect(Collectors.toList());
    }

    public List<Giveaway> getActiveGiveaways() {
        return getAllGiveaways().stream().filter(giveaway -> !giveaway.isEnded()).collect(Collectors.toList());
    }

    public List<Giveaway> getAllGiveaways() {
        List<Giveaway> list = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM giveaways");
            while (rs.next()) {
                list.add(new Gson().fromJson(rs.getString("giveaway"), Giveaway.class));
            }
        }catch (SQLException exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public List<Giveaway> getGiveawaysForGuild(long guildId) {
        List<Giveaway> list = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM giveaways WHERE guildId='"+guildId+"'");
            while (rs.next()) {
                list.add(new Gson().fromJson(rs.getString("giveaway"), Giveaway.class));
            }
        }catch (SQLException exception) {
            exception.printStackTrace();
        }
        return list;
    }

    public Giveaway getGiveaway(long guildId, long messageId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> giveaway.getMessageId() == messageId).findFirst().orElse(null);
    }

    public Giveaway updateGiveaway(long guildId, long messageId, Giveaway giveaway) {
        try {
            String stringGiveaway = new Gson().toJson(giveaway, Giveaway.class);
            connection.createStatement().executeUpdate("UPDATE giveaways SET giveaway='"+stringGiveaway+"' WHERE guildId='"+guildId+"' AND messageId='"+messageId+"'");
        }catch (SQLException exception) {
            exception.printStackTrace();
        }
        return getGiveaway(guildId, messageId);
    }

    public Giveaway getActiveGiveaway(long guildId, long messageId) {
        return getGiveawaysForGuild(guildId).stream().filter(giveaway -> giveaway.getMessageId() == messageId && !giveaway.isEnded()).findFirst().orElse(null);
    }

    public void endGiveaway(long guildId, long messageId) {

    }

}
