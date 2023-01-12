package ga.justreddy.wiki.daka.listeners;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.data.Ticket;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.ButtonClickEvent;
import org.javacord.api.listener.interaction.ButtonClickListener;

public class TicketListener implements ButtonClickListener {

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getButtonInteraction().getServer().isEmpty()) return;
        if (event.getButtonInteraction().getCustomId().equals("close_ticket")) {
            User user = event.getButtonInteraction().getUser();
            Server server = event.getButtonInteraction().getServer().get();
            Ticket ticket = Main.getStorage().getTicket(server, user.getId());
            if (ticket == null) return;

        }
    }
}
