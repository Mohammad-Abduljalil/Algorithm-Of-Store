import java.awt.Color;

/**
 * نموذج بيانات بسيط لعرض صف مستخدم في جدول إدارة المستخدمين (Mock Data للمعاينة البصرية حاليًا).
 */
public class UserRow {
    public final String initials;
    public final String username;
    public final Role role;
    public final boolean active;
    public final Color avatarBg;
    public final Color avatarFg;

    public UserRow(String initials, String username, Role role, boolean active, Color avatarBg, Color avatarFg){
        this.initials = initials;
        this.username = username;
        this.role = role;
        this.active = active;
        this.avatarBg = avatarBg;
        this.avatarFg = avatarFg;
    }
}
