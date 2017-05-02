package com.zh.shen.fragment;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
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

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.zebra.adc.decoder.Barcode2DWithSoft;
import com.zh.shen.R;
import com.zh.shen.bean.BarcodeBean;
import com.zh.shen.config.Constant;
import com.zh.shen.db.biz.TableEx;
import com.zh.shen.utils.ExcelUtil;
import com.zh.shen.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * 标签 -- 新增
 *
 */
public class CreateExcelFragment extends Fragment implements View.OnClickListener {


    /** 生成excel--按钮 */
    private Button mBtnCreateExcel;

    public static CreateExcelFragment newInstance() {
        CreateExcelFragment f = new CreateExcelFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_excel, container, false);

        initView(view);
        initListener();
        initData();
        return view;
    }


    private void initView(View view) {

        mBtnCreateExcel = (Button) view.findViewById(R.id.btn_create_excel);
    }

    private void initListener() {
        mBtnCreateExcel.setOnClickListener(this);
    }


    private void initData() {

    }


    private void createExcel() {

        new createExcelTask().execute();
    }

    /**
     * 生成Excel -- 异步类
     *
     */
    public class createExcelTask extends AsyncTask<String, Integer, String> {
        /** 进度条对话框 */
        ProgressDialog mypDialog;

        /** 数据库的表的操作类 */
        TableEx mTableEx;
        ArrayList<BarcodeBean> mBarcodeBeanList = new ArrayList<>();

        @Override
        protected void onPreExecute() {								// 执行前
            // TODO Auto-generated method stub
            super.onPreExecute();

            mTableEx = new TableEx(getActivity());

            mypDialog = new ProgressDialog(getActivity());
            mypDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mypDialog.setMessage("正在生成Excel...");
            mypDialog.setCanceledOnTouchOutside(false);
            mypDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {		// 执行中

            mBarcodeBeanList = new ArrayList<>();

            Cursor cursor = mTableEx.Query(Constant.TABLE_BARCODE,null,null,null,null,null,null);

            while (cursor.moveToNext()) {
                BarcodeBean bean = new BarcodeBean();

                bean.setId(cursor.getString(0));
                bean.setTableNumber(cursor.getString(1));
                bean.setOldTitleNumber(cursor.getString(2));
                bean.setNewTitleNumber(cursor.getString(3));
                bean.setTime(cursor.getString(4));

                mBarcodeBeanList.add(bean);
            }

            try {
                ExcelUtil.writeExcel(getActivity(), mBarcodeBeanList, "扫描表号");
            } catch (Exception e) {
               // e.printStackTrace();
                Log.i("shen","e.getMessage():" + e.getMessage());
            }

            return "";
        }

        @Override
        protected void onPostExecute(String str) {				// 执行后
            super.onPostExecute(str);
            mypDialog.cancel();
            Toast.makeText(getActivity(), "已生成Excel文件", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onResume() {								// 生命周期 -- 重新开始，继续
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    public void onPause() {								    // 生命周期 -- 停止
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

            case R.id.btn_create_excel:
                createExcel();
                break;

        }
    }
}
