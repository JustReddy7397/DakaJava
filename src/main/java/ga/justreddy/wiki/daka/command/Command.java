package ga.justreddy.wiki.daka.command;

import ga.justreddy.wiki.daka.Main;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.interaction.SlashCommandCreateEvent;
import org.javacord.api.interaction.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public abstract class Command {

    private final long id;
    private final String name;
    private final String description;
    private final String usage;
    private final String category;
    private final PermissionType permission;
    private final List<SlashCommandOptionBuilder> options;

    public Command(String name, String description, String usage, String category, PermissionType permission, SlashCommandOptionBuilder... options) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.permission = permission;
        this.options = Arrays.asList(options);
        SlashCommand command = SlashCommand.with(name, description, options)
                .createGlobal(Main.getClient().getApi())
                .join();
        Main.getClient().getLogger().log(Level.INFO, "[COMMAND] Registered command: /" + command.getName());
        this.id = command.getId();
    }

    public Command(String name, String description, String usage, String category, PermissionType permission) {
        this.name = name;
        this.description = description;
        this.usage = usage;
        this.category = category;
        this.permission = permission;
        this.options = new ArrayList<>();
        SlashCommand command = SlashCommand.with(name, description)
                .createForServer(Main.getClient().getApi(), 878285082564132914L)
                .join();

        Main.getClient().getLogger().log(Level.INFO, "[COMMAND] Registered command: /" + command.getName());
        this.id = command.getId();
    }

    public abstract void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public String getCategory() {
        return category;
    }

    public PermissionType getPermission() {
        return permission;
    }

    public List<SlashCommandOptionBuilder> getOptions() {
        return options;
    }


    public long getId() {
        return id;
    }
}
