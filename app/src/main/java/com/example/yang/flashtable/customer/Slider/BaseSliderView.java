package com.example.yang.flashtable.customer.slider;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.yang.flashtable.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

/**
 * Created by Yang on 2017/5/28.
 */

public abstract class BaseSliderView extends com.daimajia.slider.library.SliderTypes.BaseSliderView{

    protected Context mContext;

    private Bundle mBundle;

    /**

     Error place holder image.
     */
    private int mErrorPlaceHolderRes;

    /**

     Empty imageView placeholder.
     */
    private int mEmptyPlaceHolderRes;

    private String mUrl;
    private File mFile;
    private int mRes;
    private Bitmap mBitmap;

    protected OnSliderClickListener mOnSliderClickListener;

    private boolean mErrorDisappear;

    private ImageLoadListener mLoadListener;

    private String mDescription;

    /**

     Scale type of the image.
     */
    private ScaleType mScaleType = ScaleType.Fit;

    public enum ScaleType{
        CenterCrop, CenterInside, Fit, FitCenterCrop
    }

    public BaseSliderView(Context context) {
        super(context);
        mContext = context;
        this.mBundle = new Bundle();
    }

    /**

     the placeholder image when loading image from url or file.
     @Param resId Image resource id
     @return
     */
    public BaseSliderView empty(int resId){
        mEmptyPlaceHolderRes = resId;
        return this;
    }

    /**

     determine whether remove the image which failed to download or load from file
     @Param disappear
     @return
     */
    public BaseSliderView errorDisappear(boolean disappear){
        mErrorDisappear = disappear;
        return this;
    }

    /**

     if you set errorDisappear false, this will set a error placeholder image.
     @Param resId image resource id
     @return
     */
    public BaseSliderView error(int resId){
        mErrorPlaceHolderRes = resId;
        return this;
    }

    /**

     the description of a slider image.
     @Param description
     @return
     */
    public BaseSliderView description(String description){
        mDescription = description;
        return this;
    }

    /**

     set a url as a image that preparing to load
     @Param url
     @return
     */
    public BaseSliderView image(String url){
        if(mFile != null || mRes != 0 || mBitmap != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mUrl = url;
        return this;
    }

    /**

     set a file as a image that will to load
     @Param file
     @return
     */
    public BaseSliderView image(File file){
        if(mUrl != null || mRes != 0 || mBitmap != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mFile = file;
        return this;
    }

    public BaseSliderView image(int res){
        if(mUrl != null || mFile != null || mBitmap != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mRes = res;
        return this;
    }

    public BaseSliderView image(Bitmap bitmap){
        if(mUrl != null || mFile != null || mBitmap != null){
            throw new IllegalStateException("Call multi image function," +
                    "you only have permission to call it once");
        }
        mBitmap = bitmap;
        return this;
    }

    public String getUrl(){
        return mUrl;
    }

    public boolean isErrorDisappear(){
        return mErrorDisappear;
    }

    public int getEmpty(){
        return mEmptyPlaceHolderRes;
    }

    public int getError(){
        return mErrorPlaceHolderRes;
    }

    public String getDescription(){
        return mDescription;
    }

    public Context getContext(){
        return mContext;
    }

    /**

     set a slider image click listener
     @Param l
     @return
     */
    public BaseSliderView setOnSliderClickListener(OnSliderClickListener l){
        mOnSliderClickListener = l;
        return this;
    }

    /**

     When you want to implement your own slider view, please call this method in the end in getView() method

     @Param v the whole view

     @Param targetImageView where to place image
     */
    @Override
    protected void bindEventAndShow(final View v, ImageView targetImageView){
        final BaseSliderView me = this;

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnSliderClickListener != null){
                    mOnSliderClickListener.onSliderClick(me);
                }
            }
        });

        //mLoadListener.onStart(me);

        Picasso p = Picasso.with(mContext);
        RequestCreator rq = null;
        if(mUrl!=null){
            rq = p.load(mUrl);
        }else if(mFile != null){
            rq = p.load(mFile);
        }else if(mBitmap != null){
            targetImageView.setImageBitmap(mBitmap);
            if(v.findViewById(R.id.loading_bar) != null){
                v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
            }
        }else if(mRes != 0){
            rq = p.load(mRes);
        }else{
            return;
        }

        if(rq == null){
            return;
        }

        if(getEmpty() != 0){
            rq.placeholder(getEmpty());
        }

        if(getError() != 0){
            rq.error(getError());
        }

        switch (mScaleType){
            case Fit:
                rq.fit();
                break;
            case CenterCrop:
                rq.fit().centerCrop();
                break;
            case CenterInside:
                rq.fit().centerInside();
                break;
        }

        rq.into(targetImageView,new Callback() {
            @Override
            public void onSuccess() {
                if(v.findViewById(R.id.loading_bar) != null){
                    v.findViewById(R.id.loading_bar).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError() {
                if(mLoadListener != null){
                    mLoadListener.onEnd(false,me);
                }
            }

        });
    }

    public BaseSliderView setScaleType(ScaleType type){
        mScaleType = type;
        return this;
    }

    /**

     the extended class have to implement getView(), which is called by the adapter,
     every extended class response to render their own view.
     @return
     */
    public abstract View getView();

    /**

     set a listener to get a message , if load error.
     @Param l
     */
    public void setOnImageLoadListener(ImageLoadListener l){
        mLoadListener = l;
    }

    public interface OnSliderClickListener {
        public void onSliderClick(BaseSliderView slider);
    }

    /**

     when you have some extra information, please put it in this bundle.
     @return
     */
    public Bundle getBundle(){
        return mBundle;
    }

    public interface ImageLoadListener{
        public void onStart(BaseSliderView target);
        public void onEnd(boolean result,BaseSliderView target);
    }
}