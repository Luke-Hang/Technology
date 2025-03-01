package com.myspringboot.redis.range;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Set;

/**
 * @author xiehang
 * @date 2024/8/5 12:11
 */
@Component
public class LeaderBoardService {

    private final RedisTemplate<String, String> redisTemplate;

    public LeaderBoardService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 更新用户的分数,新增或者更新数据
     *
     * @param userId 用户ID
     * @param score  分数
     * @param type   排行榜类型（周、月、年）
     */
    public void updateScore(String userId, double score, String type) {
        String key = buildKey(type);
        //按排行榜类型key，新增或者更新数据
        //redisTemplate.opsForZSet().add(Week leaderboard:2024:32, user1, 100),
        redisTemplate.opsForZSet().add(key, userId, score);
    }

    /**
     * 根据指定类型获取某个用户的排名，指定元素排名
     *
     * @param userId 用户ID
     * @param type   排行榜类型（周、月、年）
     * @return 用户排名
     */
    public Long getRank(String userId, String type) {
        String key = buildKey(type);
        //根据排行榜类型key，获取用户的排名userId
        //获取某个用户userId的排名类型排名，如果用户user2在周榜排名为1，则返回0
        return redisTemplate.opsForZSet().reverseRank(key, userId);
    }

    /**
     * 获取指定类型的排行榜结果
     *
     * @param type  排行榜类型（周、月、年）
     * @param start 起始位置
     * @param end   结束位置
     * @return 排行榜列表
     */
    public Set<String> getLeaderBoard(String type, long start, long end) {
        String key = buildKey(type);
        //只有三条数据，这里根据排行榜类型key，获取从0到2的排行榜列表
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 构建排行榜的Key
     *
     * @param type 排行榜类型（周、月、年）
     * @return Key
     */
    private String buildKey(String type) {
        LocalDate now = LocalDate.now();
        switch (type) {
            case "Week":
                // 周榜
                return "Week leaderboard:" + now.getYear() + ":" + getWeekOfYear(now);
            case "Month":
                // 月榜
                return "Month leaderboard" + now.getYear() + ":" + now.getMonthValue();
            case "Year":
                // 年榜
                return "leaderboard:y:" + now.getYear();
            default:
                throw new IllegalArgumentException("Invalid leaderboard type");
        }
    }

    /**
     * 获取当前年份的第几周
     *
     * @param date 当前日期
     * @return 第几周
     */
    private int getWeekOfYear(LocalDate date) {
        return date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
    }
}

