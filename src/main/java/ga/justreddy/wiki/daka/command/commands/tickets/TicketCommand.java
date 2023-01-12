package ga.justreddy.wiki.daka.command.commands.tickets;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.SQLite;
import ga.justreddy.wiki.daka.command.Command;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.javacord.api.interaction.SlashCommandOptionBuilder;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.List;

public class TicketCommand extends Command {

    public TicketCommand() {
        super("ticket", "Ticket Commands", "/ticket <arguments>", "tickets", null,
                new SlashCommandOptionBuilder()
                        .setName("create")
                        .setDescription("Create a ticket")
                        .setType(SlashCommandOptionType.SUB_COMMAND));
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        String name = options.get(0).getName();
        if (name.equals("create")) {
            if (!Main.getStorage().createTicket(server, user)) {
                interaction.createImmediateResponder().setFlags(MessageFlag.EPHEMERAL)
                        .setContent("You already have a ticket open!")
                        .respond();
                return;
            }
        }
    }
}
