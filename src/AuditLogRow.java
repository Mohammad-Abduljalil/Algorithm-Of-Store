import java.awt.Color;

/**
 * نموذج بيانات بسيط لعرض صف في سجل العمليات (Mock Data للمعاينة البصرية حاليًا).
 * الحقول مطابقة لصيغة AuditLog.java الفعلية (timestamp | username | action | result).
 */
public class AuditLogRow {
    public final String timestamp;
    public final String username;
    public final String actionDetails;
    public final String extraDetail; // مثل [DENIED - insufficient privileges] أو [attempt 2/3]
    public final String result; // SUCCESS, DENIED, FAILED
    public final Color avatarBg;
    public final Color avatarFg;
    public final boolean highlightRow; // صفوف DENIED تُصبَغ بخلفية حمراء خافتة جدًا

    public AuditLogRow(String timestamp, String username, String actionDetails, String extraDetail,
                        String result, Color avatarBg, Color avatarFg, boolean highlightRow){
        this.timestamp = timestamp;
        this.username = username;
        this.actionDetails = actionDetails;
        this.extraDetail = extraDetail;
        this.result = result;
        this.avatarBg = avatarBg;
        this.avatarFg = avatarFg;
        this.highlightRow = highlightRow;
    }
}
