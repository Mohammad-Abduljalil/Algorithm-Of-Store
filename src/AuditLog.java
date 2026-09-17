import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * سجل عمليات بسيط (Audit Log): يُسجّل من نفّذ أي عملية مُغيِّرة للحالة ومتى.
 * التسجيل فوري (append لكل سطر لحظة وقوع الحدث)، وليس مُجمّعًا عند الحفظ/الخروج،
 * حتى يبقى السجل صحيحًا حتى لو أُغلق البرنامج فجأة أو انهار.
 */
public class AuditLog {

    private static final String LOG_FILE = "store_data" + File.separator + "audit_log.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void record(String username, String action, String details){
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = timestamp + " | " + username + " | " + action + (details.isBlank() ? "" : " | " + details);
        try {
            new File("store_data").mkdirs();
            // وضع append=true مهم جدًا هنا: نضيف سطرًا جديدًا في كل مرة، لا نستبدل الملف بالكامل
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

    // تُعيد آخر n سطر من السجل بترتيب الأحدث أولاً (تُستخدم من الواجهة الرسومية بدل الطباعة المباشرة)
    public static java.util.List<String> getRecentEntries(int n){
        java.util.List<String> lines = new java.util.ArrayList<>();
        File file = new File(LOG_FILE);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null){
                if (!line.isBlank()) lines.add(line);
            }
        } catch (IOException e){
            System.out.println(" Error while reading audit log: " + e.getMessage());
        }
        java.util.Collections.reverse(lines); // الأحدث أولاً
        if (lines.size() > n){
            return lines.subList(0, n);
        }
        return lines;
    }
}
