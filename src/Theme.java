import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * نظام التصميم المركزي للواجهة، مبني مباشرة على قيم DESIGN.md ("Kinetic Enterprise").
 * كل الألوان والخطوط والانحناءات في المشروع يجب أن تُؤخَذ من هنا فقط،
 * حتى تبقى الواجهة متسقة بصريًا بين كل الشاشات (بدل تكرار قيم Hex في كل ملف).
 */
public class Theme {

    // ===================== الألوان (من قسم colors في DESIGN.md) =====================
    public static final Color SURFACE = hex("#f7f9fb");
    public static final Color SURFACE_CONTAINER_LOWEST = hex("#ffffff"); // بطاقات المحتوى (Level 1)
    public static final Color SURFACE_CONTAINER_LOW = hex("#f2f4f6");
    public static final Color SURFACE_CONTAINER = hex("#eceef0");
    public static final Color SURFACE_CONTAINER_HIGH = hex("#e6e8ea"); // خلفية رأس الجدول
    public static final Color ON_SURFACE = hex("#191c1e");             // نص أساسي
    public static final Color ON_SURFACE_VARIANT = hex("#45474c");     // نص ثانوي
    public static final Color OUTLINE = hex("#75777d");
    public static final Color OUTLINE_VARIANT = hex("#c5c6cd");

    // الشريط الجانبي (Primary = Deep Navy)
    public static final Color PRIMARY = hex("#091426");
    public static final Color PRIMARY_CONTAINER = hex("#1e293b");      // خلفية الشريط الجانبي الفعلية في اللقطات
    public static final Color ON_PRIMARY = hex("#ffffff");
    public static final Color ON_PRIMARY_CONTAINER = hex("#8590a6");   // نص العناصر غير النشطة بالشريط الجانبي

    // زر الإجراء الأساسي (Confident Blue - من قسم Components في DESIGN.md)
    public static final Color ACCENT_BLUE = hex("#2563EB");
    public static final Color ACCENT_BLUE_HOVER = hex("#1D4ED8");

    // ألوان إضافية دقيقة مستخرجة من كود Stitch الفعلي (تختلف قليلاً عن زر الإجراء الأساسي)
    public static final Color SECONDARY_CONTAINER = hex("#316BF3"); // خلفية عنصر التنقل النشط في الشريط الجانبي
    public static final Color ON_SECONDARY_CONTAINER = hex("#FEFCFF");
    public static final Color SECONDARY_LINK = hex("#0051D5"); // روابط نصية مثل "View All"
    public static final Color PRIMARY_FIXED = hex("#D8E3FB"); // خلفية أيقونة بطاقة KPI الأولى
    public static final Color SECONDARY_FIXED = hex("#DBE1FF"); // خلفية أيقونة بطاقة KPI الثانية
    public static final Color TERTIARY_FIXED = hex("#D3E4FE");  // خلفية أيقونة بطاقة KPI الرابعة
    public static final Color TERTIARY = hex("#041528");

    // الخلفية العامة ولون حدود البطاقات (من قسم Elevation)
    public static final Color BACKGROUND = hex("#F8FAFC");
    public static final Color CARD_BORDER = hex("#E2E8F0");
    public static final Color INPUT_BORDER = hex("#D1D5DB");
    public static final Color DIVIDER = hex("#F1F5F9");

    // الألوان الدلالية (Semantic) - من قسم Components
    public static final Color SUCCESS_BG = hex("#DCFCE7");
    public static final Color SUCCESS_TEXT = hex("#16A34A");
    public static final Color WARNING_BG = hex("#FEF3C7");
    public static final Color WARNING_TEXT = hex("#D97706");
    public static final Color ERROR_BG = hex("#FFDAD6");
    public static final Color ERROR_TEXT = hex("#93000A");
    public static final Color ERROR = hex("#BA1A1A");

    // ===================== الانحناءات (rounded) =====================
    public static final int RADIUS_SM = 4;   // 0.25rem
    public static final int RADIUS_DEFAULT = 8;  // 0.5rem - الأزرار وحقول الإدخال
    public static final int RADIUS_MD = 12;  // 0.75rem
    public static final int RADIUS_LG = 16;  // 1rem - النوافذ المنبثقة الكبيرة
    public static final int RADIUS_FULL = 999; // للشارات (Pills)

    // ===================== المسافات (8pt spacing system) =====================
    public static final int SPACE_XS = 4;
    public static final int SPACE_SM = 8;
    public static final int SPACE_MD = 16;
    public static final int SPACE_LG = 24;
    public static final int SPACE_XL = 32;

    // ===================== الخطوط =====================
    private static final String PREFERRED_FONT_FAMILY = resolveFontFamily();

    private static String resolveFontFamily(){
        // نحاول Inter أولاً (المطلوب في DESIGN.md)، ثم نتراجع لخطوط شائعة مضمونة التوفر
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        String[] candidates = {"Inter", "Segoe UI", "Helvetica Neue", "Arial", "SansSerif"};
        for (String candidate : candidates){
            if (available.contains(candidate)){
                return candidate;
            }
        }
        return Font.SANS_SERIF; // ضمانة أخيرة، موجودة دائمًا في جافا
    }

    // مطابقة لمقياس typography في DESIGN.md (الاسم -> الحجم/الوزن)
    public static Font display()      { return font(Font.BOLD, 36); }
    public static Font headlineLg()   { return font(Font.BOLD, 28); }
    public static Font headlineMd()   { return font(Font.BOLD, 20); }
    public static Font bodyLg()       { return font(Font.PLAIN, 16); }
    public static Font bodyMd()       { return font(Font.PLAIN, 14); }
    public static Font bodySm()       { return font(Font.PLAIN, 13); }
    public static Font labelMd()      { return font(Font.BOLD, 12); } // تُستخدم عادة بأحرف كبيرة (UPPERCASE) لعناوين الجداول
    public static Font monoLabel()    { return new Font(Font.MONOSPACED, Font.PLAIN, 13); }

    private static Font font(int style, int size){
        return new Font(PREFERRED_FONT_FAMILY, style, size);
    }

    // ===================== أدوات مساعدة =====================
    public static Color hex(String hexColor){
        return Color.decode(hexColor.startsWith("#") ? hexColor : "#" + hexColor);
    }

    private Theme(){} // كلاس ثوابت فقط، لا يُنشأ منه كائن
}
