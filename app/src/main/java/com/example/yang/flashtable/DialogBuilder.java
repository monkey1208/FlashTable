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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by CS on 2017/3/27.
 */

public class DialogBuilder {

    private Context context;

    private Dialog dialog;
    private TextView tv_content, tv_persons;
    private Button bt_submit, bt_cancel;
    private LinearLayout ll_content, ll_buttons, ll_persons;
    private ImageButton ib_add, ib_minus;
    private DialogEventListener event_listener;
    private View.OnClickListener persons_add, persons_minus;

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
        ll_persons = (LinearLayout) dialog.findViewById(R.id.alert_ll_persons);
        tv_persons = (TextView) dialog.findViewById(R.id.alert_tv_persons);
        ib_add = (ImageButton) dialog.findViewById(R.id.alert_ib_add);
        ib_minus = (ImageButton) dialog.findViewById(R.id.alert_ib_minus);
        bt_submit = (Button) dialog.findViewById(R.id.alert_bt_submit);
        bt_cancel = (Button) dialog.findViewById(R.id.alert_bt_cancel);

        persons_add = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int persons = Integer.parseInt(tv_persons.getText().toString());
                persons += 1;
                tv_persons.setText(Integer.toString(persons));
            }
        };
        persons_minus = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int persons = Integer.parseInt(tv_persons.getText().toString());
                if (persons > 1) persons -= 1;
                tv_persons.setText(Integer.toString(persons));
            }
        };
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
                if (ll_persons.getParent() != null) ll_content.removeView(ll_persons);
                if (bt_cancel.getParent() != null) ll_buttons.removeView(bt_cancel);
                tv_content.setText(_content);
                break;
            case "withCancel":
                if (ll_persons.getParent() != null) ll_content.removeView(ll_persons);
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
                if (ll_persons.getParent() == null) ll_content.addView(ll_persons);
                if (bt_cancel.getParent() == null) ll_buttons.addView(bt_cancel);
                ib_add.setOnClickListener(persons_add);
                ib_minus.setOnClickListener(persons_minus);
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

        }
        dialog.show();
    }

}
