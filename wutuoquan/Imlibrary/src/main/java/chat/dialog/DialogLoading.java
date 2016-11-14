package chat.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.imlibrary.R;
public class DialogLoading {
    private AlertDialog dialog;
    private Context context;

    public DialogLoading(Context context) {
        this.context = context;
        dialog = new AlertDialog.Builder(context).create();
    }

    public static void notifydialog(Context context, int titleId,
                                    int messageId, OnClickListener arg0, OnClickListener arg1) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titleId);
        dialog.setMessage(messageId);
        dialog.setPositiveButton(android.R.string.ok, arg0);
        dialog.setNegativeButton(android.R.string.cancel, arg1);
        dialog.create().show();
    }

    public void show() {

        if (dialog != null)
            try {
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                View loading = ((Activity) context).getLayoutInflater().inflate(
                        R.layout.view_loading, null);
                dialog.getWindow().setContentView(loading);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void show(String msg) {

        if (dialog != null)
            try {
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                View loading = ((Activity) context).getLayoutInflater().inflate(
                        R.layout.view_loading, null);
                TextView load_dialog = (TextView) loading.findViewById(R.id.load_dialog);
                load_dialog.setText(msg);
                dialog.getWindow().setContentView(loading);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }

    public void giftloadshow() {
        try {
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            View loading = ((Activity) context).getLayoutInflater().inflate(
                    R.layout.view_loading, null);
            TextView tv = (TextView) loading.findViewById(R.id.load_dialog);
            tv.setText(context.getResources().getString(R.string.loading));
            dialog.getWindow().setContentView(loading);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void diss() {
        try {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancel() {
        try {
            if (dialog != null) {
                dialog.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showOnListener(final DialogInterface.OnDismissListener listener, final DialogInterface.OnCancelListener cancelListener) {
        if (dialog != null)
            try {
                dialog.show();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                View loading = ((Activity) context).getLayoutInflater()
                        .inflate(R.layout.view_loading, null);
                dialog.getWindow().setContentView(loading);
                dialog.setOnDismissListener(listener);
                dialog.setOnCancelListener(cancelListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(cancel);
        }
    }
}
