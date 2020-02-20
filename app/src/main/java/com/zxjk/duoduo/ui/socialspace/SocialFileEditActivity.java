package com.zxjk.duoduo.ui.socialspace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
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
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.request.EditCommunityFileRequest;
import com.zxjk.duoduo.network.Api;
import com.zxjk.duoduo.network.ServiceFactory;
import com.zxjk.duoduo.network.rx.RxSchedulers;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.dialog.LoadingDialog;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.OssUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

public class SocialFileEditActivity extends BaseActivity {

    //20M
    private final String maxFileSize = "20971520";

    private int currentCount;
    private int currentMax;
    private int maxCount;

    private TextView tvCurrentCount;
    private TextView tvMaxCount;
    private LinearLayout llTopTips;
    private RecyclerView recycler;
    private BaseQuickAdapter<FileBean, BaseViewHolder> adapter;

    private ArrayList<FileBean> selectedFiles = new ArrayList<>();

    private LoadingDialog uploadLoading;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_file_edit);

        uploadLoading = new LoadingDialog(this, "上传中");
        uploadLoading.setDelayTimeStamp(0);

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.addfile);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

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
                        .setText(R.id.tv_size, item.size).setChecked(R.id.cbSelectVideo, item.isChecked());

                if (item.format.contains("doc") || item.format.contains("docx")) {
                    Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_word).into(ivHead);
                } else if (item.format.contains("xls") || item.format.contains("xlsx")) {
                    Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_excel).into(ivHead);
                } else if (item.format.contains("ppt") || item.format.contains("pptx")) {
                    Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_ppt).into(ivHead);
                } else {
                    Glide.with(SocialFileEditActivity.this).load(R.drawable.ic_social_file_pdf).into(ivHead);
                }
            }
        };

        if (currentMax == 0) {
            tvCurrentCount.setText("目前已达上传上限");
        } else {
            tvCurrentCount.setText("已选" + " (0" + "/" + currentMax + ")");
        }

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

        Observable.create((ObservableOnSubscribe<List<FileBean>>) emitter -> {
            emitter.onNext(getFilesByType(TYPE_DOC));
        })
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

    private List<EditCommunityFileRequest.FilesListBean> uploadList;

    public void uploadFile(View view) {
        if (currentCount == 0) {
            ToastUtils.showShort(R.string.selectFiles);
            return;
        }
        uploadList = new ArrayList<>(currentMax);
        uploadFiles();
    }

    @SuppressLint("CheckResult")
    private void uploadFiles() {
        if (uploadLoading.isShowing()) {
            uploadLoading.dismissReally();
        }

        if (selectedFiles.size() == 0) {
            if (uploadList.size() != 0) {
                //do upload
                EditCommunityFileRequest request = new EditCommunityFileRequest();
                request.setType("add");
                request.setGroupId(getIntent().getStringExtra("id"));
                request.setFilesList(uploadList);

                ServiceFactory.getInstance().getBaseService(Api.class)
                        .editCommunityFile(GsonUtils.toJson(request, false))
                        .compose(bindToLifecycle())
                        .compose(RxSchedulers.normalTrans())
                        .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(this)))
                        .subscribe(s -> {
                            ToastUtils.showShort(R.string.upload_social_file_success);
                            Intent intent = new Intent();
                            setResult(1, intent);
                            finish();
                        }, t -> {
                            finish();
                            handleApiError(t);
                        });
            }
            return;
        }

        uploadLoading.setText("上传中");
        uploadLoading.show();

        OssUtils.uploadFile(selectedFiles.get(0).path, selectedFiles.get(0).format, new OssUtils.OssCallBack1() {
            @Override
            public void onSuccess(String fileAddress) {
                EditCommunityFileRequest.FilesListBean bean = new EditCommunityFileRequest.FilesListBean();
                bean.setFileAddress(fileAddress);
                bean.setFileFormat(selectedFiles.get(0).format.replace(".", ""));
                bean.setFileName(selectedFiles.get(0).title);
                bean.setFileSize(selectedFiles.get(0).size);

                uploadList.add(bean);
                selectedFiles.remove(0);
                uploadFiles();
            }

            @Override
            public void onFail() {
                ToastUtils.showShort(R.string.upload_social_file_fail);
                if (uploadLoading.isShowing()) {
                    uploadLoading.dismissReally();
                }
                finish();
            }
        }, progress -> runOnUiThread(() -> uploadLoading.setText("上传中," + (int) (progress * 100) + "%")));
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
        c = getContentResolver().query(MediaStore.Files.getContentUri("external"), new String[]{"_id", "_data", "_size", "title", "date_modified"},
                MediaStore.Files.FileColumns.SIZE + "<=? and _data not like ?", new String[]{maxFileSize,
                        "%" + Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg/Download%"}, null);

        if (c == null) {
            return new ArrayList<>();
        }

        int dataindex = c.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        int sizeindex = c.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
        int titleindex = c.getColumnIndex(MediaStore.MediaColumns.TITLE);
        int dateIndex = c.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED);

        while (c.moveToNext()) {
            String path = c.getString(dataindex);
            long size = c.getLong(sizeindex);
            String title = c.getString(titleindex);
            Integer date = c.getInt(dateIndex);

            if (getFileType(path) == fileType) {
                if (!FileUtils.isFileExists(path)) {
                    continue;
                }
                if (size == 0) continue;
                if (!getFileFormat(path).equals("unknown")) {
                    FileBean fileBean = new FileBean(path, Formatter.formatFileSize(SocialFileEditActivity.this, size), title, getFileFormat(path), date);
                    files.add(fileBean);
                }
            }
        }
        c.close();

        File wechatFile = new File(Environment.getExternalStorageDirectory().getPath() + "/tencent/MicroMsg/Download");
        if (FileUtils.isFileExists(wechatFile)) {
            File[] wechatFiles = wechatFile.listFiles();
            if (wechatFiles != null) {
                for (File file : wechatFiles) {
                    if (!getFileFormat(file.getPath()).equals("unknown")) {
                        FileBean fileBean = new FileBean(file.getPath(), FileUtils.getSize(file), file.getName(), getFileFormat(file.getPath()), FileUtils.getFileLastModified(file));
                        files.add(0, fileBean);
                    }
                }
            }
        }

        Collections.sort(files, (o1, o2) -> -Long.valueOf(o1.time).compareTo(o2.time));

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
        if (path.endsWith(".doc")) {
            return ".doc";
        }
        if (path.endsWith(".docx")) {
            return ".docx";
        }
        if (path.endsWith(".xls")) {
            return ".xls";
        }
        if (path.endsWith(".xlsx")) {
            return ".xlsx";
        }
        if (path.endsWith(".ppt")) {
            return ".ppt";
        }
        if (path.endsWith(".pptx")) {
            return ".pptx";
        }
        if (path.endsWith(".pdf")) {
            return ".pdf";
        }
        if (path.endsWith(".pdfx")) {
            return ".pdfx";
        }
        return "unknown";
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

        private long time;

        public FileBean(String path, String size, String title, String format, long time) {
            this.path = path;
            this.size = size;
            this.title = title;
            this.format = format;
            this.time = time;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

    }
}
