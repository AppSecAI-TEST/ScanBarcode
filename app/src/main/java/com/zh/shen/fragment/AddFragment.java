package com.zh.shen.fragment;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zh.shen.R;
import com.zh.shen.config.Constant;
import com.zh.shen.db.biz.TableEx;
import com.zh.shen.utils.TimeUtils;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.zebra.adc.decoder.Barcode2DWithSoft;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * 标签 -- 新增
 *
 */
public class AddFragment extends Fragment implements View.OnClickListener, OnDateSetListener {

    /** 扫描表号 */
    private static final int SCANTABLE = 1;
    /** 扫描原铅号 */
    private static final int SCANOLDTITLE = 2;
    /** 扫描新铅号 */
    private static final int SCANNEWTITLE = 3;

    private int mCurrentScan = 0;


    /** 初始化完成"二维码、一维码" */
    private static final int initReaderFinish = 100;
    /** 扫描完成"二维码、一维码"，得到的结果 */
    private final int Read_ScanResult = 200;


    /** 表号 -- 编辑框 */
    private EditText mEtTableNumber;
    /** 原铅号 -- 编辑框 */
    private EditText mEtOldTitleNumber;
    /** 新铅号 -- 编辑框 */
    private EditText mEtNewTitleNumber;

    /** 日期 -- 文本 */
    private TextView mTvTime;

    /** 扫描表号 -- 按钮*/
    private Button mBtnTableNumber;
    /** 扫描原铅号 -- 按钮 */
    private Button mBtnOldTitleNumber;
    /** 扫描新铅号 -- 按钮 */
    private Button mBtnNewTitleNumber;
    /** 保存 -- 按钮 */
    private Button mBtnSave;


    /** "二维码、一维码"，对应的文件描述符（或驱动什么的） */
    public Barcode2DWithSoft mReader;




    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

                case initReaderFinish:						// 初始化完成"二维码"，现在初始化"RFID"

                    break;

                case Read_ScanResult:									// 扫描完成"二维码、一维码"，得到的结果
                    if(msg.obj != null){
                        String str = new String((byte[])msg.obj);			// 将 byte[] 转换成 String

                        if(mCurrentScan == SCANTABLE)
                            mEtTableNumber.setText(str);
                        if(mCurrentScan == SCANOLDTITLE)
                            mEtOldTitleNumber.setText(str);
                        if(mCurrentScan == SCANNEWTITLE)
                            mEtNewTitleNumber.setText(str);

                        //setViewEnabled(true);
                    }

                    break;

                default:
                    break;
            }
        };
    };


    public static AddFragment newInstance() {
        AddFragment f = new AddFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        initView(view);
        initListener();
        initData();
        return view;
    }


    private void initView(View view) {

        mEtTableNumber = (EditText) view.findViewById(R.id.et_table_number);
        mEtOldTitleNumber = (EditText) view.findViewById(R.id.et_old_title_number);
        mEtNewTitleNumber = (EditText) view.findViewById(R.id.et_new_title_number);


        mTvTime = (TextView) view.findViewById(R.id.tv_time);

        mBtnTableNumber = (Button) view.findViewById(R.id.btn_table_number);
        mBtnOldTitleNumber = (Button) view.findViewById(R.id.btn_old_title_number);
        mBtnNewTitleNumber = (Button) view.findViewById(R.id.btn_new_title_number);
        mBtnSave = (Button) view.findViewById(R.id.btn_save);

    }

    private void initListener() {

        mBtnTableNumber.setOnClickListener(this);
        mBtnOldTitleNumber.setOnClickListener(this);
        mBtnNewTitleNumber.setOnClickListener(this);
        mTvTime.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

    }


    private void initData() {

        try {														// 拿到"二维码、一维码"扫描 实例
            mReader = Barcode2DWithSoft.getInstance();
        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getMessage(), LENGTH_SHORT).show();
            return;
        }


        mTvTime.setText(TimeUtils.getCurrentyyyyMMdd());
    }



    /**
     * 设置控件是否可以"交互"
     *
     * @param b 是否可以交互
     */
    private void setViewEnabled(Boolean b){

        mBtnTableNumber.setEnabled(b);
        mBtnOldTitleNumber.setEnabled(b);
        mBtnNewTitleNumber.setEnabled(b);

        mEtTableNumber.setEnabled(b);
        mEtOldTitleNumber.setEnabled(b);
        mEtNewTitleNumber.setEnabled(b);

    }

    /**
     * "条码采集"按钮
     */
    private void doDecode(int currentScan) {

        //mReader.stopScan();
        //setViewEnabled(false);

        mCurrentScan = currentScan;

        if (mReader != null) {
            mReader.setScanCallback(mScanCallback);
        }
        mReader.scan();
    }


    /**
     * "条码采集" 采集之后的"CallBack --回写"
     */
    public Barcode2DWithSoft.ScanCallback mScanCallback = new Barcode2DWithSoft.ScanCallback() {

        /**
         *
         * @param i
         * @param length		扫描获取的"数据长度"
         * @param data			扫描获取的"数据"
         */
        @Override
        public void onScanComplete(int i, int length, byte[] data) {

            Log.i("ErDSoftScanFragment", "onScanComplete() i=" + i);

            if (length < 1) {
                return;
            }

            mReader.stopScan();						// 停止扫描！

            Message msg = new Message();
            msg.what = Read_ScanResult;				// 扫描完成"二维码、一维码"，得到的结果
            msg.obj = data;                         // 扫描获取到的"数据"

            mHandler.sendMessage(msg);
        }

    };


    /**
     * 拿到实例了，还要上电 -- "二维码、一维码"扫描头
     * 设备上电 -- 异步类
     *
     */
    public class InitTask extends AsyncTask<String, Integer, Boolean> {
        /** 进度条对话框 */
        ProgressDialog mypDialog;


        @Override
        protected void onPreExecute() {								// 执行前
            // TODO Auto-generated method stub
            super.onPreExecute();

            mypDialog = new ProgressDialog(getActivity());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("init...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }


        @Override
        protected Boolean doInBackground(String... params) {		// 执行中

            boolean result = false;

            if (mReader != null) {
                result = mReader.open(getActivity());

                if (result) {
                    mReader.setParameter(324, 1);
                    mReader.setParameter(300, 0); // Snapshot Aiming -- 快照的目标
                    mReader.setParameter(361, 0); // Image Capture Illumination -- 图像捕获照明
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean result) {				// 执行后
            super.onPostExecute(result);

            mypDialog.cancel();

            mHandler.sendEmptyMessage(initReaderFinish);

            if (!result) {
                Toast.makeText(getActivity(), "init fail", LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 将当期数据保存到数据库中
     */
    private void save(){

        String strTableNumber = mEtTableNumber.getText().toString().trim();
        String strOldTitleNumber = mEtOldTitleNumber.getText().toString().trim();
        String strNewTitleNumber = mEtNewTitleNumber.getText().toString().trim();
        String strTime = mTvTime.getText().toString().trim();

        if(TextUtils.isEmpty(strTableNumber)){
            Toast.makeText(getActivity(), "请输入'表号'", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(strOldTitleNumber)){
            Toast.makeText(getActivity(), "请输入'原铅号'", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(strNewTitleNumber)){
            Toast.makeText(getActivity(), "请输入'新铅号'", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(strTime)){
            Toast.makeText(getActivity(), "请选择'时间'", Toast.LENGTH_SHORT).show();
        }

        TableEx tableEx = new TableEx(getActivity());


        ContentValues values = new ContentValues();
        values.put(Constant.TABLE_BARCODE_STR_TableNumber, strTableNumber);						// 字段  ： 值
        values.put(Constant.TABLE_BARCODE_STR_OldTitleNumber, strOldTitleNumber);
        values.put(Constant.TABLE_BARCODE_STR_NewTitleNumber, strNewTitleNumber);
        values.put(Constant.TABLE_BARCODE_STR_time, strTime);

        long count = tableEx.Add(Constant.TABLE_BARCODE,values);

        Log.i("shen", "count:" + count);
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
    public void onResume() {								// 生命周期 -- 重新开始，继续
        // TODO Auto-generated method stub
        super.onResume();

        if (mReader != null) {
            new InitTask().execute();						// 设备上电 -- 异步类
        }
    }

    @Override
    public void onPause() {								    // 生命周期 -- 停止
        // TODO Auto-generated method stub
        super.onPause();

        if (mReader != null) {
            mReader.close();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_table_number:             // 表号 -- 扫描
                doDecode(SCANTABLE);
            break;

            case R.id.btn_old_title_number:         // 原铅号 -- 扫描
                doDecode(SCANOLDTITLE);
            break;

            case R.id.btn_new_title_number:         // 新铅号 -- 扫描
                doDecode(SCANNEWTITLE);
            break;

            case R.id.tv_time:                      // 选择日期
                getTime();
                break;
            case R.id.btn_save:                     // 保存按钮
                save();
                break;
        }
    }
}
