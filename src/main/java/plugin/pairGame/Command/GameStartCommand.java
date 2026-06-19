package plugin.pairGame.Command;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jspecify.annotations.NonNull;
import plugin.pairGame.Main;
import plugin.pairGame.mapper.PlayerScore;
import plugin.pairGame.mapper.PlayerScoreMapper;

import java.io.IOException;
import java.io.InputStream;

import java.util.*;

public class GameStartCommand implements CommandExecutor, Listener {
    List<Integer> tyoufukuCheck = new ArrayList<>();
    Map<Location, Integer> materials = new HashMap<>();
    Integer checkNum = 0;
    Map<Integer, Block> seigoHantei = new HashMap<>();
    private final Main main;
    int score;
    SqlSessionFactory sqlSessionFactory;

    public GameStartCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        for (int i = 1; i <= 8; i++) {
            tyoufukuCheck.add(i);
            tyoufukuCheck.add(i);
        }
        Collections.shuffle(tyoufukuCheck);
        if (commandSender instanceof Player player) {

            Location loc = player.getLocation();
            World world = player.getWorld();
            for (int i = 1; i < 9; i += 2) {
                for (int j = 1; j < 9; j += 2) {
                    Block setBlock = world.getBlockAt((int) (loc.getX() + i), (int) (loc.getY()), (int) loc.getZ() + j);
                    setBlock.setType(Material.BAMBOO_BLOCK);
                    int random = tyoufukuCheck.remove(0);
                    materials.put(setBlock.getLocation(), random);
                }
            }
            Bukkit.getScheduler().runTaskLater(
                    main,
                    () ->
                    {
                        for (Map.Entry<Location, Integer> entry : materials.entrySet()) {
                            world.getBlockAt(entry.getKey()).setType(Material.AIR);
                        }
                        player.sendTitle("ゲームが終了しました", "合計" + score + "点",
                                0, 60, 0);
                        try {
                            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
                            this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try(SqlSession session = sqlSessionFactory.openSession(true)){
                            PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
                            mapper.insert(new PlayerScore(player.getName(), score));



                        }
                        score = 0;
                        seigoHantei.clear();
                        materials.clear();
                    },
                    20 * 10);

        }
        return false;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block touchBlock = e.getClickedBlock();
        if (touchBlock == null) return; //ブロック以外を触ったときの処理をはじく

        Location blockLocation = touchBlock.getLocation();
        if (touchBlock.getType() != Material.BAMBOO_BLOCK) return;//別のブロックを触ったときの処理

        Integer numCheck = materials.get(blockLocation);
        if (numCheck == null) return;

        player.sendMessage(String.valueOf(numCheck));
        if (seigoHantei.get(numCheck) == null) {
            checkNum += 1;
        } else if (!seigoHantei.get(numCheck).getLocation().equals(touchBlock.getLocation())) {
            checkNum += 1;
        }

        if (checkNum == 2 && seigoHantei.containsKey(numCheck)) {
            seigoHantei.get(numCheck).setType(Material.AIR);
            touchBlock.setType(Material.AIR);
            score += 10;
            checkNum = 0;
            player.sendMessage("アタリ。現在の得点は" + score + "点です");
            seigoHantei.clear();
        } else if (checkNum == 2) {
            checkNum = 0;
            player.sendMessage("はずれー");
            seigoHantei.clear();

        } else {
            seigoHantei.put(numCheck, touchBlock);
        }

    }

}
