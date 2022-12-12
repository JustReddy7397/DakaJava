package ga.justreddy.wiki.daka.data;

public class Punishment  {

    private int id;
    private long guildId;
    private long userId;
    private long moderator;
    private String reason;
    private String type;
    private long time;

    public Punishment(int id, long guildId, long userId, long moderator, String reason, String type, long time) {
        this.id = id;
        this.guildId = guildId;
        this.userId = userId;
        this.moderator = moderator;
        this.reason = reason;
        this.type = type;
        this.time = time;
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

    public long getModerator() {
        return moderator;
    }

    public String getReason() {
        return reason;
    }

    public String getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

}
