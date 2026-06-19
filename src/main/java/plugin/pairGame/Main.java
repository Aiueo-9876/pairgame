package plugin.pairGame;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.pairGame.Command.GameStartCommand;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.WebSocket;

public final class Main extends JavaPlugin implements Listener {

    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void onEnable() {
        try {
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        GameStartCommand gameStartCommand = new GameStartCommand(this, sqlSessionFactory);
        getCommand("gamestart").setExecutor(gameStartCommand);
        getServer().getPluginManager().registerEvents(gameStartCommand, this);

    }
}
