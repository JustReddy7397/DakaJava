package ga.justreddy.wiki.daka.client;

import ga.justreddy.wiki.daka.command.CommandManager;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.Javacord;

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
