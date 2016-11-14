package net.skjr.wtq.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import net.skjr.wtq.R;


/**
 * 账户
 */
public class TabChatFragment extends BaseFragment {
    private View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_tab_chat, null);
        } else {
            ViewParent oldParent = rootView.getParent();
            if (oldParent != null) {
                if (oldParent != container) {
                    ((ViewGroup) oldParent).removeView(rootView);
                }
            }
        }
        return rootView;
    }


}
