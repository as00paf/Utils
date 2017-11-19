package com.pafoid.utils.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pafoid.utils.R;
import com.pafoid.utils.adapters.ImageGalleryPagerAdapter;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.views.GestureImageView;
import com.pafoid.utils.views.GestureImageViewBase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageGalleryFragment extends Fragment {

    public final String TAG = "ImageGalleryFragment";

    //Views
    protected View rootView;
    protected GestureImageView imageView;

    protected ImageItem image;

    protected ImageGalleryPagerAdapter.Listener listener;

    public static ImageGalleryFragment newInstance(IImageItem image) {
        ImageGalleryFragment imageGalleryFragment = new ImageGalleryFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.IMAGE, image);
        imageGalleryFragment.setArguments(bundle);

        return imageGalleryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        image = bundle.getParcelable(AppConstants.IMAGE);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            return rootView;
        }

        rootView = inflater.inflate(R.layout.layout_fullscreen_image, container, false);
        imageView = (GestureImageView) rootView.findViewById(R.id.layout_fullscreen_image_imageView);
        imageView.setDisplayType(GestureImageViewBase.DisplayType.FIT_TO_SCREEN);

        imageView.setSingleTapListener(
                new GestureImageView.OnImageViewTouchSingleTapListener() {

                    @Override
                    public void onSingleTapConfirmed() {
                        if (listener != null) {
                            listener.toggleUIVisibility();
                        }
                    }
                }
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = image.getTransitionName();
            imageView.setTransitionName(transitionName);
            Log.d(TAG, "Setting gallery image transition name to " + imageView.getTransitionName());
        }

        initImage();

        super.onCreateView(inflater, container, savedInstanceState);

        return rootView;
    }

    private void initImage() {
        Callback callback = new Callback() {
            @Override
            public void onSuccess() {
                listener.onImageLoaded();
                ActivityCompat.startPostponedEnterTransition(getActivity());
            }

            @Override
            public void onError() {
                Log.e(TAG, "Error loading image !");
            }
        };

        //URL
        String imageURL = null;
        int imageRes = 0;
        File imageFile = null;

        //Type
        switch (image.getType()){
            case ImageItem.TYPE_FILE :
                imageFile = new File(image.getImageURL());

                Picasso.with(getContext())
                        .load(imageFile)
                        .error(R.drawable.no_media_image)
                        .into(imageView, callback);
                break;
            case ImageItem.TYPE_RES :
                imageRes = Integer.parseInt(image.getImageURL());
                Picasso.with(getContext())
                        .load(imageRes)
                        .error(R.drawable.no_media_image)
                        .into(imageView, callback);
                break;
            case ImageItem.TYPE_URI :
            case ImageItem.TYPE_URL :
                imageURL = image.getImageURL();
                Picasso.with(getContext())
                        .load(imageURL)
                        .error(R.drawable.no_media_image)
                        .into(imageView, callback);
                break;
            default:
                Log.e(TAG, "Image type is invalid");
        }
    }

    //Setters
    public void setListener(ImageGalleryPagerAdapter.Listener listener) {
        this.listener = listener;
    }

    public static abstract class ImageItem implements Parcelable, IImageItem{

        public static final int TYPE_FILE = 0;
        public static final int TYPE_URL = 1;
        public static final int TYPE_URI = 2;
        public static final int TYPE_RES = 3;

        protected int type = TYPE_URL;

        public abstract String getImageURL();
        public abstract String getTransitionName();
        public abstract String getTitle();
        public abstract String getText();
        public abstract String getReference();

        public int getType(){
            return type;
        }
    }

    public interface IImageItem extends Parcelable {
        String getImageURL();
        String getTransitionName();
        String getTitle();
        String getText();
        String getReference();
        int getType();
    }
}
