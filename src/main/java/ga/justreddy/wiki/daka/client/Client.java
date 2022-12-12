package ga.justreddy.wiki.daka.client;

import ga.justreddy.wiki.daka.Main;
import ga.justreddy.wiki.daka.command.CommandManager;
import ga.justreddy.wiki.daka.data.Giveaway;
import ga.justreddy.wiki.daka.tasks.GiveawayTask;
import ga.justreddy.wiki.daka.tasks.ReminderTask;

import java.util.logging.Level;

public class Client extends DiscordClient {

    private Thread remindersThread;
    private Thread giveawayThread;

    private CommandManager commandManager;

    public Client() {
        super(Main.getDotenv().get("TOKEN"));
        getLogger().log(Level.INFO, "[SYSTEM] Logged in.");
    }

    @Override
    public void onEnable() {
        commandManager = new CommandManager();
        remindersThread = new Thread(new ReminderTask());
        remindersThread.start();
        GiveawayTask task = new GiveawayTask(getApi());
        giveawayThread = new Thread(task);
        giveawayThread.start();
   }

    @Override
    public void onDisable() {
        commandManager.getCommands().clear();
        remindersThread.interrupt();
        getLogger().log(Level.INFO, "[SYSTEM] Logged out.");
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
