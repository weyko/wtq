package net.skjr.wtq.ui.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.skjr.wtq.R;
import net.skjr.wtq.common.Consts;

/**
 * 创建者     huangbo
 * 创建时间   2016/9/30 15:27
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class AddMinusView extends RelativeLayout implements View.OnClickListener {

    private ImageView mView_minus;
    private ImageView mView_add;
    public EditText mView_edit;

    public AddMinusView(Context context) {
        super(context);
    }

    public AddMinusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_add_minus, this);
        initView(view);
    }


    public AddMinusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(View view) {
        mView_minus = (ImageView) view.findViewById(R.id.view_minus);
        mView_add = (ImageView) view.findViewById(R.id.view_add);
        mView_edit = (EditText) view.findViewById(R.id.view_edit);

        mView_add.setOnClickListener(this);
        mView_minus.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_minus:
                minus();
                break;
            case R.id.view_add:
                add();
                break;
        }
    }

    private void add() {
        String edit = mView_edit.getText().toString().trim();
        if(edit.length() == 0) {
            mView_edit.setText("1");
            return;
        }
        int num = Integer.parseInt(edit);
        if(num == 0) {
            mView_edit.setText("1");
            return;
        }
        if(num >= Consts.MAX_BUY) {
            mView_edit.setText(Consts.MAX_BUY+"");
            return;
        }
        num++;
        mView_edit.setText(num+"");
    }

    private void minus() {
        String edit = mView_edit.getText().toString().trim();
        if(edit.length() == 0) {
            return;
        }
        int num = Integer.parseInt(edit);
        if(num == 1) {
            mView_edit.setText("1");
        } else if(num == 0) {
            mView_edit.setText("1");
        } else {
            if(num > Consts.MAX_BUY) {
                mView_edit.setText(Consts.MAX_BUY+"");
                return;
            }
            num--;
            mView_edit.setText(num+"");
        }
    }

}
