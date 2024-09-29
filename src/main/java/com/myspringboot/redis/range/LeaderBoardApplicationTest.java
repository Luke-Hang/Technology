package com.myspringboot.redis.range;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author xiehang
 * @date 2024/8/5 12:18
 */
@Component
public class LeaderBoardApplicationTest {

/*    @Autowired
    private LeaderBoardService leaderBoardService;*/

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testUpdateAndRank() {

        LeaderBoardService leaderBoardService=new LeaderBoardService(null);

        leaderBoardService.updateScore("user1", 100, "Week");
        leaderBoardService.updateScore("user2", 200, "Week");
        leaderBoardService.updateScore("user3", 150, "Week");

        System.out.println("User1 Rank: " + leaderBoardService.getRank("user1", "Week"));
        System.out.println("User2 Rank: " + leaderBoardService.getRank("user2", "Week"));
        System.out.println("User3 Rank: " + leaderBoardService.getRank("user3", "Week"));

        System.out.println("Top 3 Weekly Leaderboard: " + leaderBoardService.getLeaderBoard("Week", 0, 2));
    }

    public Long getRang(String sortedSetKey, int member,long number) {
        //从小到大排序
        Set range = redisTemplate.opsForZSet().range(sortedSetKey, 0L, number);

        //从大到小排序
        Set set = redisTemplate.opsForZSet().reverseRange(sortedSetKey, 0L, number);

        //指定元素排名
        Long aLong = redisTemplate.opsForZSet().reverseRank(sortedSetKey, member);
        return null;
    }

}
