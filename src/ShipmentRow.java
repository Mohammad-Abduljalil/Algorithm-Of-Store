import java.util.List;

/**
 * نموذج بيانات بسيط لعرض صف شحنة في الجدول (Mock Data للمعاينة البصرية حاليًا).
 */
public class ShipmentRow {
    public final String id;
    public final String destination;
    public final String deliveryDate;
    public final String budget;
    public final String currentCost;
    public final int priority;
    public final List<ShipmentProductLine> products;

    public ShipmentRow(String id, String destination, String deliveryDate, String budget,
                        String currentCost, int priority, List<ShipmentProductLine> products){
        this.id = id;
        this.destination = destination;
        this.deliveryDate = deliveryDate;
        this.budget = budget;
        this.currentCost = currentCost;
        this.priority = priority;
        this.products = products;
    }

    public static class ShipmentProductLine {
        public final String name;
        public final String quantity;
        public final String unitPrice;
        public final String subtotal;

        public ShipmentProductLine(String name, String quantity, String unitPrice, String subtotal){
            this.name = name;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.subtotal = subtotal;
        }
    }
}
