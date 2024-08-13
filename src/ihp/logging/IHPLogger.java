package ihp.logging;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class IHPLogger {
    private static final PrintWriter writer;
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long lastFlush = System.currentTimeMillis();

    static {
        try {
            FileWriter fileWriter = new FileWriter("log.txt", true);
            writer = new PrintWriter(fileWriter);
            Runtime.getRuntime().addShutdownHook(new Thread(writer::close));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static synchronized void print(String message, String level) {
        String formattedMessage = "[" + simpleDateFormat.format(System.currentTimeMillis()) + "] [" + level + "]: " + message;
        writer.println(formattedMessage);
        System.out.println(formattedMessage);
        if (System.currentTimeMillis() - lastFlush > 1000) {
            writer.flush();
            lastFlush = System.currentTimeMillis();
        }
    }

    private static void printMultiline(String message, String level) {
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }
            print(line, level);
        }
    }

    public static void info(String message) {
        printMultiline(message, "I");
    }

    public static void info(String message, Object... args) {
        printMultiline(String.format(message, args), "I");
    }

    public static void warn(String message) {
        printMultiline(message, "W");
    }

    private static void exception(Throwable t, boolean first) {
        if (first) {
            print("------ Exception Dump Start ------", "E");
            print("Exception: " + t.getClass().getName(), "E");
        } else {
            print("Caused by: " + t.getClass().getName(), "E");
        }
        String message = t.getMessage();
        if (message != null) {
            print("Reason is: " + message, "E");
        }
        for (StackTraceElement element : t.getStackTrace()) {
            print("           " + element.toString(), "E");
        }
        Throwable cause = t.getCause();
        if (cause != null) {
            exception(cause, false);
        } else {
            print("------ Exception Dump End ------", "E");
        }
    }

    public static void exception(String message, Throwable t) {
        printMultiline("Errors occurred when " + message + "!", "E");
        exception(t, true);
    }
}
