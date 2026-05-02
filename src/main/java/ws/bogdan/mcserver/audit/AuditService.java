package ws.bogdan.mcserver.audit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

public final class AuditService {
    private static final String CSV_FILE = "audit.csv";
    private static final String HEADER = "action_name,timestamp";

    private static AuditService instance;

    private final BufferedWriter writer;

    private AuditService() {
        try {
            Path path = Paths.get(CSV_FILE);
            boolean isNew = !Files.exists(path);
            this.writer = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (isNew) {
                writer.write(HEADER);
                writer.newLine();
                writer.flush();
            }
            Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to open audit log: " + CSV_FILE, e);
        }
    }

    public static AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public synchronized void logAction(String actionName) {
        try {
            writer.write(actionName + "," + LocalDateTime.now());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write audit entry: " + actionName, e);
        }
    }

    public synchronized void close() {
        try {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing audit log: " + e.getMessage());
        }
    }
}
