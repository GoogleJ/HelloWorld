package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class SocialFileEditActivity extends BaseActivity {

    //20M
    private final String maxFileSize = "20971520";

    private String id;
    private int currentCount;
    private int currentMax;
    private int maxCount;

    private TextView tvCurrentCount;
    private TextView tvMaxCount;
    private LinearLayout llTopTips;
    private RecyclerView recycler;
    private BaseQuickAdapter<FileBean, BaseViewHolder> adapter;

    private ArrayList<FileBean> selectedFiles = new ArrayList<>();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_file_edit);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.addfile);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        id = getIntent().getStringExtra("id");
        currentMax = getIntent().getIntExtra("currentMax", 0);
        maxCount = getIntent().getIntExtra("maxCount", 4);

        tvCurrentCount = findViewById(R.id.tvCurrentCount);
        tvMaxCount = findViewById(R.id.tvMaxCount);
        llTopTips = findViewById(R.id.llTopTips);
        recycler = findViewById(R.id.recycler);

        tvMaxCount.setText("最多上传" + maxCount + "份社群资料，请上传体验");

        adapter = new BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_social_file_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, FileBean item) {
                ImageView ivHead = helper.getView(R.id.ivHead);
                helper.setText(R.id.tvTitle, item.title)
                        .setText(R.id.tvSize, item.size).setChecked(R.id.cbSelectVideo, item.isChecked());

                switch (item.format) {
                    case "excel":
                        Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_excel).into(ivHead);
                        break;
                    case "ppt":
                        Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_ppt).into(ivHead);
                        break;
                    case "word":
                        Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_word).into(ivHead);
                        break;
                    case "pdf":
                        Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_pdf).into(ivHead);
                        break;
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FileBean b = (FileBean) adapter.getData().get(position);
            if (!b.isChecked() && currentCount == currentMax) {
                return;
            }
            if (!b.isChecked()) {
                b.setChecked(true);
                selectedFiles.add(((FileBean) adapter.getData().get(position)));
                currentCount += 1;
                tvCurrentCount.setText("已选" + " (" + currentCount + "/" + currentMax + ")");
                adapter.notifyItemChanged(position);
            } else {
                b.setChecked(false);
                selectedFiles.remove(adapter.getData().get(position));
                currentCount -= 1;
                tvCurrentCount.setText("已选" + " (" + currentCount + "/" + currentMax + ")");
                adapter.notifyItemChanged(position);
            }
        });

        View emptyview = LayoutInflater.from(this).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(R.string.emptylist5);
        iv.setImageResource(R.drawable.ic_empty_videos);
        adapter.setEmptyView(emptyview);

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Observable.create((ObservableOnSubscribe<List<FileBean>>) emitter -> emitter.onNext(getFilesByType(TYPE_DOC)))
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                .subscribe(adapter::setNewData, t -> {
                    ToastUtils.showShort("暂时无法读取本地文件，请联系客服");
                    finish();
                });
    }

    public void closeTip(View view) {
        llTopTips.setVisibility(View.GONE);
    }

    public void uploadFile(View view) {

    }

    /**
     * 文档类型
     */
    public static final int TYPE_DOC = 0;
    /**
     * apk类型
     */
    public static final int TYPE_APK = 1;
    /**
     * 压缩包类型
     */
    public static final int TYPE_ZIP = 2;

    /**
     * 通过文件类型得到相应文件的集合
     **/
    private List<FileBean> getFilesByType(int fileType) {
        List<FileBean> files = new ArrayList<>();
        Cursor c;

        c = getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size", "title"}, MediaStore.Files.FileColumns.SIZE + "<=?", new String[]{maxFileSize}, null);
        if (c == null) {
            return new ArrayList<>();
        }
        int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
        int titleindex = c.getColumnIndex(MediaStore.Files.FileColumns.TITLE);

        while (c.moveToNext()) {
            String path = c.getString(dataindex);
            long size = c.getLong(sizeindex);
            String title = c.getString(titleindex);

            if (getFileType(path) == fileType) {
                if (!FileUtils.isFileExists(path)) {
                    continue;
                }
                if (size == 0) continue;
                FileBean fileBean = new FileBean(path, Formatter.formatFileSize(SocialFileEditActivity.this, size), title, getFileFormat(path));
                files.add(fileBean);
            }
        }
        c.close();

        return files;
    }

    private int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".ppt") || path.endsWith(".pptx") || path.endsWith("pdf") || path.endsWith("pdfx")) {
            return TYPE_DOC;
        } else if (path.endsWith(".apk")) {
            return TYPE_APK;
        } else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return TYPE_ZIP;
        } else {
            return -1;
        }
    }

    private String getFileFormat(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx")) {
            return "word";
        }
        if (path.endsWith(".xls") || path.endsWith(".xlsx")) {
            return "excel";
        }
        if (path.endsWith(".ppt") || path.endsWith(".pptx")) {
            return "ppt";
        }
        if (path.endsWith(".pdf") || path.endsWith(".pdfx")) {
            return "pdf";
        }
        return "unknow";
    }

    static class FileBean {
        /**
         * 文件的路径
         */
        public String path;

        private String size;

        private String title;

        private String format;

        private boolean checked;

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public FileBean(String path, String size, String title, String format) {
            this.path = path;
            this.size = size;
            this.title = title;
            this.format = format;
        }
    }
}
