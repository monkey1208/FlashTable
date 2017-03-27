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
    private Button bt_submit;
    private LinearLayout ll_content;

    public DialogBuilder(Context _context) {
        context = _context;
        init();
    }

    private void init() {

        dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.alert_dialog);

        // Window dialog_window = dialog.getWindow();
        // WindowManager.LayoutParams lp = dialog_window.getAttributes();
        // lp.alpha = 0.7f; // Transparency

        tv_content = (TextView) dialog.findViewById(R.id.alert_tv_content);
        ll_content = (LinearLayout) dialog.findViewById(R.id.alert_ll_content);
        bt_submit = (Button) dialog.findViewById(R.id.alert_bt_submit);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    public void dialogEvent(String _content) {
        tv_content.setText(_content);
        dialog.show();
    }

}
