package ga.justreddy.wiki.daka.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Giveaway {

    private long guildId;
    private long channelId;
    private boolean ended;
    private long time;
    private long messageId;
    private String prize;
    private int winners;
    private long hostId;
    private List<Long> participants;

    public Giveaway(long guildId, long channelId, boolean ended, long time, long messageId, String prize, int winners, long hostId) {
        this.guildId = guildId;
        this.channelId = channelId;
        this.ended = ended;
        this.time = time;
        this.messageId = messageId;
        this.prize = prize;
        this.winners = winners;
        this.hostId = hostId;
        this.participants = new ArrayList<>();
    }

    public long getGuildId() {
        return guildId;
    }

    public long getChannelId() {
        return channelId;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    public long getTime() {
        return time;
    }

    public long getMessageId() {
        return messageId;
    }

    public String getPrize() {
        return prize;
    }

    public int getWinners() {
        return winners;
    }

    public long getHostId() {
        return hostId;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void addParticipant(long userId) {
        participants.add(userId);
    }

    public boolean isParticipating(long userId) {
        return participants.contains(userId);
    }

    public void removeParticipant(long userId) {
        participants.remove(userId);
    }

}
