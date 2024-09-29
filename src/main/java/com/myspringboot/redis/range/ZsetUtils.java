package com.myspringboot.redis.range;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author xiehang
 * @date 2024/6/27 23:44
 */
@Component
public class ZsetUtils {

    /**
     * Zset基础特性
     *  有序性：Zset中的每个元素都关联一个分数，Redis会根据这个分数自动对集合中的元素进行排序。
     *  唯一性：尽管成员可以有相同的分数，但每个成员在集合中仍然是唯一的。
     *  快速操作：添加、删除和查找操作都非常快，时间复杂度通常是O(log(N))或更好。
     *
     * 应用场景
     *  在线游戏中的玩家分数排行。
     *  社交媒体上的热门帖子按点赞数排序。
     *  电商网站的商品销量排行榜。
     */

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增或者更新数据
     * @param sortedSetKey key（用于区分不同的排行榜）
     * @param member       排行榜对象
     * @param score        排行依据（值）
     *                     如果在key中，不存在该member，则新增，如果存在，则更新
     */
    public void addOrUpdate(String sortedSetKey, int member, double score) {
        redisTemplate.opsForZSet().add(sortedSetKey, member, score);
    }

    /**
     * 获取排行榜指定范围的内容
     *
     * @param sortedSetKey key（用于区分不同的排行榜）
     * @param startIndex   起始值（从0开始）
     * @param endIndex     结束值
     * @return
     */
    public Set<Object> reverseRange(String sortedSetKey, int startIndex, int endIndex) {
        return redisTemplate.opsForZSet().reverseRange(sortedSetKey, startIndex, endIndex);
//        return redisTemplate.opsForZSet().reverseRange(sortedSetKey, 0L, number);
    }

    /**
     * 获取排名
     *
     * @param sortedSetKey key（用于区分不同的排行榜）
     * @param member       排行榜对象
     * @return
     */
    public Long getRankNum(String sortedSetKey, int member) {
        return redisTemplate.opsForZSet().rank(sortedSetKey, member);
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
