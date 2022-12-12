package ga.justreddy.wiki.daka.data;

public class Reminder {

    private int id;
    private long guildId;
    private long userId;
    private String reminder;
    private long time;
    private long channelId;

    public Reminder(int id, long guildId, long userId, String reminder, long time, long channelId) {
        this.id = id;
        this.guildId = guildId;
        this.userId = userId;
        this.reminder = reminder;
        this.time = time;
        this.channelId = channelId;
    }


    public int getId() {
        return id;
    }

    public long getGuildId() {
        return guildId;
    }

    public long getUserId() {
        return userId;
    }

    public String getReminder() {
        return reminder;
    }

    public long getTime() {
        return time;
    }

    public long getChannelId() {
        return channelId;
    }
}
