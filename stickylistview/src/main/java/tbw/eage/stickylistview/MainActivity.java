package tbw.eage.stickylistview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import tbw.eage.stickylistview.RxBus.RxBus;
import tbw.eage.stickylistview.RxBus.RxUtils;
import tbw.eage.stickylistview.SmoothListView.SmoothListView;
import tbw.eage.stickylistview.view.CardealFilterView;
import tbw.eage.stickylistview.view.CardealerHeaderFilterView;
import tbw.eage.stickylistview.view.CardealerHeaderMsgView;

public class MainActivity extends AppCompatActivity implements SmoothListView.ISmoothListViewListener {
    private CardealerHeaderMsgView mCardealMsgView;

    private CardealerHeaderFilterView mCardealFilterView;

    private SmoothListView mListView;

    private boolean isSmooth = false; // 没有吸附的前提下，是否在滑动

    private boolean isScrollIdle = true; // ListView是否在滑动

    private int msgViewTopSpace; // 车商视图距离顶部的距离

    private int filterViewTopSpace; // 筛选视图距离顶部的距离

    private int msgViewHeight = 250; // 广告视图的高度

    private View filterView; // 从ListView获取的筛选子View

    private boolean isStickyTop = false; // 是否吸附在顶部

    private View msgView;

    private int filterViewPosition;

    private CardealFilterView fvTopFilter;

    private View rlBar;

    /**
     * 筛选的位置
     * <p/>
     * button1 是历史寻车
     * button2 是历史车源
     * button3 是联系我们
     */
    private int mFilterPosition;

    private CardealerCarSourceListAdapter c;

    private Subscription mSub;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initField();
        initListener();
    }

    private void initField() {
        mListView = (SmoothListView) findViewById(R.id.listview1);
        rlBar = findViewById(R.id.relativeLayout1);
        fvTopFilter = (CardealFilterView) findViewById(R.id.filter);
        mCardealMsgView = new CardealerHeaderMsgView(this);
        mCardealFilterView = new CardealerHeaderFilterView(this);
        mCardealMsgView.fillView(new Object(), mListView);
        mCardealFilterView.fillView(new Object(), mListView);
        mListView.setRefreshEnable(false);
        mListView.setLoadMoreEnable(false);
        mListView.setSmoothListViewListener(this);
        mListView.setAdapter(c = new CardealerCarSourceListAdapter(this, DisplayUtil.dip2px(MainActivity.this, 50 + 38.5f)));
        c.addTenToList();

        filterViewPosition = mListView.getHeaderViewsCount() - 1;
    }

    private void initListener() {
        RxBus rxBus = AndroidUtils.getRxBus();
        mSub = rxBus.toObserverable()
                .publish(new Func1<Observable<Object>, Observable<List<Object>>>() {
                    @Override
                    public Observable<List<Object>> call(Observable<Object> objectObservable) {
                        return objectObservable.buffer(objectObservable.debounce(50, TimeUnit.MILLISECONDS));
                    }
                })
                .map(new Func1<List<Object>, List<Object>>() {
                    @Override
                    public List<Object> call(List<Object> objects) {
                        List<Object> list1 = new ArrayList<>();
                        List<Object> list2 = new ArrayList<>();
                        for (Object o : objects) {
                            if (o instanceof CardealerHeaderFilterView.ClickEvent || o instanceof CardealerHeaderFilterView.CheckedChangeEvent) {
                                list1.add(o);
                            } else if (o instanceof CardealFilterView.ClickEvent || o instanceof CardealFilterView.CheckedChangeEvent) {
                                list2.add(o);
                            }
                        }
                        if (list1.size() > 0) {
                            return list1;
                        } else {
                            return list2;
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Object>>() {
                               @Override
                               public void call(List<Object> objects) {

                                   for (Object o : objects) {
                                       if (o instanceof CardealerHeaderFilterView.ClickEvent) {
                                           if (!isStickyTop) {
                                               mListView.smoothScrollToPositionFromTop(mListView.getHeaderViewsCount() - 1, DisplayUtil.dip2px(MainActivity.this, 50));
                                           }
                                       }

                                       if (o instanceof CardealerHeaderFilterView.CheckedChangeEvent) {
                                           mFilterPosition = ((CardealerHeaderFilterView.CheckedChangeEvent) o).position;
                                           isSmooth = true;
                                           mListView.smoothScrollToPositionFromTop(mListView.getHeaderViewsCount() - 1, DisplayUtil.dip2px(MainActivity.this, 50));
                                       }

                                       if (o instanceof CardealFilterView.ClickEvent) {
                                           Log.e("TAG", "viewClick");
                                       }

                                       if (o instanceof CardealFilterView.CheckedChangeEvent) {
                                           onFilterViewClick(((CardealFilterView.CheckedChangeEvent) o).position);
                                       }
                                   }
                               }
                           }

                );

        mListView.setOnScrollListener(new SmoothListView.OnSmoothScrollListener() {
                                          @Override
                                          public void onSmoothScrolling(View view) {
                                          }

                                          @Override
                                          public void onScrollStateChanged(AbsListView view, int scrollState) {
                                              isScrollIdle = (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE);
                                          }

                                          @Override
                                          public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                                               int totalItemCount) {
                                              if (isScrollIdle && msgViewTopSpace < 0) return;

                                              // 获取广告头部View、自身的高度、距离顶部的高度
                                              if (msgView == null) {
                                                  msgView = mListView.getChildAt(1 - firstVisibleItem);
                                              }
                                              if (msgView != null) {
                                                  msgViewTopSpace = DisplayUtil.px2dip(MainActivity.this, msgView.getTop());
                                                  msgViewHeight = DisplayUtil.px2dip(MainActivity.this, msgView.getHeight());
                                              }

                                              // 获取筛选View、距离顶部的高度
                                              if (filterView == null) {
                                                  filterView = mListView.getChildAt(filterViewPosition - firstVisibleItem);
                                              }
                                              if (filterView != null) {
                                                  filterViewTopSpace = DisplayUtil.px2dip(MainActivity.this, filterView.getTop());
                                              }

                                              // 处理筛选是否吸附在顶部
                                              if (filterViewTopSpace > 50) {
                                                  isStickyTop = false; // 没有吸附在顶部
                                                  fvTopFilter.setVisibility(View.GONE);
                                                  if (mCardealFilterView.getMsub().isUnsubscribed()) {
                                                      mCardealFilterView.setObservable();
                                                  }
                                              } else {
                                                  isStickyTop = true; // 吸附在顶部
                                                  fvTopFilter.setVisibility(View.VISIBLE);
                                                  RxUtils.unsubscribeIfNotNull(mCardealFilterView.getMsub());
                                              }

                                              if (firstVisibleItem > filterViewPosition) {
                                                  isStickyTop = true;
                                                  fvTopFilter.setVisibility(View.VISIBLE);
                                              }

                                              updateFilterView();
                                              fvTopFilter.setmIsStickyTop(isStickyTop);

                                              if (isSmooth && isStickyTop) {
                                                  isSmooth = false;
                                                  onFilterViewClick(mFilterPosition);
                                              }

                                              // 处理标题栏颜色渐变
                                              handleTitleBarColorEvaluate();
                                          }
                                      }

        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        RxUtils.unsubscribeIfNotNull(mSub);
        RxUtils.unsubscribeIfNotNull(mCardealFilterView.getMsub());
    }

    /**
     * 完成真假FilterView
     */
    private void updateFilterView() {
        if (mCardealFilterView != null) {
            CardealFilterView tempView = mCardealFilterView.getmFilterView();
            if (tempView != null) {
                tempView.setItemChecked(mFilterPosition);
            }
        }

        if (fvTopFilter != null) {
            fvTopFilter.setItemChecked(mFilterPosition);
        }
    }

    /**
     * 点击筛选按钮
     *
     * @param position
     */
    private void onFilterViewClick(int position) {
        mFilterPosition = position;
        switch (position) {
            case R.id.button1:
                c.addTenToList();
                break;
            case R.id.button2:
                c.addOneDataToList();
                break;
            case R.id.button3:
                c.setEmptyView();
                break;
        }
    }


    // 处理标题栏颜色渐变
    private void handleTitleBarColorEvaluate() {
        float fraction;
        if (msgViewTopSpace > 0) {
            fraction = 1f - msgViewTopSpace * 1f / 60;
            if (fraction < 0f) fraction = 0f;
            rlBar.setAlpha(fraction);
            return;
        }

        float space = Math.abs(msgViewTopSpace) * 1f;
        fraction = space / (msgViewHeight - 50);
        if (fraction < 0f) fraction = 0f;
        if (fraction > 1f) fraction = 1f;
        rlBar.setAlpha(1f);

        if (fraction >= 1f || isStickyTop) {
            isStickyTop = true;

            rlBar.setBackgroundColor(AndroidUtils.getColor(R.color.colorWhite));
        } else {
            rlBar.setBackgroundColor(ColorUtil.getNewColorByStartEndColor(this, fraction, R.color.colorTransparent, R.color.colorWhite));
        }
    }


    @Override
    public void onRefresh() {
    }

    @Override
    public void onLoadMore() {

    }
}
