package net.skjr.wtq.ui.activity.system;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import net.skjr.wtq.R;

import java.util.ArrayList;

/**
 * 引导界面
 */
public class GuideActivity extends AppCompatActivity {

    ViewPager viewPager;

    //定义各个界面View对象
    private View view1, view2, view3;
    private ImageView lastImageView;
    private Button skip;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        findViews();
        initViews();
    }

    private void findViews() {

        viewPager = (ViewPager) findViewById(R.id.guide_viewpager);

        //实例化各个界面的布局对象
        LayoutInflater inflater = LayoutInflater.from(this);
        view1 = inflater.inflate(R.layout.guide_view1, null);
        view2 = inflater.inflate(R.layout.guide_view2, null);
        view3 = inflater.inflate(R.layout.guide_view3, null);
        //view4 = inflater.inflate(R.layout.guide_view4, null);

        lastImageView = (ImageView) view3.findViewById(R.id.guide3_image);

    }

    private void initViews() {
        //将要分页显示的View装入数组中
        views.add(view1);
        views.add(view2);
        views.add(view3);
        //views.add(view4);

        MyViewPagerAdapter adapter = new MyViewPagerAdapter(views);
        viewPager.setAdapter(adapter);
        // 设置监听
        //viewPager.addOnPageChangeListener(new MyOnPageChangeListener());

        lastImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    /**
     * 进入主界面
     */
    private void startMainActivity() {
        startActivity(new Intent(GuideActivity.this, MainActivity.class));
        finish();
    }

    /**
     * 跳过
     */

    public void onSkipClick(View view) {
        startMainActivity();
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //图片已带点,这里不再进行切换
            //switchPoint(position);
        }

        @Override
        public void onPageScrollStateChanged(int scrollState) {

        }
    }

    class MyViewPagerAdapter extends PagerAdapter {

        //界面列表
        private ArrayList<View> views;

        public MyViewPagerAdapter(ArrayList<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            if (views != null) {
                return views.size();
            }

            return 0;
        }

        //初始化position位置的界面
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position), 0);

            return views.get(position);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
