package link.reallth.usermatchbackend.utils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import link.reallth.usermatchbackend.model.po.User;
import link.reallth.usermatchbackend.service.UserService;
import lombok.Data;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * data utils
 * for import and export user data from or to excel
 *
 * @author ReAllTh
 */
public class DataUtils {

    private DataUtils() {
    }

    /**
     * user data object with fields name corresponding to excel headers
     */
    @Data
    private static class UserData implements Serializable {

        @ExcelProperty("id")
        private String id;

        @ExcelProperty("username")
        private String username;

        @ExcelProperty("email")
        private String email;

        @ExcelProperty("passwd")
        private String passwd;

        @Serial
        private static final long serialVersionUID = 1L;

    }

    /**
     * recursive action class for ForkJoinPool
     */
    private static class ImportTask extends RecursiveAction {
        private final List<User> subUsers;
        private final UserService userService;

        ImportTask(List<User> subUsers, UserService userService) {
            this.subUsers = subUsers;
            this.userService = userService;
        }

        @Override
        protected void compute() {
            if (subUsers.size() < 85000) userService.saveBatch(subUsers);
            else {
                List<List<User>> partitioned = ListUtils.partition(subUsers, subUsers.size() / 2);
                ForkJoinTask<Void> subTask1 = new ImportTask(partitioned.get(0), userService).fork();
                ForkJoinTask<Void> subTask2 = new ImportTask(partitioned.get(1), userService).fork();
                subTask1.join();
                subTask2.join();
            }
        }
    }

    private static final String file = "yourPath/fileName";

    /**
     * generate 1000000 fake user data to excel
     */
    public static void writeExcel() {
        IdentifierGenerator generator = new DefaultIdentifierGenerator(0, 0);
        List<UserData> usersData = new ArrayList<>(1000000);

        for (int i = 0; i < 1000000; i++) {
            UserData userData = new UserData();
            userData.setId(generator.nextUUID(new Object()));
            userData.setUsername("FakeUsername" + i);
            userData.setEmail("FakeEmail" + i + "@fake.com");
            userData.setPasswd("FakePasswd" + i + ".");
            usersData.add(userData);
        }

        EasyExcelFactory.write(file, UserData.class).sheet().doWrite(usersData);
    }

    /**
     * import data without multi thread
     *
     * @param userService user service
     */
    public static void readExcel(UserService userService) {
        List<User> users = new ArrayList<>(1000000);

        EasyExcelFactory.read(file, UserData.class, new ReadListener<UserData>() {
            @Override
            public void invoke(UserData userData, AnalysisContext analysisContext) {
                User user = new User();
                BeanUtils.copyProperties(userData, user);
                users.add(user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                userService.saveBatch(users);
                stopWatch.stop();
                System.out.println("All done. Total time: " + stopWatch.getTime());
            }
        }).sheet().doRead();
    }

    /**
     * import data with multi thread using customized ThreadPool and CompletableFuture
     *
     * @param userService user service
     */
    public static void multiFutureReadExcel(UserService userService) {
        List<User> userList = new ArrayList<>(1000000);

        EasyExcelFactory.read(file, UserData.class, new ReadListener<UserData>() {
            @Override
            public void invoke(UserData userData, AnalysisContext analysisContext) {
                User user = new User();
                BeanUtils.copyProperties(userData, user);
                userList.add(user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                List<List<User>> users = ListUtils.partition(userList, 85000);
                ExecutorService executorService = new ThreadPoolExecutor(12, 18, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
                List<CompletableFuture<Void>> futureList = new ArrayList<>();
                StopWatch stopWatch = new StopWatch();
                for (List<User> userList : users)
                    futureList.add(CompletableFuture.runAsync(() -> userService.saveBatch(userList), executorService));
                stopWatch.start();
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
                stopWatch.stop();
                System.out.println("All done. Total time: " + stopWatch.getTime());
            }
        }).sheet().doRead();
    }

    /**
     * import data with multi thread using ForkJoinPool and RecursiveAction
     *
     * @param userService user service
     */
    public static void multiForkJoinReadExcel(UserService userService) {
        List<User> userList = new ArrayList<>(1000000);

        EasyExcelFactory.read(file, UserData.class, new ReadListener<UserData>() {
            @Override
            public void invoke(UserData userData, AnalysisContext analysisContext) {
                User user = new User();
                BeanUtils.copyProperties(userData, user);
                userList.add(user);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                ForkJoinPool forkJoinPool = new ForkJoinPool();
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                forkJoinPool.invoke(new ImportTask(userList, userService));
                stopWatch.stop();
                forkJoinPool.shutdown();
                System.out.println("All done." + stopWatch.getTime(TimeUnit.MILLISECONDS));
            }
        }).sheet().doRead();
    }
}
