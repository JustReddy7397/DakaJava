package ga.justreddy.wiki.daka.data;

public class Ticket {

    private long guildId;
    private long userId;
    private long creationDate;
    private long channelId;
    private long messageId;
    private long claimed;

    public Ticket(long guildId, long userId, long creationDate, long channelId, long messageId, long claimed) {
        this.guildId = guildId;
        this.userId = userId;
        this.creationDate = creationDate;
        this.channelId = channelId;
        this.messageId = messageId;
        this.claimed = claimed;
    }

    public long getGuildId() {
        return guildId;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getChannelId() {
        return channelId;
    }

    public void setChannelId(long channelId) {
        this.channelId = channelId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getClaimed() {
        return claimed;
    }

    public void setClaimed(long claimed) {
        this.claimed = claimed;
    }
}
