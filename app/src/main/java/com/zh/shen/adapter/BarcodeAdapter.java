package com.zh.shen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zh.shen.R;
import com.zh.shen.bean.BarcodeBean;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/30.
 */
public class BarcodeAdapter extends BaseAdapter {

    private ArrayList<BarcodeBean> mBeanArrayList = new ArrayList<>();
    private Context mContext;

    public BarcodeAdapter(Context context, ArrayList<BarcodeBean> barcodeBeenList){

        mContext = context;
        mBeanArrayList = barcodeBeenList;
    }

    @Override
    public int getCount() {
        return mBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHold viewHold;
        if(convertView == null){                // 拿缓存
            // 将 layout 填充成"View"
            LayoutInflater inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.item_lv_barcode,parent,false); // listview中每一项的布局

            viewHold = new ViewHold(convertView);

            convertView.setTag(viewHold);
        }else {
            viewHold = (ViewHold) convertView.getTag();
        }

        viewHold.tvId.setText(mBeanArrayList.get(position).getId());
        viewHold.tvTableNum.setText(mBeanArrayList.get(position).getTableNumber());
        viewHold.tvOldTitleNum.setText(mBeanArrayList.get(position).getOldTitleNumber());
        viewHold.tvNewTitleNum.setText(mBeanArrayList.get(position).getNewTitleNumber());
        viewHold.tvTime.setText(mBeanArrayList.get(position).getTime());

        return convertView;
    }


    class ViewHold{

        public TextView tvId;
        public TextView tvTableNum;
        public TextView tvOldTitleNum;
        public TextView tvNewTitleNum;
        public TextView tvTime;

        public ViewHold(View view) {

            tvId = (TextView) view.findViewById(R.id.tv_id);
            tvTableNum = (TextView) view.findViewById(R.id.tv_table_num);
            tvOldTitleNum = (TextView) view.findViewById(R.id.tv_old_title_num);
            tvNewTitleNum = (TextView) view.findViewById(R.id.tv_new_title_num);
            tvTime = (TextView) view.findViewById(R.id.tv_time);
        }


    }
}
