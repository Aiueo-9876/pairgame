package plugin.pairGame;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.pairGame.Command.GameStartCommand;

import java.net.http.WebSocket;

public final class Main extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        GameStartCommand gameStartCommand = new GameStartCommand(this);
        getCommand("gamestart").setExecutor(gameStartCommand);
        getServer().getPluginManager().registerEvents(gameStartCommand, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
