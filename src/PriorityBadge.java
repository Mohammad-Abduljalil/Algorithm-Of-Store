/**
 * شارة أولوية الطلب، بلون يعكس مستوى الخطورة: أحمر (7-10)، كهرماني (4-6)، رمادي (1-3).
 * يطابق ألوان Tailwind المستخدمة في تصميم Stitch (red-100/800, amber-100/800, gray-100/800).
 */
public class PriorityBadge extends Badge {

    public PriorityBadge(int priority){
        super("Priority " + priority, semanticFor(priority));
    }

    private static Semantic semanticFor(int priority){
        if (priority >= 7) return Semantic.ERROR;
        if (priority >= 4) return Semantic.WARNING;
        return Semantic.NEUTRAL;
    }
}
