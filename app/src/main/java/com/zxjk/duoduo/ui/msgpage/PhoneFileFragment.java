package com.zxjk.duoduo.ui.msgpage;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.FileInfo;
import com.zxjk.duoduo.ui.base.BaseFragment;
import com.zxjk.duoduo.utils.CommonUtils;
import com.zxjk.duoduo.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
public class PhoneFileFragment extends BaseFragment {

    protected BackHandledInterface backHandledInterface;
    private int currentCount;
    private int currentMax = 5;

    private TextView tvCurrentCount;
    private RecyclerView recyclerView;
    private Button btSendFile;
    private BaseQuickAdapter<FileInfo, BaseViewHolder> adapter;
    private ArrayList<FileInfo> fileInfo = new ArrayList<>();
    private ArrayList<FileInfo> selectedFiles = new ArrayList<>();


    private File file;
    private String uId;
    private String conversationType;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.backHandledInterface = (BackHandledInterface) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_phone_file, container, false);

        initView();

        initData();

        return rootView;
    }

    private void initView() {
        recyclerView = rootView.findViewById(R.id.recycler);
        tvCurrentCount = rootView.findViewById(R.id.tvCurrentCount);
        btSendFile = rootView.findViewById(R.id.bt_send_file);
    }

    private void initData() {
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("sUId", getActivity().MODE_PRIVATE);
        uId = mSharedPreferences.getString("uId", "");
        conversationType = mSharedPreferences.getString("ConversationType", "");
        adapter = new BaseQuickAdapter<FileInfo, BaseViewHolder>(R.layout.item_phone_file, null) {
            @Override
            protected void convert(BaseViewHolder helper, FileInfo item) {
                ImageView ivHead = helper.getView(R.id.ivHead);


                CheckBox checkBox = helper.getView(R.id.cbSelectVideo);
                TextView tvSize = helper.getView(R.id.tv_size);
                TextView tvFileNum = helper.getView(R.id.tv_file);
                helper.setText(R.id.tvTitle, item.getFileName());
                checkBox.setChecked(item.isChecked());
                if (item.isDir()) {
                    tvFileNum.setVisibility(View.GONE);
                    checkBox.setVisibility(View.GONE);
                    tvSize.setText(String.format(getResources().getString(R.string.phonefilenum), item.getFileNum()));
                    Glide.with(PhoneFileFragment.this).load(R.drawable.ic_folder).into(ivHead);
                } else {
                    String prefix = item.getFileName().substring(item.getFileName().indexOf(".") + 1);
                    if (prefix.contains("doc") || prefix.contains("docx")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_social_file_word).into(ivHead);
                    } else if (prefix.contains("xls") || prefix.contains("xlsx")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_social_file_excel).into(ivHead);
                    } else if (prefix.contains("ppt") || prefix.contains("pptx")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_social_file_ppt).into(ivHead);
                    } else if (prefix.contains("txt")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_file_txt).into(ivHead);
                    } else if (prefix.contains("mp3") || prefix.contains("wav") || prefix.contains("wma")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_audio_file).into(ivHead);
                    } else if (prefix.contains("pdf")) {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_social_file_pdf).into(ivHead);
                    } else {
                        Glide.with(PhoneFileFragment.this).load(R.drawable.ic_unknown_file).into(ivHead);
                    }

                    tvFileNum.setVisibility(View.VISIBLE);
                    checkBox.setVisibility(View.VISIBLE);

                    tvSize.setText(item.getFileSize());

                    tvFileNum.setText(item.getFileTime());
                }
            }
        };


        adapter.setOnItemClickListener((adap, view, position) -> {
            CheckBox checkBox = view.findViewById(R.id.cbSelectVideo);
            FileInfo b = (FileInfo) adap.getData().get(position);
            if (!b.isChecked() && currentCount == currentMax) {
                return;
            }
            file = new File(b.getFilePath());
            if (b.isDir()) {
                Observable.create((ObservableOnSubscribe<List<FileInfo>>) emitter ->
                        emitter.onNext(init(file))
                )
                        .compose(bindToLifecycle())
                        .subscribe(adapter::setNewData, t -> {
                            ToastUtils.showShort(R.string.phonefile_nofile);
                        });
            } else {
                if (!b.isChecked()) {
                    b.setChecked(true);
                    selectedFiles.add(adapter.getData().get(position));
                    currentCount += 1;
                    tvCurrentCount.setText(String.format(getResources().getString(R.string.phonefile_selectedfile), currentCount, currentMax));
                    checkBox.setChecked(true);
                } else {
                    b.setChecked(false);

                    Iterator<FileInfo> fileInfoIterator = selectedFiles.iterator();
                    while (fileInfoIterator.hasNext()) {
                        FileInfo file = fileInfoIterator.next();
                        if (file.getFilePath().equals(b.getFilePath())) {
                            fileInfoIterator.remove();
                        }
                    }

                    currentCount -= 1;
                    tvCurrentCount.setText(String.format(getResources().getString(R.string.phonefile_selectedfile), currentCount, currentMax));
                    checkBox.setChecked(false);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        Observable.create((ObservableOnSubscribe<List<FileInfo>>) emitter -> {
            emitter.onNext(init(Environment.getExternalStorageDirectory()));
        })
                .compose(bindToLifecycle())
                .subscribe(adapter::setNewData, t -> {
                    ToastUtils.showShort(R.string.phonefile_nofile);
                });

        btSendFile.setOnClickListener(v ->
                sendFile());
    }

    private List<FileInfo> init(File f) {
        fileInfo.clear();

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            file = new File(f.getPath());
            File[] files = f.listFiles();
            if (files != null) {
                List<File> fileList = Arrays.asList(files);
                Collections.sort(fileList, (o1, o2) -> {
                    if (o1.isDirectory() && o2.isFile())
                        return -1;
                    if (o1.isFile() && o2.isDirectory())
                        return 1;
                    return o1.getName().compareTo(o2.getName());
                });
                for (File file : files)
                    if (!file.getName().startsWith(".")) {
                        saveData(file);
                    }
            }
        }
        return fileInfo;
    }

    private void saveData(File f) {
        FileInfo fi = new FileInfo();
        try {
            fi.setFileName(f.getName());
            fi.setFilePath(f.getPath());
            fi.setFileSize(Formatter.formatFileSize(getActivity(), f.length()));
            fi.setFileTime(FileUtils.LongTimeToStr(f.lastModified()));
            fi.setFileNum(FileUtils.getFileNum(f.getPath()));
            fi.setDir(f.isDirectory());
            fi.setHidden(f.isHidden());
            fi.setModifiedData(f.lastModified());
            fi.setCanRead(f.canRead());
            fi.setCanWrite(f.canWrite());
            fi.setFileS(f.length());
            for (int i = 0; i < selectedFiles.size(); i++) {
                if (selectedFiles.get(i).getFilePath().equals(fi.getFilePath())) {
                    fi.setChecked(true);
                    tvCurrentCount.setText(String.format(getResources().getString(R.string.phonefile_selectedfile), currentCount, currentMax));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        fileInfo.add(fi);
    }


    @Override
    public void onStart() {
        super.onStart();
        backHandledInterface.setSelectedFragment(this);
    }

    public boolean onBackPressed() {
        if (!file.equals(Environment.getExternalStorageDirectory())) {
            File file1 = new File(file.getParent());
            Observable.create((ObservableOnSubscribe<List<FileInfo>>) emitter -> {
                emitter.onNext(init(file1));
            })
                    .compose(bindToLifecycle())
                    .subscribe(adapter::setNewData, t ->
                            ToastUtils.showShort(R.string.phonefile_nofile)
                    );

            return true;
        }
        return false;
    }

    private void sendFile() {

        if (selectedFiles != null && selectedFiles.size() != 0) {
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
                        FileInfo fileInfo = selectedFiles.get(l.intValue());
                        Uri filePath = Uri.parse("file://" + fileInfo.getFilePath());
                        FileMessage fileMessage = FileMessage.obtain(filePath);
                        String fileName = fileInfo.getFileName();
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
        } else {
            ToastUtils.showShort(getResources().getString(R.string.select_file));
        }
    }


    public interface BackHandledInterface {
        void setSelectedFragment(PhoneFileFragment backHandledFragment);
    }
}
