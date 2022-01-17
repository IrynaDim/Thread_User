import com.dev.thread.user.dao.UserDaoJdbc;
import com.dev.thread.user.dao.UserDaoMongo;
import com.dev.thread.user.model.User;
import com.dev.thread.user.worker.MainWorker;
import com.dev.thread.user.worker.ThreadWorker;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class ThreadTestNew {

    private MainWorker threadWorker;
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
        threadWorker = new MainWorker(userDaoJdbc, userDaoMongo);
    }


    @Test
    public void version_1_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version1 = "join";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version1)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_2_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version3 = "count down";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version3)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_3_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version4 = "executor";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version4)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_4_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version5 = "executor2";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version5)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_5_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version6 = "completionService";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version6)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_6_Ok() {
        doNothing().when(userDaoJdbc).saveAll(new ArrayList<>());
        doNothing().when(userDaoMongo).saveAll(new ArrayList<>());
        String version7 = "barrier";
        Assert.assertArrayEquals(result.toArray(), threadWorker.testThread(version7)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }
}
