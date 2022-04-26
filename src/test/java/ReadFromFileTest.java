import com.dev.thread.user.util.FileReader;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Queue;

public class ReadFromFileTest {
    private static final String FILE_NAME_YES = "inputTest.txt";
    private static final String FILE_NAME_NO = "incorrect file name";
    private static final Queue<String> dataFromFile = new ArrayDeque<>() {{
        add("1,lev,100");
        add("2,shani,200");
        add("3,lior,-10");
        add("4,sigal,80");
        add("5,dima,-70");
        add("3,lev,-50");
        add("6,shaul,50");
        add("7,itay,90");
        add("1,lev,-30");
        add("8,lev,30");
        add("1,shaul,-80");
    }};

    @Test
    public void text_Reader_Test_OK() {
        Assert.assertArrayEquals(dataFromFile.toArray(), FileReader.readFromFile(FILE_NAME_YES).toArray());
    }

    @Test(expected = RuntimeException.class)
    public void text_Reader_Test_Fail() {
        FileReader.readFromFile(FILE_NAME_NO);
    }
}
