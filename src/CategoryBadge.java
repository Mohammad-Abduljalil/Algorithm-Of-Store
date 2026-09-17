import java.awt.Color;

/**
 * شارة تصنيف (Category Badge) بلون يُحدَّد تلقائيًا حسب اسم التصنيف نفسه،
 * بحيث يحصل كل تصنيف على لون مميز وثابت دون الحاجة لتهيئة يدوية مسبقة
 * (لأن التصنيفات في المشروع نصوص حرة يُدخلها المستخدم - راجع ProductManagement.categoryMap).
 */
public class CategoryBadge extends Badge {

    // لوحة ألوان هادئة (Pastel) تطابق أسلوب Tailwind، مع تجنّب عمدًا الأخضر/الكهرماني/الأحمر
    // لأنها محجوزة دلاليًا لشارات الحالة (In Stock / Low Stock / DENIED) في Badge.java،
    // وأي تداخل بينها وبين لون تصنيف قد يُربك القارئ (كما اكتُشف فعليًا أثناء المراجعة البصرية).
    private static final Color[][] PALETTE = {
            { new Color(0xDBEAFE), new Color(0x1E40AF) }, // Blue
            { new Color(0xF3E8FF), new Color(0x6B21A8) }, // Purple
            { new Color(0xF3F4F6), new Color(0x1F2937) }, // Gray
            { new Color(0xFCE7F3), new Color(0x9D174D) }, // Pink
            { new Color(0xE0E7FF), new Color(0x3730A3) }, // Indigo
            { new Color(0xCFFAFE), new Color(0x155E75) }, // Cyan
            { new Color(0xEDE9FE), new Color(0x5B21B6) }, // Violet
    };

    public CategoryBadge(String categoryName){
        super(categoryName, Semantic.NEUTRAL); // اللون الفعلي يُستبدَل أدناه في paintComponent عبر الحقول الموروثة
        Color[] pair = colorFor(categoryName);
        setBackground(pair[0]);
        setForeground(pair[1]);
    }

    private static Color[] colorFor(String name){
        int hash = Math.abs((name == null ? "Uncategorized" : name).hashCode());
        return PALETTE[hash % PALETTE.length];
    }
}
