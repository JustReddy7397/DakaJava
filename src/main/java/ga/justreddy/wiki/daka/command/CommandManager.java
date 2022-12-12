package ga.justreddy.wiki.daka.command;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.commands.giveaways.GStartCommand;
import ga.justreddy.wiki.daka.command.commands.misc.HelpCommand;
import ga.justreddy.wiki.daka.command.commands.moderation.BanCommand;
import ga.justreddy.wiki.daka.command.commands.moderation.HistoryCommand;
import ga.justreddy.wiki.daka.command.commands.reminders.RemindMeCommand;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.listener.interaction.SlashCommandCreateListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements SlashCommandCreateListener {

    private final List<Command> commands;

    public CommandManager() {
        Main.getClient().getApi().addListener(this);
        this.commands = new ArrayList<>();
        register(
                new BanCommand(),
                new HistoryCommand(),
                new HelpCommand(),
                new RemindMeCommand(),
                new GStartCommand()
        );
    }

    @Override
    public void onSlashCommandCreate(SlashCommandCreateEvent event) {
        Command command = getById(event.getSlashCommandInteraction().getCommandId());
        if (command == null) return;
        if (event.getInteraction().getServer().isEmpty()) return;
        final Server server = event.getInteraction().getServer().get();
        final User user = event.getInteraction().getUser();
        if (command.getPermission() != null && !server.hasPermission(user, command.getPermission())) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Invalid Permissions!")
                    .setDescription("You need the `"+command.getPermission().name()+"` permission to run this command.")
                    .setColor(Color.RED)
                    .setFooter(Main.getRandomDumbResponse());
            event.getInteraction().createImmediateResponder()
                    .addEmbed(embed)
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond();
            return;
        }
        command.onCommand(server, user, event.getInteraction(), event.getSlashCommandInteraction().getOptions());
    }

    private void register(Command... commands) {
        this.commands.addAll(Arrays.asList(commands));
    }

    private Command getById(long id) {
        return commands.stream().filter(command -> command.getId() == id).findFirst().orElse(null);
    }

    public List<Command> getCommands() {
        return commands;
    }
}
