import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

/**
 * حاوية محتوى داخل JScrollPane تُجبر عرضها على مطابقة عرض منطقة العرض (Viewport).
 *
 * بدونها يحتفظ المحتوى بعرضه "المفضّل" حتى لو كان أوسع من النافذة، فيُقصّ من اليمين
 * (كانت بطاقات KPI وعمود النشاط الأخير تختفي جزئيًا عند تصغير النافذة).
 * مع getScrollableTracksViewportWidth()=true يُعاد توزيع العرض المتاح على العناصر،
 * فتتقلّص البطاقات بدل أن تخرج خارج الشاشة - وهذا أساس التجاوب الأفقي الحقيقي.
 */
public class ScrollableContentPanel extends JPanel implements Scrollable {

    public ScrollableContentPanel(LayoutManager layout){
        super(layout);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize(){
        return getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction){
        return 16;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction){
        return orientation == javax.swing.SwingConstants.VERTICAL
                ? visibleRect.height : visibleRect.width;
    }

    @Override
    public boolean getScrollableTracksViewportWidth(){
        return true; // العرض يتبع النافذة دائمًا: لا تمدّد أفقي ولا قصّ من اليمين
    }

    @Override
    public boolean getScrollableTracksViewportHeight(){
        // الارتفاع يتبع النافذة فقط عندما يكون المحتوى أقصر منها،
        // وإلا نترك التمرير العمودي يعمل بشكل طبيعي.
        return getParent() instanceof javax.swing.JViewport
                && getParent().getHeight() > getPreferredSize().height;
    }
}
