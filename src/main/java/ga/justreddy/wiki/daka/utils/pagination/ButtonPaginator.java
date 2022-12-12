package ga.justreddy.wiki.daka.utils.pagination;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.Icon;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.javacord.api.listener.interaction.ButtonClickListener;
import org.javacord.api.util.event.ListenerManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ButtonPaginator {

    private static final Button first = Button.secondary("first", null, "⏮️");
    private static final Button previous = Button.secondary("previous", null, "⬅");
    private static final Button next = Button.secondary("next", null, "➡");
    private static final Button last = Button.secondary("last", null, "⏭");
    private static final Button delete = Button.danger("stop", null, "👎");

    private final InteractionOriginalResponseUpdater updater;
    private final int itemsPerPage;
    private final int pages;
    private final long timeout;
    private final String[] items;
    private final DiscordApi api;
    private final Set<Long> allowedUsers;
    private final boolean numbered;
    private final String title;
    private final Color color;
    private final String footer;

    private int page = 1;
    private boolean interactionStopped = false;

    private ButtonPaginator(InteractionOriginalResponseUpdater uodater, long timeout, String[] items, DiscordApi api,
                            Set<Long> allowedUsers, int itemsPerPage, boolean numberedItems, String title, Color color, String footer) {
        this.updater = uodater;
        this.timeout = timeout;
        this.items = items;
        this.api = api;
        this.allowedUsers = Collections.unmodifiableSet(allowedUsers);
        this.itemsPerPage = itemsPerPage;
        this.numbered = numberedItems;
        this.title = title;
        this.color = color;
        this.footer = footer;
        this.pages = (int) Math.ceil((double) items.length / itemsPerPage);
    }

    public void paginate(Message message, int page) {
        this.page = page;
        message.edit("\u200E", getEmbed(page)).thenAcceptAsync(msg -> {
            updater.addComponents(getButtonLayout(page)).update()
                    .thenAcceptAsync(updatedMessage -> {
                        waitForEvent(0, updatedMessage.getId());
                    });
        });
    }

    private ActionRow getButtonLayout(int page) {
        if (pages > 2)
            return ActionRow.of(
                    page <= 1 ? Button.secondary("first", null, "⏮️", true) : first,
                    page <= 1 ? Button.secondary("previous", null, "⬅", true) : previous,
                    page >= pages ? Button.secondary("next", null, "➡", true) : next,
                    page >= pages ? Button.secondary("last", null, "⏭", true) : last,
                    delete);
        else
            return ActionRow.of(
                    page <= 1 ? Button.secondary("previous", null, "⬅", true) : previous,
                    page >= pages ? Button.secondary("next", null, "➡", true) : next,
                    delete);
    }

    private void waitForEvent(long channelId, long messageId) {
        updater.update().thenAccept(message -> {
            ListenerManager<ButtonClickListener> listener = message.addButtonClickListener(e -> {
                if (interactionStopped) return;
                if (messageId != message.getId()) return;
                if (allowedUsers.size() > 1) {
                    if (!allowedUsers.contains(e.getButtonInteraction().getUser().getId())) {
                        e.getInteraction().createImmediateResponder()
                                .setFlags(MessageFlag.EPHEMERAL)
                                .setContent("You are not allowed to click this!")
                                .respond();
                    }
                    return;
                }
                switch (e.getButtonInteraction().getCustomId()) {
                    case "previous" -> {
                        page--;
                        if (page < 1) page = 1;
                        message.edit(getEmbed(this.page)).thenAcceptAsync(msg -> {
                            updater.removeAllComponents().update().join();
                            updater.addComponents(getButtonLayout(this.page)).update().join();
                        });
                    }
                    case "next" -> {
                        page++;
                        if (page > pages) page = pages;
                        message.edit(getEmbed(this.page)).thenAcceptAsync(msg -> {
                            updater.removeAllComponents().update().join();
                            updater.addComponents(getButtonLayout(this.page)).update().join();
                        });
                    }
                    case "stop" -> {
                        interactionStopped = true;
                        message.edit(getEmbed(page))
                                .thenAcceptAsync(msg -> {
                                    updater.removeAllComponents().update().join();
                                });
                    }
                    case "first" -> {
                        page = 1;
                        message.edit(getEmbed(this.page)).thenAcceptAsync(msg -> {
                            updater.removeAllComponents().update().join();
                            updater.addComponents(getButtonLayout(this.page)).update().join();
                        });
                    }
                    case "last" -> {
                        page = pages;
                        message.edit(getEmbed(this.page)).thenAcceptAsync(msg -> {
                            updater.removeAllComponents().update().join();
                            updater.addComponents(getButtonLayout(this.page)).update().join();
                        });
                    }
                }
                e.getInteraction().createImmediateResponder().respond();
            });
            message.addMessageDeleteListener(event -> listener.remove());
        });
    }

    private EmbedBuilder getEmbed(int page) {
        if (page > pages) page = pages;
        if (page < 1) page = 1;
        int start = page == 1 ? 0 : ((page - 1) * itemsPerPage);
        int end = Math.min(items.length, page * itemsPerPage);
        StringBuilder sb = new StringBuilder();
        for (int i = start; i < end; i++) {
            sb.append(numbered ? "**" + (i + 1) + ".** " : "").append(this.items[i]).append("\n");
        }
        return new EmbedBuilder()
                .setFooter("Page " + page + "/" + pages + (footer != null ? " • " + footer : ""))
                .setColor(color)
                .setAuthor(this.title)
                .setDescription(sb.toString().trim());
    }

    public static class Builder {
        private final DiscordApi api;
        private InteractionOriginalResponseUpdater updater;
        private long timeout = -1;
        private String[] items;
        private final Set<Long> allowedUsers = new HashSet<>();
        private int itemsPerPage = 10;
        private boolean numberItems = true;
        private String title = null;
        private Color color;
        private String footer;

        public Builder(DiscordApi api) {
            this.api = api;
        }

        public Builder setUpdater(InteractionOriginalResponseUpdater updater) {
            this.updater = updater;
            return this;
        }

        public Builder setTimeout(long delay, TimeUnit unit) {
            timeout = unit.toSeconds(delay);
            return this;
        }

        public Builder setItems(String[] items) {
            this.items = items;
            return this;
        }

        public Builder setItems(List<String> items) {
            String[] convertedItems = new String[items.size()];
            int x = 0;
            while (x < items.size()) {
                convertedItems[x] = items.get(x);
                x++;
            }
            this.items = convertedItems;
            return this;
        }

        public Builder addAllowedUsers(Long... userIds) {
            allowedUsers.addAll(Set.of(userIds));
            return this;
        }

        public Builder setColor(Color color) {
            this.color = color;
            return this;
        }

        public Builder setItemsPerPage(int items) {
            this.itemsPerPage = items;
            return this;
        }

        public Builder useNumberedItems(boolean b) {
            this.numberItems = b;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setFooter(String footer) {
            this.footer = footer;
            return this;
        }

        public ButtonPaginator build() {
            return new ButtonPaginator(updater, timeout, items, api, allowedUsers, itemsPerPage, numberItems, title, color == null ? Color.black : color, footer);
        }
    }


}
