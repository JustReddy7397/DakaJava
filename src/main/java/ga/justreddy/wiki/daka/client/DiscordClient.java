package ga.justreddy.wiki.daka.client;

import ga.justreddy.wiki.daka.command.CommandManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.Javacord;
import org.javacord.api.entity.activity.ActivityType;

import java.net.Proxy;
import java.util.jar.JarEntry;
import java.util.logging.Logger;

public abstract class DiscordClient {

    private final DiscordApi api;

    public DiscordClient(String token) {
        api = new DiscordApiBuilder()
                .setToken(token)
                .setAllIntents()
                .login()
                .join();
        api.updateActivity(ActivityType.WATCHING, "Reddy");

        
    }

    public abstract void onEnable();

    public void onDisable() {};

    public DiscordApi getApi() {
        return api;
    }

    public Logger getLogger() {
        return Logger.getLogger("Daka");
    }



}
