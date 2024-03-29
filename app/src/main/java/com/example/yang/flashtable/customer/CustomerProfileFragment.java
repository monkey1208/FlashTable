package com.example.yang.flashtable.customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.DialogEventListener;
import com.example.yang.flashtable.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    TextView tv_username, tv_credit, tv_edit, tv_exchange_gifts_content, tv_points, tv_go;
    LinearLayout ll_comments, ll_reservations, ll_points_record, ll_contact_us, ll_about_credits;
    ImageView iv_avatar;

    private String credits;

    private int GET_IMAGE_GALLERY = 0, GET_IMAGE_CAMERA = 1, CROP_IMAGE = 2, REQUEST_CAMERA = 3, REQUEST_MEDIA = 4;
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
        ll_about_credits = (LinearLayout) view.findViewById(R.id.customer_profile_ll_about_credit);
        tv_points = (TextView) view.findViewById(R.id.customer_profile_tv_points);
        tv_go = (TextView) view.findViewById(R.id.customer_profile_tv_go_exchange);

        credits = getResources().getString(R.string.customer_profile_credit);

        tv_exchange_gifts_content = (TextView) view.findViewById(R.id.customer_profile_tv_exchange_info);
    }

    private void initData() {
        getUserInfo();

        tv_username.setText(username);
        tv_credit.setText(credits + "-");

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
                                        // Get image from gallery
                                        Intent intent = new Intent(
                                                Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(intent, GET_IMAGE_GALLERY);
                                    } else {
                                        // Get image from camera
                                        // NOTE: Android M crash; needs to get permission first

                                        // boolean camPermission = hasPermissionInManifest(getActivity(), android.Manifest.permission.CAMERA);
                                        // Toast.makeText(getActivity(), String.valueOf(camPermission), Toast.LENGTH_LONG).show();

                                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                                // Should we show an explanation?
                                                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                                    //This is called if user has denied the permission before
                                                    //In this case I am just asking the permission again
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);

                                                } else {
                                                    requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                                                }
                                            }
                                            else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA);
                                            // Otherwise has all permissions; start camera

                                            else startCamera();
                                        }
                                        else startCamera();
                                    }
                                }
                            }
                        });

            }
        });

        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(), CustomerNameActivity.class);
                startActivity(intent);
            }
        });
        ll_about_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getBaseContext(), CustomerCreditsActivity.class);
                startActivity(intent);
            }
        });

        tv_exchange_gifts_content.setText(Html.fromHtml("<font color=\"#FFFFFF\">每成功預約１人用餐每人可得</font> " +
                "<font color=\"#FFD800\"><big><big><big>-</big></big></big></font> " +
                "<font color=\"#FFFFFF\">FLASH Points！<br>現在開始累積你的FLASH Points，<br>各種專屬回饋好禮在兌換區等你喔！</font>"));

        tv_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((CustomerMainActivity)getActivity()).navigate("points");
            }
        });

        new CustomerAPIProfile().execute(userID);
    }

    private void setPointRatio(int points_ratio) {
        tv_exchange_gifts_content.setText(Html.fromHtml("<font color=\"#FFFFFF\">每成功預約１人用餐每人可得</font> " +
                "<font color=\"#FFD800\"><big><big><big>" + String.valueOf(points_ratio) + "</big></big></big></font> " +
                "<font color=\"#FFFFFF\">FLASH Points！<br>現在開始累積你的FLASH Points，<br>各種專屬回饋好禮在兌換區等你喔！</font>"));

    }

    private void startCamera() {
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

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_MEDIA);
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Toast.makeText(getActivity(), "無法取得相機權限！", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == REQUEST_MEDIA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                startCamera();
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
                Toast.makeText(getActivity(), "無法取得影像權限！", Toast.LENGTH_LONG).show();
            }

        }
        else super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        private int ratio = -1;

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
                if (pic_url != null && !pic_url.equals("")) {
                    /*
                    ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                    imageLoader.loadImage(pic_url, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            // Do whatever you want with Bitmap
                            avatar = getRoundedShape(loadedImage);
                            iv_avatar.setImageBitmap(avatar);
                        }
                    });
                    // Bitmap bmp = imageLoader.loadImageSync(pic_url);
                    */
                    avatar = BitmapFactory.decodeStream((InputStream)new URL(pic_url).getContent());

                }

                HttpGet requestRatio = new HttpGet(
                        getString(R.string.server_domain)+"api/flash_rate");
                requestRatio.addHeader("Content-Type", "application/json");
                HttpResponse responseRatio = httpClient.execute(requestRatio);
                ResponseHandler<String> handlerRatio = new BasicResponseHandler();
                String httpResponseRatio = handlerRatio.handleResponse(responseRatio);
                JSONObject responseJSONRatio = new JSONObject(httpResponseRatio);
                ratio = Integer.parseInt(responseJSONRatio.getString("rate"));

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
                if (avatar != null) iv_avatar.setImageBitmap(getRoundedShape(avatar));
                tv_points.setText(flash_points);
                setPointRatio(ratio);
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
