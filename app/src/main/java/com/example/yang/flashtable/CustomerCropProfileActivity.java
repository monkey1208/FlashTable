package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.net.URI;

/**
 * Created by CS on 2017/4/23.
 */

public class CustomerCropProfileActivity extends AppCompatActivity {

    private CropImageView cv_avatar;
    private Button bt_submit;
    private ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_crop_profile_activity);

        initView();
        initData();
    }

    private void initView() {
        cv_avatar = (CropImageView) findViewById(R.id.customer_profile_cv_avatar);
        bt_submit = (Button) findViewById(R.id.customer_profile_bt_avatar_submit);
    }

    private void initData() {
        Intent intent = getIntent();

        // Get path of image from intent and set image to be cropped
        String path = intent.getStringExtra("avatar");
        cv_avatar.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE); // Indicator is circle; but image is saved as square
        cv_avatar.startLoad(
                Uri.fromFile(new File(path)),
                new LoadCallback() {
                    @Override
                    public void onSuccess() {}

                    @Override
                    public void onError() {}
                });

        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Finish crop and return intent with file path
                final String path = Environment.getExternalStorageDirectory().getPath() +
                        "/flashCropped.png";
                cv_avatar.startCrop(
                        Uri.fromFile(new File(path)),
                        new CropCallback() {
                            @Override
                            public void onSuccess(Bitmap cropped) {
                                progress_dialog = new ProgressDialog(
                                        CustomerCropProfileActivity.this);
                                progress_dialog.setMessage("儲存中...");
                                progress_dialog.show();
                            }

                            @Override
                            public void onError() {}
                        },
                        new SaveCallback() {
                            @Override
                            public void onSuccess(Uri outputUri) {
                                progress_dialog.dismiss();
                                Intent return_data = new Intent();
                                return_data.putExtra("path", path);
                                setResult(RESULT_OK, return_data);
                                finish();
                            }

                            @Override
                            public void onError() {}
                        }
                );

            }
        });
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

}
