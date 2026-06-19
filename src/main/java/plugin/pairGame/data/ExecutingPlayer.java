package plugin.pairGame.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExecutingPlayer {
    private int id;
    private String playerName;
    private int score;
    private LocalDateTime registeredTime;

    public ExecutingPlayer(String playerName, int score){
        this.playerName = playerName;
        this.score = score;
    }
}
