package com.zh.shen.fragment;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zh.shen.R;
import com.zh.shen.adapter.BarcodeAdapter;
import com.zh.shen.bean.BarcodeBean;
import com.zh.shen.config.Constant;
import com.zh.shen.db.biz.TableEx;
import com.zh.shen.utils.ExcelUtil;
import com.zh.shen.utils.TimeUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 标签 -- 新增
 *
 */
public class ChartFragment extends Fragment implements View.OnClickListener, OnDateSetListener {

    /** 日期 -- 文本 */
    private TextView mTvTime;

    /** 查询--按钮 */
    private Button mBtnQuery;
    /** 生成excel--按钮 */
    private Button mBtnCreateExcel;
    /** 列表框--ListView */
    private ListView mLvBarcode;
    /** 列表框适配器--Adapter */
    private BarcodeAdapter barcodeAdapter;

    ArrayList<BarcodeBean> mBarcodeBeanList = new ArrayList<>();

    int mType = 0;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:						// 初始化完成"二维码"，现在初始化"RFID"

                    break;


                default:
                    break;
            }
        };
    };

    public static ChartFragment newInstance(int type) {
        ChartFragment f = new ChartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);            // 在MainActivity传递
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 当Activity类动态加载fragment时可以通过fragment的setArguments()传入值，
        // 并在fragment类中通过fragment的getArguments()方法获得传入的值；
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        initView(view);
        initListener();
        initData();

        return view;
    }


    private void initView(View view) {

        mTvTime = (TextView) view.findViewById(R.id.tv_time);

        mBtnQuery = (Button) view.findViewById(R.id.btn_query);
        mBtnCreateExcel = (Button) view.findViewById(R.id.btn_create_excel);

        mLvBarcode = (ListView) view.findViewById(R.id.lv_barcode);


    }

    private void initListener() {

        mTvTime.setOnClickListener(this);

        mBtnQuery.setOnClickListener(this);
        mBtnCreateExcel.setOnClickListener(this);
    }


    private void initData() {

        barcodeAdapter = new BarcodeAdapter(getActivity(), new ArrayList<BarcodeBean>());

        mTvTime.setText(TimeUtils.getCurrentyyyyMMdd());

    }


    private void query(){

        new queryTask().execute();
    }

    private void createExcel() {

        new createExcelTask().execute();
    }

    /**
     * 设置控件是否可以"交互"
     *
     * @param b 是否可以交互
     */
    private void setViewEnabled(Boolean b){


    }



    /**
     *
     * 查询获取"数据库的记录" -- 异步类
     *
     */
    public class queryTask extends AsyncTask<String, Integer, ArrayList<BarcodeBean>> {
        /** 进度条对话框 */
        ProgressDialog mypDialog;
        /** 数据库的表的操作类 */
        TableEx mTableEx;

        String mTime = "";

        @Override
        protected void onPreExecute() {								// 执行前
            // TODO Auto-generated method stub
            super.onPreExecute();

            mTableEx = new TableEx(getActivity());
            mTime = mTvTime.getText().toString().trim();

            mypDialog = new ProgressDialog(getActivity());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("正在查询...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }


        @Override
        protected ArrayList<BarcodeBean> doInBackground(String... params) {		// 执行中
            mBarcodeBeanList = new ArrayList<>();

            Cursor cursor = mTableEx.Query(Constant.TABLE_BARCODE,null,"time=?",
                    new String[]{mTime},null,null,null);

            while (cursor.moveToNext()) {
                BarcodeBean bean = new BarcodeBean();

                bean.setId(cursor.getString(0));
                bean.setTableNumber(cursor.getString(1));
                bean.setOldTitleNumber(cursor.getString(2));
                bean.setNewTitleNumber(cursor.getString(3));
                bean.setTime(cursor.getString(4));

                mBarcodeBeanList.add(bean);
            }
            return mBarcodeBeanList;
        }

        @Override
        protected void onPostExecute(ArrayList<BarcodeBean> beanList) {				// 执行后
            super.onPostExecute(beanList);

            barcodeAdapter = new BarcodeAdapter(getActivity(), beanList);
            mLvBarcode.setAdapter(barcodeAdapter);

            mypDialog.cancel();

            mHandler.sendEmptyMessage(0);

        }
    }


    /**
     * 生成Excel -- 异步类
     *
     */
    public class createExcelTask extends AsyncTask<String, Integer, String> {
        /** 进度条对话框 */
        ProgressDialog mypDialog;

        @Override
        protected void onPreExecute() {								// 执行前
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(getActivity());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("正在生成Excel...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {		// 执行中

            try {
                ExcelUtil.writeExcel(getActivity(), mBarcodeBeanList, "扫描表号");
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "";
        }

        @Override
        protected void onPostExecute(String str) {				// 执行后
            super.onPostExecute(str);


            mypDialog.cancel();
            mHandler.sendEmptyMessage(0);

        }
    }


    /***************************  弹出窗口 选择时间 -- start **************************************/
    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        Date date = new Date(millseconds);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//精确到分钟
        String str = format.format(date);
        mTvTime.setText(str);
    }



    private void getTime() {
        TimePickerDialog mDialogYearMonthDay = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
                .setThemeColor(getResources().getColor(R.color.blue))
                .setCallBack(this)
                .build();
        mDialogYearMonthDay.show(getActivity().getSupportFragmentManager(), "YEAR_MONTH_DAY");
    }
    /**************************  弹出窗口 选择时间 -- end ***************************************/



    @Override
    public void onResume() {								// 生命周期 -- 重新开始
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    public void onPause() {								    // 生命周期
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btn_query:                     // 保存按钮
                query();
                break;

            case R.id.btn_create_excel:
                createExcel();
                break;

            case R.id.tv_time:                      // 选择日期
                getTime();
                break;
        }
    }


}
