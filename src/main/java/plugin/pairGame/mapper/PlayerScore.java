package plugin.pairGame.mapper;

/**
 * プレイヤーのスコア情報を扱うオブジェクト
 * DBに存在するテーブルと連動する
 */
public class PlayerScore {

    private int id;
    private String playerName;
    private int score;
    private String registeredTime;

    public PlayerScore(String playerName, int score){
        this.playerName = playerName;
        this.score = score;
    }

}
