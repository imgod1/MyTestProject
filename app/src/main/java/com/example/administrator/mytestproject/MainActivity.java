package com.example.administrator.mytestproject;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by Siy on 2016/8/25.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        textView = (TextView) findViewById(R.id.textview);
        textView.setOnClickListener(this);

//        xxxx();
        PublishSubject<String> operation = PublishSubject.create();


        operation
                .subscribeOn(Schedulers.io());

//        ConnectableObservable tapEventEmitter = operation.publish();

        initSoftListener();

        operation.
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("TAG_TAG", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("TAG_TAG", "1_" + s);
                    }
                });

        operation.
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d("TAG_TAG", "onCompleted1");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("TAG_TAG", "2_" + s);
                    }
                });
//        tapEventEmitter.connect();

        operation.onNext("Foo");
        operation.onNext("Bar");

        operation.onCompleted();
    }

    /*private void  xxxx(){
        Class clazz = textView.getClass();
        try {
            Field field = clazz.getDeclaredField("editable");
            field.setAccessible(true);
            Boolean object = field.getBoolean(textView);
           object = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private MyPopupWindow myPopupWindow;

    @Override
    public void onClick(View v) {
        if (myPopupWindow == null) {
            myPopupWindow = new MyPopupWindow(this);
            myPopupWindow.setShowingListener(new MyPopupWindow.ShowingListener() {
                @Override
                public void onShowing() {
                    showSoftInput(getCurrentFocus());
                }
            });

            myPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    hideSoftInput(getCurrentFocus().getWindowToken());
                }
            });
        }
        myPopupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.BOTTOM, 0, 0);
    }

    private void initSoftListener(){
        findViewById(android.R.id.content).addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
                if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > getScreenSize()[1]/3)){
                    onOpenSoftInput();

                }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom >  getScreenSize()[1]/3)){
                    onCloseSoftInput();
                }
            }
        });
    }

    /**
     * 当软键盘打开时回调
     */
    protected void onOpenSoftInput(){

    }

    /**
     * 当软键盘关闭时回调
     */
    protected  void onCloseSoftInput(){
        if (myPopupWindow != null && myPopupWindow.isShowing()) {
            myPopupWindow.dismiss();
        }
    }

    /**
     * 获取屏幕的宽高
     *
     * @return int[0] 代表屏幕的宽
     * <br/>
     * int[1] 代表屏幕的高
     */
    public int[] getScreenSize() {
        int[] size = new int[2];

        DisplayMetrics dm = new DisplayMetrics();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(dm);

        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }

    @Override
    public void onBackPressed() {
        if (myPopupWindow !=null){
            myPopupWindow.dismiss();
        }
        super.onBackPressed();
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token 聚焦view的toen
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 聚焦的view
     *
     * @param view
     */
    public void showSoftInput(View view) {
        //1.得到InputMethodManager对象
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //2.调用showSoftInput方法显示软键盘，其中view为聚焦的view组件
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

}
