package net.skjr.wtq.chat;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import net.skjr.wtq.R;

import chat.base.BaseFragment;
import chat.base.IMClient;
import chat.contact.activity.AddFriendsActivity;
import chat.contact.activity.ChoseContactsActivity;
import chat.contact.activity.ContactsActivity;
import chat.homespace.HomeSpaceActivity;
import chat.manager.ChatNotifyManager;
import chat.session.activity.AllHistoryFragment;
import chat.session.group.activity.ChatGroupActivity;
import chat.shareholders.ShareholdersActivity;

/**
 * 社交
 */
public class TabChatFragment extends BaseFragment implements View.OnClickListener {
    public static boolean GotoChatFragment = false;
    private ViewGroup menuView;
    private View top_view_chat_title, tab_chat_menu;
    private PopupWindow pop;
    private TextView chat_create_friends, chat_create_talk;
    private AllHistoryFragment allHistoryFragment;
    private RadioButton rb_shareholder_chat_title,rb_contacts_chat_title,rb_group_chat_title,rb_home_chat_title;

    @Override
    protected void onCreateView(Bundle savedInstanceState) {
        super.onCreateView(savedInstanceState);
        setContentView(R.layout.fragment_chat);
        initView(getContentView());
        initFragment();
        initPop();
        initEvent();
    }
    private void initEvent() {
        tab_chat_menu.setOnClickListener(this);
        rb_shareholder_chat_title.setOnClickListener(this);
        rb_contacts_chat_title.setOnClickListener(this);
        rb_group_chat_title.setOnClickListener(this);
        rb_home_chat_title.setOnClickListener(this);
    }
    public void initView(View v) {
        top_view_chat_title = v.findViewById(R.id.top_view_chat_title);
        tab_chat_menu = v.findViewById(R.id.tab_chat_menu);
        rb_shareholder_chat_title= (RadioButton) v.findViewById(R.id.rb_shareholder_chat_title);
        rb_contacts_chat_title= (RadioButton) v.findViewById(R.id.rb_contacts_chat_title);
        rb_group_chat_title= (RadioButton) v.findViewById(R.id.rb_group_chat_title);
        rb_home_chat_title= (RadioButton) v.findViewById(R.id.rb_home_chat_title);
    }
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (GotoChatFragment) {
            GotoChatFragment = false;
            IMClient.isChatPage = true;
            ChatNotifyManager.getInstance().clearNotify();
        }
    }
    public void initFragment() {
        if(allHistoryFragment==null)
           allHistoryFragment = new AllHistoryFragment();
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if(allHistoryFragment.isAdded()){
            ft.show(allHistoryFragment);
        }else{
            ft.add(R.id.im_container,allHistoryFragment);
        }
        ft.commitAllowingStateLoss();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_chat_menu:
                if (pop == null)
                    initPop();
                if (pop.isShowing()) {
                    pop.dismiss();
                    pop.setFocusable(false);
                } else {
                    pop.showAsDropDown(top_view_chat_title, 0, 0);
                }
                break;
            case R.id.chat_create_friends:
                if (pop.isShowing()) {
                    pop.dismiss();
                    pop.setFocusable(false);
                }
                Intent intent_friends = new Intent(getActivity(),
                        AddFriendsActivity.class);
                getActivity().startActivity(intent_friends);
                break;
            case R.id.chat_create_talk:
                if (pop.isShowing()) {
                    pop.dismiss();
                    pop.setFocusable(false);
                }
                Intent intent = new Intent(getActivity(),
                        ChoseContactsActivity.class);
                intent.putExtra(ChoseContactsActivity.ISMULTISELECT,true);
                getActivity().startActivity(intent);
                break;
            case R.id.rb_shareholder_chat_title:
                startActivity(ShareholdersActivity.class);
                break;
            case R.id.rb_contacts_chat_title:
                startActivity(ContactsActivity.class);
                break;
            case R.id.rb_group_chat_title:
                startActivity(ChatGroupActivity.class);
                break;
            case R.id.rb_home_chat_title:
                startActivity(HomeSpaceActivity.class);
                break;
            default:
                break;
        }
    }

    private void initPop() {
        menuView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(
                R.layout.item_chat_fragment_add, null);
        pop = new PopupWindow(menuView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, true);
        menuView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (pop != null && pop.isShowing()) {
                    pop.dismiss();
                    pop.setFocusable(false);
                }
            }
        });
        chat_create_friends = (TextView) menuView
                .findViewById(R.id.chat_create_friends);
        chat_create_friends.setOnClickListener(this);
        chat_create_talk = (TextView) menuView
                .findViewById(R.id.chat_create_talk);
        chat_create_talk.setOnClickListener(this);
        pop.setContentView(menuView);
        pop.setBackgroundDrawable(new ColorDrawable());
        pop.setOutsideTouchable(true);
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setAnimationStyle(android.R.style.Animation_Dialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pop != null) {
            pop.dismiss();
            pop = null;
        }
    }
}
