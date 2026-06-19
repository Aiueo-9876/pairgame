package plugin.pairGame.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PlayerScoreMapper {

    @Select("select * from player_score")
    List<PlayerScore> selectList();

    @Insert("Insert player_score(playerName, score, registeredTime) values (#{playerName}, #{score},now())")
    int insert(PlayerScore playerScore);
}
