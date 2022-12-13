package ga.justreddy.wiki.daka.client;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.CommandManager;
import ga.justreddy.wiki.daka.data.Giveaway;
import ga.justreddy.wiki.daka.logger.DiscordLogger;
import ga.justreddy.wiki.daka.tasks.GiveawayTask;
import ga.justreddy.wiki.daka.tasks.ReminderTask;

import java.util.Timer;
import java.util.logging.Level;

public class Client extends DiscordClient {

    private Timer remindersTimer;
    private Timer giveawayTimer;

    private CommandManager commandManager;

    public Client() {
        super(Main.getDotenv().get("TOKEN"));
        getLogger().log(Level.INFO, "[SYSTEM] Logged in.");
    }

    @Override
    public void onEnable() {
        commandManager = new CommandManager();
        remindersTimer = new Timer();
        remindersTimer.schedule(new ReminderTask(), 0, 5000);
        GiveawayTask giveawayTask = new GiveawayTask(getApi());
        giveawayTimer = new Timer();
        giveawayTimer.schedule(giveawayTask, 0, 5000);
        new DiscordLogger().start(getApi());
   }

    @Override
    public void onDisable() {
        commandManager.getCommands().clear();
        remindersTimer.cancel();
        giveawayTimer.cancel();
        getLogger().log(Level.INFO, "[SYSTEM] Logged out.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
