package ga.justreddy.wiki.daka.command.commands.misc;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.Command;
import org.javacord.api.entity.message.MessageFlag;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.PermissionType;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.*;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "View all commands", "/help [category]", "misc", null,
                new SlashCommandOptionBuilder()
                        .setName("category")
                        .setDescription("View commands of a specific category")
                        .setRequired(false)
                        .setType(SlashCommandOptionType.STRING)
        );
    }

    @Override
    public void onCommand(Server server, User user, Interaction interaction, List<SlashCommandInteractionOption> options) {
        String type = null;
        try {
            type = options.get(0).getStringValue().get();
        } catch (IndexOutOfBoundsException ignored) {
        }
        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle("Help Menu")
                .setColor(Color.GREEN)
                .setFooter(Main.getRandomGoodResponse())
                .addField(":slight_smile: Misc", "/help misc", true)
                .addField(":hammer: Moderation", "/help moderation", true)
                .addField(":reminder_ribbon: Reminders", "/help reminder", true)
                .addField(":tada: Gveaways", "/help giveaway", true);
        if (type == null) {
            interaction.createImmediateResponder()
                    .addEmbed(helpEmbed)
                    .respond();
            return;
        }

        final StringBuilder miscBuilder = new StringBuilder(":slight_smile: **Misc**\n");
        final StringBuilder moderationBuilder = new StringBuilder(":hammer: **Moderation**\n");
        final StringBuilder reminderBuilder = new StringBuilder(":reminder_ribbon: **Reminder**\n");
        final StringBuilder giveawayBuilder = new StringBuilder(":tada: **Giveaway**");

        Main.getClient().getCommandManager().getCommands().forEach(command -> {

            if (command.getCategory().equals("misc")) {
                if (command.getPermission() != null) {
                    if (!server.hasPermission(user, command.getPermission())) return;
                }

                miscBuilder.append("/").append(command.getName()).append(" - ").append(command.getDescription()).append("\n");

            }

            if (command.getCategory().equals("moderation")) {
                if (command.getPermission() != null) {
                    if (!server.hasPermission(user, command.getPermission())) return;
                }

                moderationBuilder.append("/").append(command.getName()).append(" - ").append(command.getDescription()).append("\n");

            }

            if (command.getCategory().equals("reminders")) {
                if (command.getPermission() != null) {
                    if (!server.hasPermission(user, command.getPermission())) return;
                }

                reminderBuilder.append("/").append(command.getName()).append(" - ").append(command.getDescription()).append("\n");


            }

            if (command.getCategory().equals("giveaways")) {
                if (command.getPermission() != null) {
                    if (!server.hasPermission(user, command.getPermission())) return;
                }

                giveawayBuilder.append("/").append(command.getName()).append(" - ").append(command.getDescription()).append("\n");


            }

        });

        String finalType = type;
        Main.getClient().getCommandManager().getCommands().forEach(command -> {
            if (finalType.equals(command.getName().toLowerCase())) {
                String[] status = {
                        "**Name:** `" + command.getName() + "`",
                        "**Description:** `" + (command.getDescription() != null ? command.getDescription() + "`" : "`No Description`"),
                        "**Usage:** `" + (command.getUsage() != null ? command.getUsage() + "`" : "`No Usage`"),
                        "`<>` Indicates required parameters",
                        "`[]` Indicates optional parameters"
                };
                EmbedBuilder commandHelper = new EmbedBuilder()
                        .setTitle("Help Menu")
                        .setDescription(String.join("\n", status))
                        .setColor(Color.GREEN)
                        .setFooter(Main.getRandomGoodResponse());
                interaction.createImmediateResponder()
                        .addEmbed(commandHelper)
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond();
                return;
            }
        });



        EmbedBuilder categoryEmbed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setFooter("Use /help <commandName> to show more info about the command");

        if (finalType.equalsIgnoreCase("misc")) {
            categoryEmbed.setTitle("Misc Help Menu");
            categoryEmbed.setDescription(miscBuilder.toString());
        } else if (finalType.equalsIgnoreCase("moderation")) {
            categoryEmbed.setTitle("Moderation Help Menu");
            categoryEmbed.setDescription(moderationBuilder.toString());
        }else if (finalType.equals("reminder")) {
            categoryEmbed.setTitle("Reminder Help Menu");
            categoryEmbed.setDescription(reminderBuilder.toString());
        } else if (finalType.equals("giveaway")) {
            categoryEmbed.setTitle("Giveaway Help Menu");
            categoryEmbed.setDescription(giveawayBuilder.toString());
        } else {
            interaction.createImmediateResponder().addEmbed(helpEmbed).respond();
            return;
        }
        interaction.createImmediateResponder().addEmbed(categoryEmbed).respond();

    }
}
