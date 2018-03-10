package com.example.administrator.mytestproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by Siy on 2016/8/25.
 */
public class MyPopupWindow extends PopupWindow {
    private Context mContext;
    private View mView;

    public MyPopupWindow(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        this.mView = new LinearLayout(context);
        this.mView.setBackgroundColor(Color.RED);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(getScreenSize()[1] / 4);
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOutsideTouchable(true);
        setFocusable(true);
        setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(mView);
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        showPopupWindow();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        showPopupWindow();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        showPopupWindow();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        showPopupWindow();
    }

    private void showPopupWindow() {
        setWindowBackgroundAlpha(0.9f);
        if (mShowingListener != null) {
            mShowingListener.onShowing();
        }
    }

    @Override
    public void dismiss() {
        setWindowBackgroundAlpha(1.0f);
        super.dismiss();
    }

    /**
     * 控制窗口背景的不透明度 *
     */
    private void setWindowBackgroundAlpha(float alpha) {
        Window window = ((Activity) mContext).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    public interface ShowingListener {
        public void onShowing();
    }

    private ShowingListener mShowingListener;

    public void setShowingListener(ShowingListener showingListener) {
        this.mShowingListener = showingListener;
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

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        wm.getDefaultDisplay().getMetrics(dm);

        size[0] = dm.widthPixels;
        size[1] = dm.heightPixels;
        return size;
    }
}
