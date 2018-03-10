package tbw.eage.stickyviewapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Siy on 2016/8/25.
 */
public class MyXListView extends ListView {
    public MyXListView(Context context) {
        this(context,null);
    }

    public MyXListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyXListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}
