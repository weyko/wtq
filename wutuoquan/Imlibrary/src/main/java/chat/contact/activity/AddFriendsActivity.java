package chat.contact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.imlibrary.R;

import chat.base.BaseActivity;
import chat.qrcode.QRCodeActivity;
/**
 * Description: 添加好友
 */
public class AddFriendsActivity extends BaseActivity {
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
    }

    @Override
    protected void initView() {
        titleText= (TextView) this.findViewById(R.id.titleText);
    }

    @Override
    protected void initEvents() {
        this.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFriendsActivity.this.finish();
            }
        });
    }
    @Override
    protected void initData() {
        titleText.setText(R.string.chat_title_add_friend);
    }
    public void  onClick(View view){
        String tag= (String) view.getTag();
        if("qrcode".equals(tag)){
            startActivity(new Intent(this, QRCodeActivity.class));
        }else{
            Intent intent = new Intent(this, ContactsInvateActivity.class);
            startActivity(intent);
        }
    }
}
