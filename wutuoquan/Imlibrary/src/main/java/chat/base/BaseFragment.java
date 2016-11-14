package chat.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.lang.reflect.Field;

import chat.common.util.ToolsUtils;
import chat.common.util.storage.PreferencesHelper;

public class BaseFragment extends Fragment {
    /**
     * mSharedPreferences工具
     */
    protected PreferencesHelper mHelper = null;
    /**
     * 屏幕的宽度
     */
    protected int mScreenWidth;
    /**
     * 高度
     */
    protected int mScreenHeight;
    /**
     * 密度
     */
    protected float mDensity;
    /**
     * 逛街商品的图片的宽高(正方形)
     */
    protected int mGoodsImageWidth;
    private LayoutInflater inflater;
    private View contentView;
    private Context context;
    private ViewGroup container;
    /**
     * 逛街商品的图片的容器(正方形)
     */
    private LayoutParams mParaTemp = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        mHelper = new PreferencesHelper(getActivity());
        // 获取屏幕宽高密度
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
        mDensity = metric.density;
        mGoodsImageWidth = (mScreenWidth - ToolsUtils.dip2px(context, 10 * 3)) / 2;
    }

    @Override
    public final View onCreateView(LayoutInflater inflater,
                                   ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        onCreateView(savedInstanceState);
        if (contentView == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        return contentView;
    }

    protected void onCreateView(Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
		/*if (URLConfig.conditionFlag > 1) {
			UmsAgent.onResume(getActivity());
		}*/
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
		/*if (URLConfig.conditionFlag > 1) {
			UmsAgent.onPause(getActivity());
		}*/
    }

    /**
     * onDestriyView 时释放资源
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        IMClient.sImageLoader.clearMemoryCache();
        contentView = null;
        container = null;
        inflater = null;
    }

    public Context getApplicationContext() {
        return context;
    }

    public void setContentView(int layoutResID) {
        setContentView((ViewGroup) inflater.inflate(layoutResID, container,
                false));
    }

    /**
     * @return LayoutParams
     * @Title: setGoodsImageLayout
     * @param:
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    protected LayoutParams getGoodsImageLayout(LayoutParams mLayoutParams) {
        if (null != mParaTemp) {
            return mParaTemp;
        }
        mParaTemp = mLayoutParams;
        mParaTemp.height = mGoodsImageWidth;
        mParaTemp.width = mGoodsImageWidth;
        return mParaTemp;
    }

    public View getContentView() {
        return contentView;
    }

    public void setContentView(View view) {
        contentView = view;
    }

    public View findViewById(int id) {
        if (contentView != null)
            return contentView.findViewById(id);
        return null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void startActivity(Class<?> className) {
        Intent intent = new Intent(context, className);
        startActivity(intent);
    }
}
