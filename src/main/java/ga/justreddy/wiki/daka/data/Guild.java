package ga.justreddy.wiki.daka.data;

public class Guild {

    private boolean confirmModerationAction;
    private long logChannel;
    private boolean allowMultipleTickets;
    private long ticketCategory;
    private long panelMessageId;

    public Guild() {
        this.confirmModerationAction = false;
        this.logChannel = 0L;
        this.allowMultipleTickets = false;
        this.ticketCategory = 859711678224793611L;
        this.panelMessageId = 0L;
    }

    public void setConfirmModerationAction(boolean confirmModerationAction) {
        this.confirmModerationAction = confirmModerationAction;
    }

    public boolean isConfirmModerationAction() {
        return confirmModerationAction;
    }

    public void setLogChannel(long logChannel) {
        this.logChannel = logChannel;
    }

    public long getLogChannel() {
        return logChannel;
    }

    public boolean isAllowMultipleTickets() {
        return allowMultipleTickets;
    }

    public void setAllowMultipleTickets(boolean allowMultipleTickets) {
        this.allowMultipleTickets = allowMultipleTickets;
    }

    public long getTicketCategory() {
        return ticketCategory;
    }

    public void setTicketCategory(long ticketCategory) {
        this.ticketCategory = ticketCategory;
    }

    public long getPanelMessageId() {
        return panelMessageId;
    }

    public void setPanelMessageId(long panelMessageId) {
        this.panelMessageId = panelMessageId;
    }

}
