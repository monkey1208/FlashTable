package com.example.yang.flashtable;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by CS on 2017/3/27.
 */

public class DialogBuilder {

    private Context context;

    private Dialog dialog;
    private TextView tv_content;
    private Button bt_submit, bt_cancel;
    private LinearLayout ll_content, ll_buttons;
    private  DialogEventListener event_listener;

    public DialogBuilder(Context _context) {
        context = _context;
        init();
    }

    private void init() {

        dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.alert_dialog);

        tv_content = (TextView) dialog.findViewById(R.id.alert_tv_content);
        ll_content = (LinearLayout) dialog.findViewById(R.id.alert_ll_content);
        ll_buttons = (LinearLayout) dialog.findViewById(R.id.alert_ll_buttons);
        bt_submit = (Button) dialog.findViewById(R.id.alert_bt_submit);
        bt_cancel = (Button) dialog.findViewById(R.id.alert_bt_cancel);
    }

    public void dialogEvent(String _content, String _mode, final DialogEventListener event_listener) {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event_listener != null)
                    event_listener.clickEvent(true, 0);
                dialog.hide();
            }
        });

        switch (_mode) {
            case "normal":
                if (bt_cancel.getParent() != null) ll_buttons.removeView(bt_cancel);
                tv_content.setText(_content);
                break;
            case "withCancel":
                if (bt_cancel.getParent() == null) ll_buttons.addView(bt_cancel);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (event_listener != null)
                            event_listener.clickEvent(false, 0);
                        dialog.hide();
                    }
                });
                tv_content.setText(_content);
                break;
            case "personsPicker":
                break;

        }
        dialog.show();
    }

}
