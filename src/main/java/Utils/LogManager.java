// LogManager.java
package Utils;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LogManager {
    private static final String LOG_DIR = "target/logs";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
    private static final ConcurrentHashMap<String, PrintStream> logStreams = new ConcurrentHashMap<>();
    private static final ReentrantLock lock = new ReentrantLock();

    static {
        createLogDirectory();
    }

    private static void createLogDirectory() {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    public static PrintStream getLogStream(String testClassName) {
        return logStreams.computeIfAbsent(testClassName, className -> {
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String logFileName = String.format("%s/%s_%s.log", LOG_DIR, className, timestamp);

            try {
                File logFile = new File(logFileName);
                FileOutputStream fos = new FileOutputStream(logFile);
                PrintStream logStream = new PrintStream(fos, true);

                // Write header to log file
                logStream.println("=".repeat(80));
                logStream.println("TEST LOG FOR: " + className);
                logStream.println("STARTED AT: " + LocalDateTime.now());
                logStream.println("=".repeat(80));
                logStream.println();

                return logStream;
            } catch (IOException e) {
                System.err.println("Failed to create log file: " + logFileName);
                e.printStackTrace();
                return System.out; // Fallback to console
            }
        });
    }

    public static void log(String testClassName, String message) {
        lock.lock();
        try {
            PrintStream logStream = getLogStream(testClassName);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logStream.println("[" + timestamp + "] " + message);
            logStream.flush();

            // Also log to console
            System.out.println("[" + timestamp + "] " + testClassName + " - " + message);
        } finally {
            lock.unlock();
        }
    }

    public static void logTestStart(String testClassName, String testMethodName) {
        log(testClassName, ">>> STARTING TEST: " + testMethodName);
    }

    public static void logTestEnd(String testClassName, String testMethodName, String status) {
        log(testClassName, "<<< FINISHED TEST: " + testMethodName + " - " + status);
        log(testClassName, "");
    }

    public static void logApiCall(String testClassName, String method, String endpoint, int statusCode) {
        log(testClassName, String.format("API CALL: %s %s -> Status: %d", method, endpoint, statusCode));
    }

    public static void closeAllStreams() {
        lock.lock();
        try {
            logStreams.values().forEach(stream -> {
                if (stream != System.out) {
                    stream.println();
                    stream.println("=".repeat(80));
                    stream.println("TEST LOG ENDED AT: " + LocalDateTime.now());
                    stream.println("=".repeat(80));
                    stream.close();
                }
            });
            logStreams.clear();
        } finally {
            lock.unlock();
        }
    }
}