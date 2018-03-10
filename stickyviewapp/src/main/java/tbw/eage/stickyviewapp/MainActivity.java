package tbw.eage.stickyviewapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Siy on 2016/8/26.
 */
public class MainActivity extends AppCompatActivity implements ObservableScrollView.Callbacks {

    private View stickyView;

    private View stopView;

    private View titleView;

    private int titleViewHeight;

    private ListView list;

    private ObservableScrollView observableScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        stickyView = findViewById(R.id.scendview);

        stopView = findViewById(R.id.stopview);

        titleView = findViewById(R.id.relativeLayout1);

        titleViewHeight = titleView.getLayoutParams().height;

        list = (ListView) findViewById(R.id.content);
        list.setAdapter(new CardealerCarSourceListAdapter(this));

        observableScrollView = (ObservableScrollView) findViewById(R.id.observableview);
        observableScrollView.setCallbacks(this);

        // 滚动范围
        observableScrollView.scrollTo(0, 0);
        observableScrollView.smoothScrollTo(0, 0);//设置scrollView默认滚动到顶部

    }

    @Override
    public void onScrollChanged(int scrollY) {
        stickyView.setTranslationY(Math.max(stopView.getTop() - titleViewHeight, scrollY));
        titleView.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(this, (float)((scrollY * 1.0 / (stopView.getTop())) > 1 ? 1 : (scrollY * 1.0 / (stopView.getTop()))), R.color.colorTransparent, R.color.colorWhite));
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }
}
