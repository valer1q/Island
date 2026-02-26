package functional;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class MainCancelFunctionalTest {

    @Test
    void main_withInputN_printsCancelMessage() throws Exception {
        InputStream oldIn = System.in;
        PrintStream oldOut = System.out;

        try {
            ByteArrayInputStream in = new ByteArrayInputStream("n\n".getBytes());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.setIn(in);
            System.setOut(new PrintStream(out));

            Main.main(new String[0]);

            String console = out.toString();
            assertTrue(console.contains("Симуляция отменена."));
        } finally {
            System.setIn(oldIn);
            System.setOut(oldOut);
        }
    }
}
