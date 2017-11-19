package com.pafoid.utils.fragments;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pafoid.utils.R;
import com.pafoid.utils.adapters.OnBoardPagerAdapter;
import com.pafoid.utils.permissions.PermissionManager;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.utils.DeviceUtils;

public class OnBoardFragment extends Fragment implements View.OnClickListener {
    public final String TAG = "OnBoardFragment";

    private View rootView;

    private ImageView image;
    private TextView text;
    private Button button;

    private int type;

    private PermissionManager.PermissionRequestListener listener;

    public static OnBoardFragment newInstance(int type) {
        OnBoardFragment fragment = new OnBoardFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.TYPE, type);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_on_board, container, false);

        type = getArguments().getInt(AppConstants.TYPE);

        image = (ImageView) rootView.findViewById(R.id.image);
        image.setImageResource(getImageRes());

        text = (TextView) rootView.findViewById(R.id.text);
        text.setText(getText());

        button = (Button) rootView.findViewById(R.id.button);
        button.setOnClickListener(this);

        if(type == OnBoardPagerAdapter.INTRO){
            button.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    private int getImageRes(){
        int res = 0;

        switch (type){//TODO: replace assets with -large
            case OnBoardPagerAdapter.INTRO:
                res = (DeviceUtils.isScreenLarge(getContext().getResources())) ?  R.drawable.welcome_tablet : R.drawable.welcome_phone;
                break;
            case OnBoardPagerAdapter.GPS:
                res = (DeviceUtils.isScreenLarge(getContext().getResources())) ?  R.drawable.localisation_tablet : R.drawable.localisation_phone;
                break;
            case OnBoardPagerAdapter.STORAGE:
                res = (DeviceUtils.isScreenLarge(getContext().getResources())) ?  R.drawable.storage_tablet : R.drawable.storage_phone;
                break;
        }

        return res;
    }

    public CharSequence getText() {
        CharSequence text = "";

        switch (type){
            case OnBoardPagerAdapter.INTRO:
                String appName = getString(R.string.app_name);

                String msg = getString(R.string.welcome, getString(R.string.app_name));
                SpannableStringBuilder sb = new SpannableStringBuilder(msg);

                final ForegroundColorSpan fcs = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                sb.setSpan(fcs, msg.indexOf(appName), msg.indexOf(appName) + appName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                sb.setSpan(bss, msg.indexOf(appName), msg.indexOf(appName) + appName.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                text = sb;
                break;
            case OnBoardPagerAdapter.GPS:
                text = getString(R.string.on_board_gps);
                break;
            case OnBoardPagerAdapter.STORAGE:
                text = getString(R.string.on_board_storage);
                break;
        }

        return text;
    }

    @Override
    public void onClick(View v) {
        switch(type){
            case OnBoardPagerAdapter.GPS:
                PermissionManager.getInstance(getContext()).requestPermission(OnBoardFragment.this, Manifest.permission.ACCESS_FINE_LOCATION);
                break;
            case OnBoardPagerAdapter.STORAGE:
                PermissionManager.getInstance(getContext()).requestPermission(OnBoardFragment.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == 0) {
            button.setText(getString(R.string.accepted));

            listener.onPermissionAccepted(permissions[0]);
        }else{
            button.setText(getString(R.string.denied));
            button.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));

            listener.onPermissionDenied(permissions[0]);
        }
    }

    public void setListener(PermissionManager.PermissionRequestListener listener) {
        this.listener = listener;
    }
}
