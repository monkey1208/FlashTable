package com.example.yang.flashtable;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CS on 2017/3/23.
 * This class uses the library in repository SimpleCropView
 */

public class CustomerProfileFragment extends Fragment {
    SharedPreferences user;
    String userID, username;

    DialogBuilder dialog_builder;

    private View view;
    TextView tv_username, tv_credit, tv_edit, tv_exchange_gifts_content, tv_points;
    LinearLayout ll_comments, ll_reservations, ll_points_record, ll_contact_us;
    ImageView iv_avatar;
    Button bt_about_credits;

    private String credits;

    private int GET_IMAGE_GALLERY = 0, GET_IMAGE_CAMERA = 1, CROP_IMAGE = 2;
    private String picture_path;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_profile_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        dialog_builder = new DialogBuilder(getActivity());

        tv_username = (TextView) view.findViewById(R.id.customer_profile_tv_name);
        tv_credit = (TextView) view.findViewById(R.id.customer_profile_tv_credit);
        ll_reservations = (LinearLayout) view.findViewById(R.id.customer_profile_ll_reservations);
        ll_comments = (LinearLayout)  view.findViewById(R.id.customer_profile_ll_comments);
        ll_points_record = (LinearLayout) view.findViewById(R.id.customer_profile_ll_points_record);
        ll_contact_us = (LinearLayout) view.findViewById(R.id.customer_profile_ll_contact_us);
        iv_avatar = (ImageView) view.findViewById(R.id.customer_profile_iv_avatar);
        tv_edit = (TextView) view.findViewById(R.id.customer_profile_bt_edit);
        bt_about_credits = (Button) view.findViewById(R.id.customer_profile_bt_about_credit);
        tv_points = (TextView) view.findViewById(R.id.customer_profile_tv_points);

        credits = getResources().getString(R.string.customer_profile_credit);

        tv_exchange_gifts_content = (TextView) view.findViewById(R.id.customer_profile_tv_exchange_info);
    }

    private void initData() {
        getUserInfo();

        tv_username.setText(username);
        tv_credit.setText(credits + "-");
        new CustomerAPIProfile().execute(userID);

        ll_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerDetailActivity.class);
                startActivity(intent);
            }
        });
        ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerCommentActivity.class);
                intent.putExtra("type", "user");
                startActivity(intent);
            }
        });
        ll_points_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerCouponRecordActivity.class);
                startActivity(intent);
            }
        });
        ll_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CustomerContactUsActivity.class));
            }
        });
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_builder.dialogEvent("", "imagePicker",
                        new DialogEventListener() {
                            @Override
                            public void clickEvent(boolean ok, int status) {
                                if (ok) {
                                    if (status == 1) {
                                        Intent intent = new Intent(
                                                Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(intent, GET_IMAGE_GALLERY);
                                    } else {
                                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                                        String imageFileName = timeStamp + ".jpg";
                                        File storageDir = Environment.getExternalStoragePublicDirectory(
                                                Environment.DIRECTORY_PICTURES);
                                        picture_path = storageDir.getAbsolutePath() + "/" + imageFileName;
                                        File file = new File(picture_path);
                                        Uri outputFileUri = Uri.fromFile(file);
                                        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                                        startActivityForResult(intent, GET_IMAGE_CAMERA);
                                    }
                                }
                            }
                        });

            }
        });

        // String path = Environment.getExternalStorageDirectory().getPath() +
        //         getResources().getString(R.string.customer_profile_pic);
        // Bitmap avatar = readImage(path);
        // if (avatar == null) iv_avatar.setImageBitmap(getRoundedShape(((BitmapDrawable) iv_avatar.getDrawable()).getBitmap()));
        // else    iv_avatar.setImageBitmap(getRoundedShape(avatar));
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(), CustomerNameActivity.class);
                startActivity(intent);
            }
        });
        bt_about_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(), CustomerCreditsActivity.class);
                startActivity(intent);
            }
        });

        tv_exchange_gifts_content.setText(Html.fromHtml("<font color=\"#FFFFFF\">每次預約用餐每人可得</font> " +
                "<font color=\"#FFD800\"><big><big><big>5</big></big></big></font> " +
                "<font color=\"#FFFFFF\">FLASH Points<br>現在開始累積你的FLASH Points<br>各種專屬回饋好禮在兌換區等你喔！</font>"));
    }

    // Functions related to getting image

    // Receive results from other activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_IMAGE_GALLERY
                && resultCode == Activity.RESULT_OK) {
            // Get image from gallery
            String path = getPathFromGalleryData(data, this.getActivity());
            if (path != null) {
                // Start cropping intent
                Intent intent = new Intent(getActivity(), CustomerCropProfileActivity.class);
                intent.putExtra("avatar", path);
                startActivityForResult(intent, CROP_IMAGE);
            }
        }
        else if (requestCode == GET_IMAGE_CAMERA
                && resultCode == Activity.RESULT_OK) {
            // Get image from camera
            File imgFile = new  File(picture_path);
            if(imgFile.exists()) {
                String path = imgFile.getAbsolutePath();
                Intent intent = new Intent(getActivity(), CustomerCropProfileActivity.class);
                intent.putExtra("avatar", path);
                startActivityForResult(intent, CROP_IMAGE);
            }

        }
        else if (requestCode == CROP_IMAGE
                && resultCode == Activity.RESULT_OK) {
            // Get image from crop; finish profile pic change
            String path = data.getStringExtra("path");

            Bitmap avatar = readImage(path);
            if (avatar == null) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
                Log.i("CroppedImagePath", path);
            }
            else iv_avatar.setImageBitmap(getRoundedShape(avatar));

            String pic_url = data.getStringExtra("url");
            pic_url = "http://i.imgur.com/" + pic_url + ".png";
            new APIAvatar().execute(userID, pic_url);
        }
    }

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

    // Trim rounded shape from image
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    // Get image path from gallery
    private static String getPathFromGalleryData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    // Get user info

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    // APIs

    // API for getting profile info
    class CustomerAPIProfile extends AsyncTask<String, Void, String> {
        private String status = null;
        private Bitmap avatar = null;
        private String flash_points = null;

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            String pic_url = null;
            System.out.println("APIProfile");
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        getString(R.string.server_domain)+"api/user_info?user_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                System.out.println(httpResponse);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    content = responseJSON.getString("point");
                    pic_url = responseJSON.getString("picture_url");
                    flash_points = responseJSON.getString("flash_point");
                }

                // TODO: Get image from local storage if available.
                if (pic_url != null && !pic_url.equals("")) avatar = getRoundedShape(BitmapFactory.decodeStream((InputStream)new URL(pic_url).getContent()));
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return content;
        }
        @Override
        protected void onPostExecute(String _content) {

            if(status == null || status.equals("-1") || _content == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                tv_credit.setText(credits + _content);
                if (avatar != null) iv_avatar.setImageBitmap(avatar);
                tv_points.setText(flash_points);
            }
        }
    }

    // API to change profile pic
    class APIAvatar extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerProfileFragment.this.getActivity());
        private String status = null, new_username;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            new_username = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        getString(R.string.server_domain) + "api/modify_user");
                StringEntity se = new StringEntity("{ \"user_id\":\"" + params[0] +
                        "\", \"new_picture_url\":\"" + params[1] + "\"}", HTTP.UTF_8);
                request.addHeader("Content-Type", "application/json");
                request.setEntity(se);
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
            } catch (Exception e) {
                status = null;
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            progress_dialog.dismiss();
            if( status.equals("-2") )    dialog_builder.dialogEvent(getResources().getString(R.string.customer_profile_name_used), "normal", null);
            else if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);

        }
    }
}
