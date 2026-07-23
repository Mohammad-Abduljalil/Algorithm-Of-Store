import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditLog {

    private static final String LOG_FILE = "store_data" + File.separator + "audit_log.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void record(String username, String action, String details){
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = timestamp + " | " + username + " | " + action + (details.isBlank() ? "" : " | " + details);
        try {
            new File("store_data").mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println(line);
            }
        } catch (IOException e){
            System.out.println(" Error while writing audit log: " + e.getMessage());
        }
    }

    public static void printLog(){
        File file = new File(LOG_FILE);
        if (!file.exists()){
            System.out.println(" No audit log entries yet. ");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            System.out.println("Audit Log : ");
            String line;
            int count = 1;
            while ((line = reader.readLine()) != null){
                if (line.isBlank()) continue;
                System.out.println(count++ + "- " + line);
            }
        } catch (IOException e){
            System.out.println(" Error while reading audit log: " + e.getMessage());
        }
    }
}
