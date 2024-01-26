package link.reallth.usermatchbackend.task;

import jakarta.annotation.Resource;
import link.reallth.usermatchbackend.constants.CacheConst;
import link.reallth.usermatchbackend.model.vo.UserVO;
import link.reallth.usermatchbackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * precache task
 *
 * @author ReAllTh
 */
@Slf4j
@Component
public class PreCacheTask {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserService userService;

    @Scheduled(cron = "0/15 * * * * ? ")
    public void doPreCache() {
        RLock lock = redissonClient.getLock(CacheConst.MAIN_PAGE + "lock");
        try {
            if (lock.tryLock(0, 10L, TimeUnit.SECONDS)) {
                RBucket<List<UserVO>> rBucket = redissonClient.getBucket(CacheConst.MAIN_PAGE + 1);
                if (!rBucket.isExists()) {
                    List<UserVO> userVOList = userService.mainPageUsers(1);
                    rBucket.set(userVOList, Duration.ofSeconds(10L + new Random().nextLong(10L)));
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
