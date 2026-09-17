import java.awt.Color;

/**
 * نماذج بيانات بسيطة لودجات شاشة Reports (Mock Data للمعاينة البصرية حاليًا).
 */
public class ReportModels {

    public static class TopShipmentEntry {
        public final int rank;
        public final String destination;
        public final String shipmentId;
        public final String category;
        public final String cost;
        public final String status;
        public final Color medalBg;
        public final Color medalBorder;
        public final Color medalText;
        public final Badge.Semantic statusSemantic;

        public TopShipmentEntry(int rank, String destination, String shipmentId, String category, String cost,
                                 String status, Color medalBg, Color medalBorder, Color medalText, Badge.Semantic statusSemantic){
            this.rank = rank;
            this.destination = destination;
            this.shipmentId = shipmentId;
            this.category = category;
            this.cost = cost;
            this.status = status;
            this.medalBg = medalBg;
            this.medalBorder = medalBorder;
            this.medalText = medalText;
            this.statusSemantic = statusSemantic;
        }
    }

    public static class CategoryEntry {
        public final String name;
        public final int count;
        public final int percent;
        public final Color color;

        public CategoryEntry(String name, int count, int percent, Color color){
            this.name = name;
            this.count = count;
            this.percent = percent;
            this.color = color;
        }
    }

    private ReportModels(){}
}
