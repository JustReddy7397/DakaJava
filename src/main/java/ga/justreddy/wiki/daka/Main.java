package ga.justreddy.wiki.daka;

import ga.justreddy.wiki.daka.client.Client;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

import java.io.File;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private static Client client;
    private static Dotenv dotenv;
    private static SQLite storage;

    private static final List<String> DUMB_RESPONSES = List.of("I can't believe you did that...", "I can't believe you...", "I can't believe you actually did that...", "Are you stupid?",
            "Did you really just try that?", "How dumb can you be...", "Better luck next time!", "You are so dumb for thinking you could run this!");

    private static final List<String> GOOD_RESPONSES = List.of("Damn, you actually did something correct!", "Great job!", "Celebrating for you doing something correct!", "Did you know? No me neither!",
            "Did you just one shot that?", "Did you just one shot that? Here is a cookie!");

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static void main(String[] args) {
        storage = new SQLite();
        try {
            dotenv = new DotenvBuilder().directory(
                    new File(Main.class.getClassLoader().getResource(".env").toURI()).getAbsolutePath()
            ).load();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        client = new Client();
        client.onEnable();
    }

    public static Client getClient() {
        return client;
    }

    public static Dotenv getDotenv() {
        return dotenv;
    }

    public static SQLite getStorage() {
        return storage;
    }

    public static String getRandomDumbResponse() {
        return DUMB_RESPONSES.get(RANDOM.nextInt(DUMB_RESPONSES.size()));
    }

    public static String getRandomGoodResponse() {
        return GOOD_RESPONSES.get(RANDOM.nextInt(GOOD_RESPONSES.size()));
    }

}
