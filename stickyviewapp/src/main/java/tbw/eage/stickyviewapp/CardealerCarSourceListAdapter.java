package tbw.eage.stickyviewapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 车源列表的适配器
 * <p/>
 * Created by Siy on 2016/6/4.
 */
public class CardealerCarSourceListAdapter extends BaseAdapter {
    private Context mContext;

    private LayoutInflater mInflater;



    public CardealerCarSourceListAdapter(Context context) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return 1000;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG","getView()");
        if (convertView == null) {
            Log.d("TAG","null");
            convertView = mInflater.inflate(R.layout.fragment_layout_carsource_item2, parent, false);
        }
        return convertView;
    }
}
