package plugin.pairGame.Command;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
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

import java.util.*;

public class GameStartCommand implements CommandExecutor, Listener {
    public List<Integer> warifuriList = new ArrayList<>();
    public Map<Location, Integer> materials = new HashMap<>();
    public Map<Integer, Block> seigoHantei = new HashMap<>();
    public Integer checkNum = 0;
    public int score;
    private final Main main;
    public static final String List1 = "list";
    public SqlSessionFactory sqlSessionFactory;

    public GameStartCommand(Main main, SqlSessionFactory sqlSessionFactory) {
        this.main = main;
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] args) {
        if (commandSender instanceof Player player) {

            //listを引数につけたときにDBに接続し過去データを引っ張ってくる
            if (args.length == 1
                    && (List1.equals(args[0]))) {
                try (SqlSession session = sqlSessionFactory.openSession()) {
                    PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
                    List<PlayerScore> playerScoreList = mapper.selectList();
                    for (PlayerScore playerScore : playerScoreList){
                        player.sendMessage(playerScore.getId() + " | "
                                + playerScore.getPlayerName() + " | "
                                + playerScore.getScore() + " | "
                                + playerScore.getRegisteredTime());
                    }
                }
                return false;
            }

            //ブロックに数字を割り振る用のリスト作成
            for (int i = 1; i <= 8; i++) {
                warifuriList.add(i);
                warifuriList.add(i);
            }

            //ランダムな数字を割り振るためのシャッフル
            Collections.shuffle(warifuriList);
            Location loc = player.getLocation();
            World world = player.getWorld();

            MakeBlock(world, loc);

            //時間経過後の処理
            Bukkit.getScheduler().runTaskLater(
                    main,
                    () ->
                    {
                        //生成したブロックを消す処理
                        for (Map.Entry<Location, Integer> entry : materials.entrySet()) {
                            world.getBlockAt(entry.getKey()).setType(Material.AIR);
                        }

                        //ゲーム終了メッセージ、点数の提示
                        player.sendTitle("ゲームが終了しました", "合計" + score + "点",
                                0, 60, 0);

                        //データベースへ結果を登録
                        try (SqlSession session = sqlSessionFactory.openSession(true)) {
                            PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
                            mapper.insert(new PlayerScore(player.getName(), score));
                        }

                        //次のゲームのためのリセット
                        score = 0;
                        seigoHantei.clear();
                        materials.clear();
                    },
                    20 * 20);

        }
        return false;
    }

    /**
     * 所定の位置に竹ブロックを4×4個生成する
     *
     * @param world プレイヤーのワールド情報
     * @param loc   プレイヤーのロケーション情報
     */
    private void MakeBlock(World world, Location loc) {
        for (int i = 1; i < 9; i += 2) {
            for (int j = 1; j < 9; j += 2) {
                Block setBlock = world.getBlockAt((int) (loc.getX() + i), (int) (loc.getY()), (int) loc.getZ() + j);
                setBlock.setType(Material.BAMBOO_BLOCK);
                int random = warifuriList.remove(0);
                materials.put(setBlock.getLocation(), random);
            }
        }
    }

    /**
     * 竹ブロックを触ったときの対応
     *　数字を触ったときの表示と一回目と二回目で一致したときにブロックが消える処理
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block touchBlock = e.getClickedBlock();

        //ブロック以外を触ったときの処理をはじく
        if (touchBlock == null) return;

        Location blockLocation = touchBlock.getLocation();
        //別のブロックを触ったときの処理
        if (touchBlock.getType() != Material.BAMBOO_BLOCK) return;

        Integer locCheck = materials.get(blockLocation);
        if (locCheck == null) return;

        player.sendMessage(String.valueOf(locCheck));
        //一回目か二回目かをわかるようにする
        if (seigoHantei.get(locCheck) == null) {
            checkNum += 1; //seigoHanteiに何も入っていない＝一回もチェックしていない
        } else if (!seigoHantei.get(locCheck).getLocation().equals(touchBlock.getLocation())) {
            checkNum += 1; //同じブロック以外を触ったときにカウントする
        }

        //2回目かつ一回目と一致しているときの動作
        if (checkNum == 2 && seigoHantei.containsKey(locCheck)) {
            seigoHantei.get(locCheck).setType(Material.AIR);
            touchBlock.setType(Material.AIR);
            score += 10;
            checkNum = 0;
            player.sendMessage("アタリ。現在の得点は" + score + "点です");
            seigoHantei.clear();
        }
        //2回目だが、一致していなかった場合
        else if (checkNum == 2) {
            checkNum = 0;
            player.sendMessage("はずれー");
            seigoHantei.clear();

        }
        //一回目である場合
        else {
            seigoHantei.put(locCheck, touchBlock);
        }

    }

}
