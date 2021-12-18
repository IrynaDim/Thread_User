import com.dev.thread.user.model.User;
import com.dev.thread.user.worker.ThreadWorker;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

public class ThreadTest {

    private final ThreadWorker threadWorker = new ThreadWorker();
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

    @Test
    public void version_1_Ok() {
        String version1 = "join";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version1, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_2_Ok() {
        String version2 = "join array";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version2, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_3_Ok() {
        String version3 = "count down";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version3, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_4_Ok() {
        String version4 = "executor";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version4, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_5_Ok() {
        String version5 = "executor2";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version5, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_6_Ok() {
        String version6 = "executor3";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version6, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }

    @Test
    public void version_7_Ok() {
        String version7 = "barrier";
        Assert.assertArrayEquals(result.toArray(), threadWorker.chooseVersion(version7, FILE_NAME)
                .stream().sorted(Comparator.comparing(User::getName)).toArray());
    }
}
