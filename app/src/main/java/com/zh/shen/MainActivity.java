package com.zh.shen;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.zh.shen.adapter.Adapter;
import com.zh.shen.fragment.AddFragment;
import com.zh.shen.fragment.ChartFragment;
import com.zh.shen.fragment.CreateExcelFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Thread.UncaughtExceptionHandler{

    private ViewPager mViewPager;
    private List<Fragment> mTabContents;
    private Adapter mAdapter;


    private TabLayout mTabLayout;

    private ArrayList<String> mTitleList;

    /** 三大标签中正在显示的Fragment */
    private Fragment fragment;


    private long exitTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //在此调用下面方法，才能捕获到线程中的异常
        Thread.setDefaultUncaughtExceptionHandler(this);


        initView();
        initData();
    }

    private void initView() {

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void initData() {
        mTabContents = new ArrayList<>();
        mTabContents.add(AddFragment.newInstance());
        mTabContents.add(ChartFragment.newInstance(0));
        mTabContents.add(CreateExcelFragment.newInstance());

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        mTitleList = new ArrayList<>();
        mTitleList.add("扫描");
        mTitleList.add("查询");
        mTitleList.add("生成");

        mAdapter = new Adapter(getSupportFragmentManager(), mTabContents, mTitleList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);

        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()- exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //在此处理异常， arg1即为捕获到的异常
        Log.i("shen","+++++++++++++++++++++++++++++++++++++++++++++  Exception  ++++++++++++++++++++++++++++++++++++++++++++++++");
        Log.i("shen","AAA:   " + ex);
        Log.i("shen","+++++++++++++++++++++++++++++++++++++++++++++  Exception  ++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
