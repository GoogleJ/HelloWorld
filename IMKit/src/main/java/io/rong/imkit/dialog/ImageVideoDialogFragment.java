//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.utilities.RongUtils;

public class ImageVideoDialogFragment extends DialogFragment implements OnClickListener {
    private static final String TAG = "ImageVideoDialogFragment";
    protected Dialog mDialog;
    protected View mRootView;
    private boolean hasSight;
    private boolean hasImage;
    private ImageVideoDialogFragment.ImageVideoDialogListener mListener;
    private TextView mSight;
    private TextView mCancel;
    private TextView mAlbum;
    private View mLine;

    public ImageVideoDialogFragment() {
    }

    public void setHasSight(boolean pHasSight) {
        this.hasSight = pHasSight;
    }

    public void setHasImage(boolean pHasImage) {
        this.hasImage = pHasImage;
    }

    public void setImageVideoDialogListener(ImageVideoDialogFragment.ImageVideoDialogListener pListener) {
        this.mListener = pListener;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(1, 16973940);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mRootView = inflater.inflate(layout.rc_dialog_video_image, container, false);
        this.initView();
        return this.mRootView;
    }

    protected void initView() {
        this.mSight = (TextView) this.mRootView.findViewById(id.tv_sight);
        this.mCancel = (TextView) this.mRootView.findViewById(id.tv_cancel);
        this.mLine = this.mRootView.findViewById(id.view_horizontal);
        this.mAlbum = (TextView) this.mRootView.findViewById(id.tv_album);
        if (!this.hasSight) {
            this.mSight.setVisibility(8);
            this.mLine.setVisibility(8);
        }

        if (!this.hasImage) {
            this.mAlbum.setVisibility(8);
            this.mLine.setVisibility(8);
        }

        this.mSight.setOnClickListener(this);
        this.mCancel.setOnClickListener(this);
        this.mAlbum.setOnClickListener(this);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mDialog = this.getDialog();
        if (this.mDialog != null) {
            Window dialogWindow = this.mDialog.getWindow();
            dialogWindow.setBackgroundDrawableResource(17170445);
            DisplayMetrics dm = new DisplayMetrics();
            this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            dialogWindow.setLayout((int) ((float) dm.widthPixels * this.getScreenWidthProportion()), -2);
            LayoutParams attributes = dialogWindow.getAttributes();
            attributes.gravity = this.getGravity();
            attributes.x = -RongUtils.dip2px(this.getHorizontalMovement());
            attributes.y = RongUtils.dip2px(this.getVerticalMovement());
            dialogWindow.setAttributes(attributes);
        }

    }

    protected int getGravity() {
        return 80;
    }

    protected float getScreenWidthProportion() {
        return 1.0F;
    }

    protected float getVerticalMovement() {
        return 0.0F;
    }

    protected float getHorizontalMovement() {
        return 0.0F;
    }

    public void show(FragmentManager manager) {
        if (!this.hasSight && !this.hasImage) {
            Toast.makeText(this.getContext(), this.getContext().getResources().getString(string.rc_no_plugin), 0).show();
        } else {
            try {
                this.show(manager, "");
                this.setCancelable(true);
                if (this.mDialog != null) {
                    this.mDialog.setCanceledOnTouchOutside(true);
                }
            } catch (IllegalStateException var3) {
                RLog.e("ImageVideoDialogFragment", "show", var3);
            }

        }
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == id.tv_sight) {
            if (this.mListener != null) {
                this.mListener.onSightClick(v);
            }
        } else if (i == id.tv_album && this.mListener != null) {
            this.mListener.onImageClick(v);
        }

        this.hideDialog();
    }

    public void hideDialog() {
        this.dismissAllowingStateLoss();
    }

    public interface ImageVideoDialogListener {
        void onSightClick(View var1);

        void onImageClick(View var1);
    }
}
