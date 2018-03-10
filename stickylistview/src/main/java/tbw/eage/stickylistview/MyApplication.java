package tbw.eage.stickylistview;

import android.app.Application;

import tbw.eage.stickylistview.RxBus.RxBus;

/**
 * Created by Siy on 2016/8/26.
 */
public class MyApplication extends Application{
    private static MyApplication CONTEXT;

    private RxBus mRxBus;


    @Override
    public void onCreate() {
        super.onCreate();
        CONTEXT = this;
    }

    public static MyApplication getInstance() {
        return CONTEXT;
    }

    public RxBus getRxBusSingleton() {
        if (mRxBus == null) {
            mRxBus = new RxBus();
        }
        return mRxBus;
    }
}
