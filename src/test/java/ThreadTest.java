import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.worker.ThreadWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

public class ThreadTest {

    private ThreadWorker threadWorker;
    private UserDaoJdbc userDaoJdbc;
    private UserDaoMongo userDaoMongo;
    private static final String FILE_NAME = "input.txt";
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
        threadWorker = new ThreadWorker(userDaoJdbc, userDaoMongo); // мы делаем mock тех сервисов, которых мы не будем вызывать
    }


    @Test
    public void version_1_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version1 = "join";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version1, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_2_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version2 = "join array";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version2, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_3_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version3 = "count down";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version3, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_4_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version4 = "executor";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version4, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_5_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version5 = "executor2";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version5, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_6_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version6 = "executor3";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version6, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_7_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version7 = "barrier";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version7, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }
}
