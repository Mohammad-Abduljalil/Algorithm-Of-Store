import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;

/**
 * رسم بياني منحني بسيط (Area + Line Chart)، يُعيد إنتاج نفس منحنى SVG الأصلي
 * (M0,180 C100,160 200,170 300,120...) لكن مُدرَّجًا (Scaled) ليلائم أي حجم للودجت.
 * الإحداثيات الأصلية بمقياس 800x200، ونُحوّلها نسبيًا لحجم اللوحة الفعلي وقت الرسم.
 */
public class LineChartWidget extends JPanel {

    // نقاط التحكم بمنحنى Bezier التكعيبي، بنفس مقياس SVG الأصلي (800 عرض × 200 ارتفاع)
    // نقطة بداية واحدة + 3 مقاطع Bezier، كل مقطع = (تحكم1، تحكم2، نهاية) = 3 نقاط لكل مقطع
    private static final double[][] CURVE_POINTS = {
            {0, 180},                              // نقطة البداية
            {100, 160}, {200, 170}, {300, 120},    // المقطع الأول
            {400, 70}, {500, 100}, {600, 50},      // المقطع الثاني
            {700, 0}, {800, 40}, {800, 40}         // المقطع الثالث
    };

    public LineChartWidget(){
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int leftPad = 48; // مساحة تسميات المحور Y
        int bottomPad = 24; // مساحة تسميات المحور X
        int chartW = w - leftPad;
        int chartH = h - bottomPad;

        // ===== خطوط الشبكة المتقطعة + تسميات المحور Y =====
        g2.setFont(Theme.labelMd());
        g2.setColor(Theme.ON_SURFACE_VARIANT);
        String[] yLabels = {"$15k", "$10k", "$5k", "$0"};
        float[] dashPattern = {4f, 4f};
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dashPattern, 0f));
        for (int i = 0; i < yLabels.length; i++){
            int y = (int) (chartH * i / (double) (yLabels.length - 1));
            g2.setColor(Theme.CARD_BORDER);
            if (i < yLabels.length - 1){
                g2.drawLine(leftPad, y, w, y);
            } else {
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(leftPad, y, w, y);
            }
            g2.setColor(Theme.ON_SURFACE_VARIANT);
            java.awt.FontMetrics fm = g2.getFontMetrics();
            g2.drawString(yLabels[i], 0, y + fm.getAscent() / 2 - 2);
        }

        // ===== تحويل نقاط المنحنى لمقياس اللوحة الفعلي =====
        double scaleX = chartW / 800.0;
        double scaleY = chartH / 200.0;

        Path2D.Double linePath = new Path2D.Double();
        Path2D.Double areaPath = new Path2D.Double();
        double x0 = leftPad + CURVE_POINTS[0][0] * scaleX;
        double y0 = CURVE_POINTS[0][1] * scaleY;
        linePath.moveTo(x0, y0);
        areaPath.moveTo(x0, y0);
        for (int i = 1; i < CURVE_POINTS.length; i += 3){
            double c1x = leftPad + CURVE_POINTS[i][0] * scaleX,     c1y = CURVE_POINTS[i][1] * scaleY;
            double c2x = leftPad + CURVE_POINTS[i + 1][0] * scaleX, c2y = CURVE_POINTS[i + 1][1] * scaleY;
            double ex = leftPad + CURVE_POINTS[i + 2][0] * scaleX,  ey = CURVE_POINTS[i + 2][1] * scaleY;
            linePath.curveTo(c1x, c1y, c2x, c2y, ex, ey);
            areaPath.curveTo(c1x, c1y, c2x, c2y, ex, ey);
        }
        areaPath.lineTo(leftPad + chartW, chartH);
        areaPath.lineTo(leftPad, chartH);
        areaPath.closePath();

        // ===== تعبئة المساحة تحت المنحنى بتدرّج شفاف =====
        GradientPaint gradient = new GradientPaint(0, 0, new Color(37, 99, 235, 50), 0, chartH, new Color(37, 99, 235, 0));
        g2.setPaint(gradient);
        g2.fill(areaPath);

        // ===== رسم الخط نفسه =====
        g2.setColor(Theme.ACCENT_BLUE);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(linePath);

        // ===== تسميات المحور X =====
        g2.setFont(Theme.labelMd());
        g2.setColor(Theme.ON_SURFACE_VARIANT);
        String[] xLabels = {"1st", "8th", "15th", "22nd", "30th"};
        for (int i = 0; i < xLabels.length; i++){
            double xPos = leftPad + chartW * i / (double) (xLabels.length - 1);
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int labelW = fm.stringWidth(xLabels[i]);
            float drawX = (float) xPos - (i == 0 ? 0 : (i == xLabels.length - 1 ? labelW : labelW / 2f));
            g2.drawString(xLabels[i], drawX, h - 4);
        }

        g2.dispose();
    }
}
