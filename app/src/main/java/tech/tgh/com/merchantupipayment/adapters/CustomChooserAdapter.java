package tech.tgh.com.merchantupipayment.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import tech.tgh.com.merchantupipayment.events.ListEvent;
import tech.tgh.com.merchantupipayment.R;
import tech.tgh.com.merchantupipayment.models.EvalInstalledAppInfo;

public class CustomChooserAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<EvalInstalledAppInfo> mData;
    private LayoutInflater inflater;
    private ViewHolder vh;
    private ListEvent mCallBack;

    public CustomChooserAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setDataAdapter(ArrayList<EvalInstalledAppInfo> mData,ListEvent mCallBack) {
        this.mData = mData;
        this.mCallBack = mCallBack;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        if(view == null){
            vh = new ViewHolder();
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_chooser_row,null);

            vh.appImg = view.findViewById(R.id.appIcon);
            vh.appName = view.findViewById(R.id.appName);
            view.setTag(vh);
        }else{
            vh = (ViewHolder) view.getTag();
        }

        vh.appImg.setImageDrawable(mData.get(i).getAppIcon());
        vh.appName.setText(mData.get(i).getSimpleName());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBack.onClick(i);
            }
        });
        return view;
    }

    class ViewHolder{
        ImageView appImg;
        TextView appName;
    }
}
