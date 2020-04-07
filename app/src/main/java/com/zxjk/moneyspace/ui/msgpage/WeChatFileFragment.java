package com.zxjk.moneyspace.ui.msgpage;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.network.rx.RxSchedulers;
import com.zxjk.moneyspace.ui.base.BaseFragment;
import com.zxjk.moneyspace.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.FileMessage;


@SuppressLint("CheckResult")
public class WeChatFileFragment extends BaseFragment {


    private int currentCount;
    private int currentMax = 5;
    private TextView tvCurrentCount;
    private RecyclerView recyclerView;
    private Button btSendFile;
    private BaseQuickAdapter<FileBean, BaseViewHolder> adapter;
    private ArrayList<FileBean> selectedFiles = new ArrayList<>();

    private String uId;
    private String conversationType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_we_chat_file, container, false);


        initView();

        initData();

        return rootView;
    }

    private void initView() {
        recyclerView = rootView.findViewById(R.id.we_chat_recycler);
        tvCurrentCount = rootView.findViewById(R.id.tvCurrentCount);
        btSendFile = rootView.findViewById(R.id.bt_send_file);
    }

    private void initData() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("sUId", getActivity().MODE_PRIVATE);
        uId = mSharedPreferences.getString("uId", "");
        conversationType = mSharedPreferences.getString("ConversationType", "");

        adapter = new BaseQuickAdapter<FileBean, BaseViewHolder>(R.layout.item_social_file_list, null) {
            @Override
            protected void convert(BaseViewHolder helper, FileBean item) {
                ImageView ivHead = helper.getView(R.id.ivHead);
                helper.setText(R.id.tvTitle, item.title)
                        .setText(R.id.tv_size, item.size).setChecked(R.id.cbSelectVideo, item.isChecked());
                TextView tvFile = helper.getView(R.id.tv_file);
                tvFile.setVisibility(View.VISIBLE);

                tvFile.setText(com.zxjk.moneyspace.utils.FileUtils.LongTimeToStr(item.getTime()));
                if (item.format.contains("doc") || item.format.contains("docx")) {
                    Glide.with(WeChatFileFragment.this).load(R.drawable.ic_social_file_word).into(ivHead);
                } else if (item.format.contains("xls") || item.format.contains("xlsx")) {
                    Glide.with(WeChatFileFragment.this).load(R.drawable.ic_social_file_excel).into(ivHead);
                } else if (item.format.contains("ppt") || item.format.contains("pptx")) {
                    Glide.with(WeChatFileFragment.this).load(R.drawable.ic_social_file_ppt).into(ivHead);
                } else {
                    Glide.with(WeChatFileFragment.this).load(R.drawable.ic_social_file_pdf).into(ivHead);
                }
            }
        };

        adapter.setOnItemClickListener((adapter, view, position) -> {
            FileBean b = (FileBean) adapter.getData().get(position);

            if (!b.isChecked() && currentCount == currentMax) {
                ToastUtils.showShort(getString(R.string.file_remind));
                return;
            }
            if (!b.isChecked()) {
                b.setChecked(true);
                selectedFiles.add(((FileBean) adapter.getData().get(position)));
                currentCount += 1;
                tvCurrentCount.setText(String.format(getResources().getString(R.string.phonefile_selectedfile), currentCount , currentMax));
                adapter.notifyItemChanged(position);
            } else {
                b.setChecked(false);
                selectedFiles.remove(adapter.getData().get(position));
                currentCount -= 1;
                tvCurrentCount.setText(String.format(getResources().getString(R.string.phonefile_selectedfile), currentCount , currentMax));
                adapter.notifyItemChanged(position);
            }
        });

        View emptyview = LayoutInflater.from(getActivity()).inflate(R.layout.empty_publicgroup, null, false);
        TextView tv = emptyview.findViewById(R.id.tv);
        ImageView iv = emptyview.findViewById(R.id.iv);
        tv.setText(getResources().getString(R.string.no_file));
        iv.setImageResource(R.drawable.ic_empty_videos);
        adapter.setEmptyView(emptyview);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Observable.create((ObservableOnSubscribe<List<FileBean>>) emitter -> {
            emitter.onNext(getFilesByType());
        })
                .compose(bindToLifecycle())
                .compose(RxSchedulers.ioObserver(CommonUtils.initDialog(WeChatFileFragment.this.getActivity())))
                .subscribe(adapter::setNewData, t -> {
                    ToastUtils.showShort(R.string.phonefile_nofile);
                });

        btSendFile.setOnClickListener(v -> sendFile());
    }

    /**
     * 通过文件类型得到相应文件的集合
     **/
    private List<FileBean> getFilesByType() {
        List<FileBean> files = new ArrayList<>();

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

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

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


    public void sendFile() {

        if(selectedFiles!=null && selectedFiles.size() != 0){
            Observable.interval(0, 250, TimeUnit.MILLISECONDS)
                    .take(selectedFiles.size())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(d -> CommonUtils.initDialog(getActivity(), getResources().getString(R.string.file_filesending)).show())
                    .doOnDispose(CommonUtils::destoryDialog)
                    .doOnComplete(() -> {
                        ToastUtils.showShort(R.string.has_bean_sent);
                        CommonUtils.destoryDialog();
                        getActivity().finish();
                    })
                    .compose(bindToLifecycle())
                    .subscribe(l -> {
                        FileBean fileBean = selectedFiles.get(l.intValue());
                        Uri filePath = Uri.parse("file://" + fileBean.getPath());
                        FileMessage fileMessage = FileMessage.obtain(filePath);
                        String fileName = fileBean.getTitle();
                        String prefix = fileName.substring(fileName.indexOf(".") + 1);
                        if (fileMessage != null) {
                            fileMessage.setType(prefix);
                            Message message;
                            if (conversationType.equals("private")) {
                                message = Message.obtain(uId, Conversation.ConversationType.PRIVATE, fileMessage);
                                RongIM.getInstance().sendMediaMessage(message, null, null, (IRongCallback.ISendMediaMessageCallback) null);
                            } else if (conversationType.equals("group")) {
                                message = Message.obtain(uId, Conversation.ConversationType.GROUP, fileMessage);
                                RongIM.getInstance().sendMediaMessage(message, null, null, (IRongCallback.ISendMediaMessageCallback) null);
                            }
                        }
                    });
        }else {
            ToastUtils.showShort(getResources().getString(R.string.select_file));
        }

    }

}
