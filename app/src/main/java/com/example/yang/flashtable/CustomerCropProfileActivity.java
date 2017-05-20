package com.example.yang.flashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/4/23.
 */

public class CustomerCropProfileActivity extends AppCompatActivity {

    private DialogBuilder dialog_builder;

    private CropImageView cv_avatar;
    private Button bt_submit;
    private ProgressDialog progress_dialog;

    private Handler finish_crop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_crop_profile_activity);

        initView();
        initData();
    }

    private void initView() {
        dialog_builder = new DialogBuilder(this);

        cv_avatar = (CropImageView) findViewById(R.id.customer_profile_cv_avatar);
        bt_submit = (Button) findViewById(R.id.customer_profile_bt_avatar_submit);
    }

    private void initData() {
        Intent intent = getIntent();
        /*
        finish_crop = new FinishCrop() {
            @Override
            public void startUpload(String path) {
                UploadImgur upload = new UploadImgur(readImage(path));
                upload.execute("781ad2fd891649a", path);
            }
        }; */
/*
        finish_crop = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                String path = message.get
                UploadImgur upload = new UploadImgur(readImage(path));
                upload.execute("781ad2fd891649a", path);
            }
        };
*/
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
                        getResources().getString(R.string.customer_profile_pic);
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

                                // finish_crop.startUpload(path);
                                CustomerCropProfileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        UploadImgur upload = new UploadImgur(readImage(path));
                                        upload.execute("781ad2fd891649a", path);
                                    }
                                });
                            }

                            @Override
                            public void onError() {}
                        }
                );

            }
        });

    }
/*
    public interface FinishCrop {
        void startUpload(String path);
    }
*/
    // Read bitmap from file
    private Bitmap readImage(String path) {
        BitmapFactory.Options options;
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        } catch (OutOfMemoryError e) {
            try {
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                return bitmap;
            } catch(Exception i) {
                Log.i("ImageDecodeException", i.toString());
            }
        }
        return null;

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

    private void finishUpload(String path, String pic_url) {
        Intent return_data = new Intent();
        return_data.putExtra("path", path);
        return_data.putExtra("url", pic_url);
        setResult(RESULT_OK, return_data);
        finish();
    }

    public class UploadImgur extends AsyncTask<String, Void, String> {
        private Bitmap bitmap;
        private String path;
        private ProgressDialog new_progress_dialog = new ProgressDialog(CustomerCropProfileActivity.this);

        public UploadImgur(Bitmap bitmap){
            this.bitmap = bitmap;
        }
        @Override
        protected void onPreExecute()
        {
            new_progress_dialog.setMessage("儲存中...");
            new_progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String clientID = params[0];
            path = params[1];
            return getImgurContent(clientID);
        }

        public String getImgurContent(String clientID) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            String result;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://api.imgur.com/3/upload");
            httpPost.setHeader("Authorization", "Client-ID " + clientID);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("image", encoded));
            UrlEncodedFormEntity ent = null;
            try {
                ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            httpPost.setEntity(ent);
            String pic_url;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                Log.e("UploadImgur", responseJSON.toString());
                JSONObject data = responseJSON.getJSONObject("data");
                pic_url = data.getString("id");
            } catch (Exception e) {
                //no internet support
                pic_url = null;
                e.printStackTrace();
            }


            return pic_url;
        }

        @Override
        protected void onPostExecute(String pic_url)
        {
            new_progress_dialog.dismiss();
            finishUpload(path, pic_url);
        }

    }

}
