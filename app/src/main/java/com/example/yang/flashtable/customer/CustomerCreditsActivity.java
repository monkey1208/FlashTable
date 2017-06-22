package com.example.yang.flashtable.customer;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.yang.flashtable.R;

/**
 * Created by CS on 2017/5/1.
 */

public class CustomerCreditsActivity extends AppCompatActivity {

    TextView tv_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_credit_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();
        tv_about = (TextView) findViewById(R.id.customer_credit_tv_about);
    }

    private void initData() {
        tv_about.setText(Html.fromHtml(
                "一、 待店家確定預約訂位無誤並完成確認後，將由系統自動產生QR code至您所屬之帳戶，使用者得在FLASH TABLE App使用端中檢視所成功預約訂位的餐廳資訊，並前往至店家領位。每次預約訂位成功將獲得信譽分數<font color=\"#FF0000\">5分</font>，信譽分數100分為滿分，最多不超過100分。<br><br>" +
                "二、 若在使用者在店家規定的時間內到達店家領位，發現此時店家並無預留空位給使用者，此時使欲者可以選擇取消預約，店家的預約成功率將下降。該預約訂位的餐廳資訊將立即無效。<br><br>" +
                "三、若在使用者在店家規定的時間內未到達店家領位，店家有權隨時取消該使用者的預約，同時使用者的信譽分數將被扣除20分。<font color=\"#FF0000\">使用者信譽分數為0時，將暫停該使用者使用FLASH TABLE App預約訂位服務30天。</font><br><br>" +
                "四、店家可以在已設定的<font color=\"#FF0000\">『15』分鐘</font>之後，若使用者未到達領位，將有權隨時取消該筆預約訂位。<br><br>" +
                "五、 所有交易若有任何爭議、糾紛，都以本應用程式系統所自動記錄之電子資料為準，故若發現資料有誤，請立即通知本應用程式。<br>"
        ));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        setTitle(getResources().getString(R.string.customer_credit_about));
        Drawable background = getResources().getDrawable(R.drawable.customer_bg_orange_actionbar);
        getSupportActionBar().setBackgroundDrawable(background);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
