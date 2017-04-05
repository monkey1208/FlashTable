package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by CS on 2017/4/5.
 */

public class StoreRegisterActivity extends AppCompatActivity {
    DialogBuilder dialog_builder;

    ImageButton ib_info_back;
    EditText et_username, et_password, et_password_again,
            et_cellphone_2, et_cellphone_3, et_cellphone_4;
    Button bt_submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.store_register_activity);

        initView();
        initData();
    }

    private void initView() {
        dialog_builder = new DialogBuilder(this);

    }

    private void initData() {

    }
}
