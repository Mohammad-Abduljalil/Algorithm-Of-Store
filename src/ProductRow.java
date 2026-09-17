/**
 * نموذج بيانات بسيط لعرض صف منتج في جدول الشاشة (Mock Data للمعاينة البصرية حاليًا).
 * لاحقًا عند الربط بمنطق الأعمال الفعلي، سيُستبدَل هذا بقراءة مباشرة من كائنات Product الحقيقية.
 */
public class ProductRow {
    public final String id;
    public final String name;
    public final String category;
    public final String price;
    public final String quantity;
    public final boolean lowStock;

    public ProductRow(String id, String name, String category, String price, String quantity, boolean lowStock){
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStock = lowStock;
    }
}
