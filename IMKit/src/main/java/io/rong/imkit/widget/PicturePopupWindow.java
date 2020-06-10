//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;

import io.rong.common.FileUtils;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.utilities.KitStorageUtils;

public class PicturePopupWindow extends PopupWindow {
    private Button btn_save_pic;
    private Button btn_cancel;

    public PicturePopupWindow(final Context context, final File imageFile) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        View menuView = inflater.inflate(layout.rc_pic_popup_window, (ViewGroup) null);
        menuView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PicturePopupWindow.this.dismiss();
            }
        });
        this.btn_save_pic = (Button) menuView.findViewById(id.rc_content);
        this.btn_save_pic.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String saveImagePath = KitStorageUtils.getImageSavePath(v.getContext());
                if (imageFile != null && imageFile.exists()) {
                    String name = System.currentTimeMillis() + ".jpg";
                    FileUtils.copyFile(imageFile, saveImagePath + File.separator, name);
                    MediaScannerConnection.scanFile(context.getApplicationContext(), new String[]{saveImagePath + File.separator + name}, (String[]) null, (OnScanCompletedListener) null);
                    Toast.makeText(context, String.format(context.getString(string.rc_save_picture_at), saveImagePath + File.separator + name), 0).show();
                } else {
                    Toast.makeText(context, context.getString(string.rc_src_file_not_found), 0).show();
                }

                PicturePopupWindow.this.dismiss();
            }
        });
        this.btn_cancel = (Button) menuView.findViewById(id.rc_btn_cancel);
        this.btn_cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PicturePopupWindow.this.dismiss();
            }
        });
        this.setContentView(menuView);
        this.setWidth(-1);
        this.setHeight(-2);
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(-1342177280);
        this.setBackgroundDrawable(dw);
    }
}