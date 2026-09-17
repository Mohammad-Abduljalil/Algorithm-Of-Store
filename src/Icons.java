import javax.swing.Icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

/**
 * أيقونات بسيطة مرسومة يدويًا بالأشكال الهندسية (بدون أي مكتبة أو صور خارجية).
 * ليست مطابقة بكسل-ببكسل لأيقونات Stitch الأصلية، لكنها تُعطي نفس الإيحاء البصري
 * (شخص، قفل، عين، مثلث تحذير...) بأسلوب خطي بسيط (Line Icons) يناسب النمط الاحترافي.
 */
public class Icons {

    private interface Painter {
        void paint(Graphics2D g2, int size, Color color);
    }

    private static class VectorIcon implements Icon {
        private final int size;
        private final Color color;
        private final Painter painter;

        VectorIcon(int size, Color color, Painter painter){
            this.size = size;
            this.color = color;
            this.painter = painter;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            painter.paint(g2, size, color);
            g2.dispose();
        }

        @Override public int getIconWidth(){ return size; }
        @Override public int getIconHeight(){ return size; }
    }

    public static Icon user(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            int headR = s / 4;
            g2.draw(new Ellipse2D.Float(s / 2f - headR / 1.4f, s * 0.12f, headR * 1.4f, headR * 1.4f));
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.15f, s * 0.5f, s * 0.7f, s * 0.55f, 0, 180, java.awt.geom.Arc2D.OPEN));
        });
    }

    public static Icon lock(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            float bodyW = s * 0.6f, bodyH = s * 0.45f;
            float bx = (s - bodyW) / 2f, by = s * 0.45f;
            g2.draw(new RoundRectangle2D.Float(bx, by, bodyW, bodyH, 3, 3));
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.28f, s * 0.12f, s * 0.44f, s * 0.44f, 0, 180, java.awt.geom.Arc2D.OPEN));
            g2.fillOval((int)(s / 2f - 1.5f), (int)(by + bodyH / 2f - 1.5f), 3, 3);
        });
    }

    public static Icon eye(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.05f, s * 0.15f, s * 0.9f, s * 0.7f, 20, 140, java.awt.geom.Arc2D.OPEN));
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.05f, s * 0.15f, s * 0.9f, s * 0.7f, -160, -140, java.awt.geom.Arc2D.OPEN));
            g2.draw(new Ellipse2D.Float(s * 0.4f, s * 0.38f, s * 0.2f, s * 0.2f));
        });
    }

    public static Icon warningTriangle(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            int[] xs = {s / 2, (int)(s * 0.08), (int)(s * 0.92)};
            int[] ys = {(int)(s * 0.1), (int)(s * 0.9), (int)(s * 0.9)};
            g2.drawPolygon(xs, ys, 3);
            g2.drawLine(s / 2, (int)(s * 0.4f), s / 2, (int)(s * 0.65f));
            g2.fillOval(s / 2 - 1, (int)(s * 0.72f), 2, 2);
        });
    }

    public static Icon archiveBox(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new RoundRectangle2D.Float(s * 0.1f, s * 0.15f, s * 0.8f, s * 0.22f, 2, 2));
            g2.draw(new java.awt.geom.Rectangle2D.Float(s * 0.15f, s * 0.4f, s * 0.7f, s * 0.48f));
            g2.drawLine((int)(s * 0.38f), (int)(s * 0.55f), (int)(s * 0.62f), (int)(s * 0.55f));
        });
    }

    public static Icon arrowRight(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.15f), s / 2, (int)(s * 0.8f), s / 2);
            g2.drawLine((int)(s * 0.55f), (int)(s * 0.25f), (int)(s * 0.85f), s / 2);
            g2.drawLine((int)(s * 0.55f), (int)(s * 0.75f), (int)(s * 0.85f), s / 2);
        });
    }

    public static Icon search(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new Ellipse2D.Float(s * 0.1f, s * 0.1f, s * 0.55f, s * 0.55f));
            g2.drawLine((int)(s * 0.58f), (int)(s * 0.58f), (int)(s * 0.9f), (int)(s * 0.9f));
        });
    }

    public static Icon bell(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.2f, s * 0.12f, s * 0.6f, s * 0.6f, 20, 320, java.awt.geom.Arc2D.OPEN));
            g2.drawLine((int)(s * 0.32f), (int)(s * 0.75f), (int)(s * 0.68f), (int)(s * 0.75f));
            g2.drawLine((int)(s * 0.42f), (int)(s * 0.82f), (int)(s * 0.58f), (int)(s * 0.82f));
        });
    }

    public static Icon logout(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new RoundRectangle2D.Float(s * 0.1f, s * 0.15f, s * 0.4f, s * 0.7f, 2, 2));
            g2.drawLine((int)(s * 0.35f), s / 2, (int)(s * 0.9f), s / 2);
            g2.drawLine((int)(s * 0.7f), (int)(s * 0.3f), (int)(s * 0.9f), s / 2);
            g2.drawLine((int)(s * 0.7f), (int)(s * 0.7f), (int)(s * 0.9f), s / 2);
        });
    }

    public static Icon plus(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine(s / 2, (int)(s * 0.15f), s / 2, (int)(s * 0.85f));
            g2.drawLine((int)(s * 0.15f), s / 2, (int)(s * 0.85f), s / 2);
        });
    }

    public static Icon close(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.2f), (int)(s * 0.2f), (int)(s * 0.8f), (int)(s * 0.8f));
            g2.drawLine((int)(s * 0.8f), (int)(s * 0.2f), (int)(s * 0.2f), (int)(s * 0.8f));
        });
    }

    public static Icon chevronRight(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.35f), (int)(s * 0.2f), (int)(s * 0.65f), s / 2);
            g2.drawLine((int)(s * 0.65f), s / 2, (int)(s * 0.35f), (int)(s * 0.8f));
        });
    }

    public static Icon chevronDown(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.2f), (int)(s * 0.35f), s / 2, (int)(s * 0.65f));
            g2.drawLine(s / 2, (int)(s * 0.65f), (int)(s * 0.8f), (int)(s * 0.35f));
        });
    }

    public static Icon moreVert(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.fillOval((int)(s * 0.42f), (int)(s * 0.16f), (int)(s * 0.16f), (int)(s * 0.16f));
            g2.fillOval((int)(s * 0.42f), (int)(s * 0.42f), (int)(s * 0.16f), (int)(s * 0.16f));
            g2.fillOval((int)(s * 0.42f), (int)(s * 0.68f), (int)(s * 0.16f), (int)(s * 0.16f));
        });
    }

    public static Icon info(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new Ellipse2D.Float(s * 0.1f, s * 0.1f, s * 0.8f, s * 0.8f));
            g2.fillOval((int)(s / 2f - 1.5f), (int)(s * 0.28f), 3, 3);
            g2.drawLine(s / 2, (int)(s * 0.45f), s / 2, (int)(s * 0.72f));
        });
    }

    public static Icon trendingUp(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.1f), (int)(s * 0.75f), (int)(s * 0.4f), (int)(s * 0.45f));
            g2.drawLine((int)(s * 0.4f), (int)(s * 0.45f), (int)(s * 0.58f), (int)(s * 0.62f));
            g2.drawLine((int)(s * 0.58f), (int)(s * 0.62f), (int)(s * 0.9f), (int)(s * 0.25f));
            g2.drawLine((int)(s * 0.68f), (int)(s * 0.25f), (int)(s * 0.9f), (int)(s * 0.25f));
            g2.drawLine((int)(s * 0.9f), (int)(s * 0.25f), (int)(s * 0.9f), (int)(s * 0.47f));
        });
    }

    public static Icon calendar(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new RoundRectangle2D.Float(s * 0.12f, s * 0.2f, s * 0.76f, s * 0.68f, 3, 3));
            g2.drawLine((int)(s * 0.12f), (int)(s * 0.38f), (int)(s * 0.88f), (int)(s * 0.38f));
            g2.drawLine((int)(s * 0.3f), (int)(s * 0.1f), (int)(s * 0.3f), (int)(s * 0.28f));
            g2.drawLine((int)(s * 0.7f), (int)(s * 0.1f), (int)(s * 0.7f), (int)(s * 0.28f));
        });
    }

    public static Icon download(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine(s / 2, (int)(s * 0.15f), s / 2, (int)(s * 0.62f));
            g2.drawLine((int)(s * 0.3f), (int)(s * 0.42f), s / 2, (int)(s * 0.62f));
            g2.drawLine((int)(s * 0.7f), (int)(s * 0.42f), s / 2, (int)(s * 0.62f));
            g2.drawLine((int)(s * 0.15f), (int)(s * 0.82f), (int)(s * 0.85f), (int)(s * 0.82f));
        });
    }

    public static Icon lockReset(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            float bodyW = s * 0.5f, bodyH = s * 0.38f;
            float bx = s * 0.15f, by = s * 0.5f;
            g2.draw(new RoundRectangle2D.Float(bx, by, bodyW, bodyH, 3, 3));
            g2.draw(new java.awt.geom.Arc2D.Float(bx + bodyW * 0.15f, s * 0.18f, bodyW * 0.7f, bodyW * 0.7f, 0, 180, java.awt.geom.Arc2D.OPEN));
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.55f, s * 0.42f, s * 0.4f, s * 0.4f, -60, 300, java.awt.geom.Arc2D.OPEN));
        });
    }

    public static Icon personAdd(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new Ellipse2D.Float(s * 0.15f, s * 0.1f, s * 0.4f, s * 0.4f));
            g2.draw(new java.awt.geom.Arc2D.Float(s * 0.02f, s * 0.55f, s * 0.66f, s * 0.5f, 0, 180, java.awt.geom.Arc2D.OPEN));
            g2.drawLine((int)(s * 0.78f), (int)(s * 0.35f), (int)(s * 0.78f), (int)(s * 0.75f));
            g2.drawLine((int)(s * 0.58f), (int)(s * 0.55f), (int)(s * 0.98f), (int)(s * 0.55f));
        });
    }

    public static Icon pencil(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.2f), (int)(s * 0.8f), (int)(s * 0.65f), (int)(s * 0.35f));
            g2.drawLine((int)(s * 0.65f), (int)(s * 0.35f), (int)(s * 0.8f), (int)(s * 0.5f));
            g2.drawLine((int)(s * 0.8f), (int)(s * 0.5f), (int)(s * 0.35f), (int)(s * 0.95f));
            g2.drawLine((int)(s * 0.2f), (int)(s * 0.8f), (int)(s * 0.35f), (int)(s * 0.95f));
        });
    }

    public static Icon trash(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.drawLine((int)(s * 0.2f), (int)(s * 0.28f), (int)(s * 0.8f), (int)(s * 0.28f));
            g2.draw(new RoundRectangle2D.Float(s * 0.28f, s * 0.28f, s * 0.44f, s * 0.6f, 2, 2));
            g2.drawLine((int)(s * 0.4f), (int)(s * 0.18f), (int)(s * 0.6f), (int)(s * 0.18f));
        });
    }

    public static Icon dashboardGrid(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            float g = s * 0.1f, cell = s * 0.36f;
            g2.draw(new RoundRectangle2D.Float(g, g, cell, cell, 2, 2));
            g2.draw(new RoundRectangle2D.Float(s - g - cell, g, cell, cell, 2, 2));
            g2.draw(new RoundRectangle2D.Float(g, s - g - cell, cell, cell, 2, 2));
            g2.draw(new RoundRectangle2D.Float(s - g - cell, s - g - cell, cell, cell, 2, 2));
        });
    }

    public static Icon truck(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new RoundRectangle2D.Float(s * 0.08f, s * 0.32f, s * 0.5f, s * 0.36f, 2, 2));
            g2.draw(new RoundRectangle2D.Float(s * 0.58f, s * 0.42f, s * 0.34f, s * 0.26f, 2, 2));
            g2.draw(new Ellipse2D.Float(s * 0.18f, s * 0.68f, s * 0.16f, s * 0.16f));
            g2.draw(new Ellipse2D.Float(s * 0.68f, s * 0.68f, s * 0.16f, s * 0.16f));
        });
    }

    public static Icon barChart(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new java.awt.geom.Rectangle2D.Float(s * 0.15f, s * 0.55f, s * 0.18f, s * 0.32f));
            g2.draw(new java.awt.geom.Rectangle2D.Float(s * 0.41f, s * 0.35f, s * 0.18f, s * 0.52f));
            g2.draw(new java.awt.geom.Rectangle2D.Float(s * 0.67f, s * 0.18f, s * 0.18f, s * 0.69f));
        });
    }

    public static Icon shield(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            int[] xs = {(int)(s*0.5f),(int)(s*0.85f),(int)(s*0.85f),(int)(s*0.5f),(int)(s*0.15f),(int)(s*0.15f)};
            int[] ys = {(int)(s*0.08f),(int)(s*0.25f),(int)(s*0.55f),(int)(s*0.92f),(int)(s*0.55f),(int)(s*0.25f)};
            g2.drawPolygon(xs, ys, 6);
        });
    }

    public static Icon gear(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new Ellipse2D.Float(s * 0.3f, s * 0.3f, s * 0.4f, s * 0.4f));
            for (int i = 0; i < 8; i++){
                double angle = Math.PI * 2 * i / 8;
                float x1 = (float)(s * 0.5 + Math.cos(angle) * s * 0.35);
                float y1 = (float)(s * 0.5 + Math.sin(angle) * s * 0.35);
                float x2 = (float)(s * 0.5 + Math.cos(angle) * s * 0.46);
                float y2 = (float)(s * 0.5 + Math.sin(angle) * s * 0.46);
                g2.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            }
        });
    }

    public static Icon dollarCircle(int size, Color color){
        return new VectorIcon(size, color, (g2, s, c) -> {
            g2.draw(new Ellipse2D.Float(s * 0.1f, s * 0.1f, s * 0.8f, s * 0.8f));
            g2.drawLine(s / 2, (int)(s * 0.28f), s / 2, (int)(s * 0.72f));
            g2.drawLine((int)(s * 0.35f), (int)(s * 0.38f), (int)(s * 0.65f), (int)(s * 0.38f));
        });
    }

    private Icons(){}
}
