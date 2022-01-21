import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.thread.*;
import com.dev.thread.user.worker.MainWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class ThreadTest {

    private MainWorker mainWorker;
    private UserDaoJdbc userDaoJdbc;
    private UserDaoMongo userDaoMongo;
    private final List<User> result = new ArrayList<>() {{
        add(new User(null, "dima", -140.0));
        add(new User(null, "itay", 180.0));
        add(new User(null, "lev", 100.0));
        add(new User(null, "lior", 60.0));
        add(new User(null, "shani", 400.0));
        add(new User(null, "shaul", 100.0));
        add(new User(null, "sigal", 160.0));
    }};

    @Before
    public void init() {
        userDaoJdbc = mock(UserDaoJdbc.class);
        userDaoMongo = mock(UserDaoMongo.class);
        mainWorker = new MainWorker(userDaoJdbc, userDaoMongo,
                new ThreadExecutorAwait(userDaoJdbc, userDaoMongo),
                new ThreadFuture(userDaoJdbc, userDaoMongo),
                new ThreadJoin(userDaoJdbc, userDaoMongo),
                new ThreadCompletion(userDaoJdbc, userDaoMongo),
                new ThreadCountDown(userDaoJdbc, userDaoMongo),
                new ThreadCyclicBarrier(userDaoJdbc, userDaoMongo));
    }

    @Test
    public void version_join_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version1 = "join";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version1)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_countDown_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version3 = "count down";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version3)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_future_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version4 = "future";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version4)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_await_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version5 = "await";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version5)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_completionService_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version6 = "completionService";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version6)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_barrier_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version7 = "barrier";
        Assert.assertArrayEquals(result.toArray(), mainWorker.testThread(version7)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }
}
