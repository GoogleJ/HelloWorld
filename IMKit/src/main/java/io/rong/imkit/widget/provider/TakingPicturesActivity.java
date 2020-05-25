//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget.provider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import io.rong.common.RLog;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.message.utils.BitmapUtil;

public class TakingPicturesActivity extends Activity implements OnClickListener {
    private static final String TAG = "TakingPicturesActivity";
    private static final int REQUEST_CAMERA = 2;
    private static int COMPRESSED_SIZE = 1080;
    private ImageView mImage;
    private Uri mSavedPicUri;

    public TakingPicturesActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(1);
        this.setContentView(layout.rc_ac_camera);
        Button cancel = (Button) this.findViewById(id.rc_back);
        Button send = (Button) this.findViewById(id.rc_send);
        this.mImage = (ImageView) this.findViewById(id.rc_img);
        cancel.setOnClickListener(this);
        send.setOnClickListener(this);
        RLog.d("TakingPicturesActivity", "onCreate savedInstanceState : " + savedInstanceState);

        try {
            Resources resources = this.getApplicationContext().getResources();
            COMPRESSED_SIZE = resources.getInteger(resources.getIdentifier("rc_image_size", "integer", this.getApplicationContext().getPackageName()));
        } catch (Exception var7) {
            RLog.e("TakingPicturesActivity", "onCreate", var7);
        }

        if (savedInstanceState == null) {
            this.startCamera();
        } else {
            String str = savedInstanceState.getString("photo_uri");
            if (str != null) {
                this.mSavedPicUri = Uri.parse(str);

                try {
                    this.mImage.setImageBitmap(BitmapUtil.getNewResizedBitmap(this, this.mSavedPicUri, COMPRESSED_SIZE));
                } catch (IOException var6) {
                    RLog.e("TakingPicturesActivity", "onCreate", var6);
                }
            }
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void onClick(View v) {
        File file = new File(this.mSavedPicUri.getPath());
        if (!file.exists()) {
            this.finish();
        }

        if (v.getId() == id.rc_send) {
            if (this.mSavedPicUri != null) {
                Intent data = new Intent();
                data.setData(this.mSavedPicUri);
                this.setResult(-1, data);
            }

            this.finish();
        } else if (v.getId() == id.rc_back) {
            this.finish();
        }

    }

    private void startCamera() {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!path.exists()) {
            path.mkdirs();
        }

        String name = System.currentTimeMillis() + ".jpg";
        File file = new File(path, name);
        this.mSavedPicUri = Uri.fromFile(file);
        RLog.d("TakingPicturesActivity", "startCamera output pic uri =" + this.mSavedPicUri);
        Intent intent = new Intent();
        intent.setAction("android.media.action.IMAGE_CAPTURE");
        List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, 65536);
        Uri uri = null;

        try {
            uri = FileProvider.getUriForFile(this, this.getPackageName() + this.getString(string.rc_authorities_fileprovider), file);
        } catch (Exception var11) {
            throw new RuntimeException("Please check IMKit Manifest FileProvider config.");
        }

        Iterator var7 = resInfoList.iterator();

        while (var7.hasNext()) {
            ResolveInfo resolveInfo = (ResolveInfo) var7.next();
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, uri, 2);
            this.grantUriPermission(packageName, uri, 1);
        }

        intent.putExtra("output", uri);
        intent.addCategory("android.intent.category.DEFAULT");

        try {
            this.startActivityForResult(intent, 2);
        } catch (SecurityException var10) {
            Log.e("TakingPicturesActivity", "REQUEST_CAMERA SecurityException!!!");
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        RLog.d("TakingPicturesActivity", "onActivityResult resultCode = " + resultCode + ", intent=" + data);
        if (resultCode != -1) {
            this.finish();
        } else {
            switch (requestCode) {
                case 2:
                    if (resultCode == 0) {
                        this.finish();
                        Log.e("TakingPicturesActivity", "RESULT_CANCELED");
                    }

                    if (this.mSavedPicUri != null && resultCode == -1) {
                        try {
                            this.mImage.setImageBitmap(BitmapUtil.getNewResizedBitmap(this, this.mSavedPicUri, 1080));
                        } catch (IOException var5) {
                            RLog.e("TakingPicturesActivity", "onActivityResult", var5);
                        }
                    }

                    super.onActivityResult(requestCode, resultCode, data);
                    return;
                default:
            }
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e("TakingPicturesActivity", "onRestoreInstanceState");
        this.mSavedPicUri = Uri.parse(savedInstanceState.getString("photo_uri"));
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        Log.e("TakingPicturesActivity", "onSaveInstanceState");
        outState.putString("photo_uri", this.mSavedPicUri.toString());
        super.onSaveInstanceState(outState);
    }
}