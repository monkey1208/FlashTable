package com.example.yang.flashtable;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
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
    private DialogEventListener event_listener;

    // Views in normal mode
    private TextView tv_content, tv_persons;
    private Button bt_submit, bt_cancel;
    private LinearLayout ll_content, ll_buttons, ll_persons;
    private ImageButton ib_add, ib_minus;
    private View.OnClickListener persons_add, persons_minus;

    // Views in image picker mode
    private LinearLayout image_ll_camera, image_ll_gallery;
    private Button image_bt_cancel;

    public DialogBuilder(Context _context) {
        context = _context;
    }

    private void initNormal() {

        dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.customer_alert_dialog);

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
                if (persons < 15) persons += 1;
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
        ib_add.setOnClickListener(persons_add);
        ib_minus.setOnClickListener(persons_minus);

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event_listener != null)
                    event_listener.clickEvent(true, 0);
                dialog.hide();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (event_listener != null)
                    event_listener.clickEvent(false, 0);
                dialog.hide();
            }
        });
    }

    private void initImagePicker() {
        dialog = new Dialog(context, R.style.Dialog);
        dialog.setContentView(R.layout.customer_choose_image_dialog);

        image_ll_camera = (LinearLayout) dialog.findViewById(R.id.alert_image_ll_camera);
        image_ll_gallery = (LinearLayout) dialog.findViewById(R.id.alert_image_ll_gallery);
        image_bt_cancel = (Button) dialog.findViewById(R.id.alert_image_bt_cancel);

        image_bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
    }

    public void dialogEvent(String _content, String _mode, DialogEventListener _event_listener) {
        event_listener = _event_listener;
        if (_mode.equals("imagePicker")) {
            initImagePicker();

            // Image options
            image_ll_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (event_listener != null)
                        event_listener.clickEvent(true, 0); // Camera code: 0
                    dialog.hide();
                }
            });

            image_ll_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (event_listener != null)
                        event_listener.clickEvent(true, 1); // Gallery code: 1
                    dialog.hide();
                }
            });
        } else {
            initNormal();
            tv_content.setText(_content);

            switch (_mode) {
                case "normal":
                    if (ll_persons.getParent() != null) ll_content.removeView(ll_persons);
                    if (bt_cancel.getParent() != null) ll_buttons.removeView(bt_cancel);
                    break;
                case "withCancel":
                    if (ll_persons.getParent() != null) ll_content.removeView(ll_persons);
                    if (bt_cancel.getParent() == null) ll_buttons.addView(bt_cancel);
                    break;
                case "personsPicker":
                    if (ll_persons.getParent() == null) ll_content.addView(ll_persons);
                    if (bt_cancel.getParent() == null) ll_buttons.addView(bt_cancel);
                    bt_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (event_listener != null)
                                event_listener.clickEvent(true, Integer.parseInt(tv_persons.getText().toString()));
                            dialog.hide();
                        }
                    });
                    break;
            }
        }
        dialog.show();
    }

}
