//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imkit.RongConfigurationManager;
import io.rong.imkit.activity.FileListActivity;
import io.rong.imkit.model.FileInfo;
import io.rong.imkit.utils.FileTypeUtils;
import io.rong.imkit.utils.FileTypeUtils.FileNameComparator;
import io.rong.imkit.widget.LoadingDialogFragment;
import io.rong.imkit.widget.adapter.FileListAdapter;

public class FileListFragment extends Fragment implements OnItemClickListener, OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    private static final String MOBILE_DIR = "directory";
    private static final int ALL_FILE_FILES = 1;
    private static final int ALL_VIDEO_FILES = 2;
    private static final int ALL_AUDIO_FILES = 3;
    private static final int ALL_other_FILES = 4;
    private static final int ALL_RAM_FILES = 5;
    private static final int ALL_SD_FILES = 6;
    private static final int ROOT_DIR = 100;
    private static final int SD_CARD_ROOT_DIR = 101;
    private static final int FILE_TRAVERSE_TYPE_ONE = 200;
    private static final int FILE_TRAVERSE_TYPE_TWO = 201;
    private ImageView mFileListTitleImageBack;
    private TextView mFilesCategoryTitleTextView;
    private TextView mFileSelectStateTextView;
    private ListView mFilesListView;
    private LinearLayout mFileLoadingLinearLayout;
    private TextView mNoFileMessageTextView;
    private FileListAdapter mFileListAdapter;
    private AsyncTask mLoadFilesTask;
    private List<FileInfo> mFilesList;
    private HashSet<FileInfo> mSelectedFiles = new HashSet();
    private File currentDir;
    private File startDir;
    private String mFileInfoMessage;
    private int fileTraverseType;
    private int fileFilterType;
    private String mPath;
    private LoadingDialogFragment mLoadingDialogFragment;

    public FileListFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getActivity().getIntent();
        int rootDirType = intent.getIntExtra("rootDirType", -1);
        this.fileFilterType = intent.getIntExtra("fileFilterType", -1);
        this.fileTraverseType = intent.getIntExtra("fileTraverseType", -1);
        this.mPath = intent.getStringExtra("rootDir");
        Bundle arguments = this.getArguments();
        if (arguments != null && arguments.containsKey("directory")) {
            this.currentDir = new File(arguments.getString("directory"));
        } else if (rootDirType == 100) {
            String path = Environment.getExternalStorageDirectory().getPath();
            this.currentDir = new File(path);
        } else if (rootDirType == 101) {
            this.currentDir = new File(this.mPath);
        }

    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout.rc_fr_file_list, container, false);
        this.mFileListTitleImageBack = (ImageView) view.findViewById(id.rc_ad_iv_file_list_go_back);
        this.mFilesCategoryTitleTextView = (TextView) view.findViewById(id.rc_ad_tv_file_list_title);
        this.mFileSelectStateTextView = (TextView) view.findViewById(id.rc_ad_tv_file_list_select_state);
        this.mFilesListView = (ListView) view.findViewById(id.rc_fm_lv_storage_folder_list_files);
        this.mFileLoadingLinearLayout = (LinearLayout) view.findViewById(id.rc_fm_ll_storage_folder_list_load);
        this.mNoFileMessageTextView = (TextView) view.findViewById(id.rc_fm_tv_no_file_message);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.mLoadingDialogFragment = LoadingDialogFragment.newInstance("", this.getResources().getString(string.rc_notice_data_is_loading));
        this.loadFileList();
        String text = "";
        switch (this.fileFilterType) {
            case 1:
                text = this.getString(string.rc_fr_file_category_title_text);
                break;
            case 2:
                text = this.getString(string.rc_fr_file_category_title_video);
                break;
            case 3:
                text = this.getString(string.rc_fr_file_category_title_audio);
                break;
            case 4:
                text = this.getString(string.rc_fr_file_category_title_other);
                break;
            case 5:
                text = this.getString(string.rc_fr_file_category_title_ram);
                break;
            case 6:
                text = this.getString(string.rc_fr_file_category_title_sd);
        }

        this.mFilesCategoryTitleTextView.setText(text);
        this.mFilesListView.setOnItemClickListener(this);
        this.mFileListTitleImageBack.setOnClickListener(this);
        this.mFileSelectStateTextView.setOnClickListener(this);
        this.mFileSelectStateTextView.setClickable(false);
        this.mFileSelectStateTextView.setSelected(false);
    }

    public void onDestroyView() {
        this.mFilesListView = null;
        this.mLoadingDialogFragment = null;
        super.onDestroyView();
    }

    public void onDestroy() {
        if (this.mLoadFilesTask != null) {
            this.mLoadFilesTask.cancel(true);
        }

        super.onDestroy();
    }

    @TargetApi(11)
    private void loadFileList() {
        if (this.mLoadFilesTask == null) {
            if (VERSION.SDK_INT >= 11) {
                this.mLoadFilesTask = (new AsyncTask<File, Void, List<FileInfo>>() {
                    protected void onPreExecute() {
                        FileListFragment.this.mLoadingDialogFragment.show(FileListFragment.this.getFragmentManager());
                        if (FileListFragment.this.fileTraverseType == 200) {
                            FileListFragment.this.showLoadingFileView();
                        }

                        super.onPreExecute();
                    }

                    protected List<FileInfo> doInBackground(File... params) {
                        FileListFragment.this.mFileInfoMessage = "";

                        try {
                            List<FileInfo> fileInfos = new ArrayList();
                            if (FileListFragment.this.fileTraverseType == 201) {
                                File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
                                fileInfos = FileTypeUtils.getFileInfosFromFileArray(files);
                            } else if (FileListFragment.this.fileTraverseType == 200) {
                                FileListFragment.this.startDir = new File(Environment.getExternalStorageDirectory().getPath());
                                switch (FileListFragment.this.fileFilterType) {
                                    case 1:
                                        fileInfos = FileTypeUtils.getTextFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_text);
                                        break;
                                    case 2:
                                        fileInfos = FileTypeUtils.getVideoFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_video);
                                        break;
                                    case 3:
                                        fileInfos = FileTypeUtils.getAudioFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_audio);
                                        break;
                                    case 4:
                                        fileInfos = FileTypeUtils.getOtherFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_other);
                                }
                            }

                            if (fileInfos == null) {
                                return new ArrayList();
                            } else if (this.isCancelled()) {
                                return new ArrayList();
                            } else {
                                Collections.sort((List) fileInfos, new FileNameComparator());
                                if (FileListFragment.this.mLoadingDialogFragment != null) {
                                    FileListFragment.this.mLoadingDialogFragment.dismiss();
                                }

                                return (List) fileInfos;
                            }
                        } catch (Exception var4) {
                            if (FileListFragment.this.mLoadingDialogFragment != null) {
                                FileListFragment.this.mLoadingDialogFragment.dismiss();
                            }

                            return new ArrayList();
                        }
                    }

                    protected void onCancelled() {
                        FileListFragment.this.mLoadFilesTask = null;
                        if (FileListFragment.this.mLoadingDialogFragment != null) {
                            FileListFragment.this.mLoadingDialogFragment.dismiss();
                        }

                        super.onCancelled();
                    }

                    protected void onPostExecute(List<FileInfo> fileInfos) {
                        FileListFragment.this.mFileLoadingLinearLayout.setVisibility(8);
                        FileListFragment.this.mFilesListView.setVisibility(0);
                        FileListFragment.this.mLoadFilesTask = null;

                        try {
                            FileListFragment.this.mFilesList = fileInfos;
                            if (FileListFragment.this.mFilesList.isEmpty()) {
                                FileListFragment.this.showNoFileMessage(FileListFragment.this.mFileInfoMessage);
                                return;
                            }

                            FileListFragment.this.mFileListAdapter = new FileListAdapter(FileListFragment.this.getActivity(), FileListFragment.this.mFilesList, FileListFragment.this.mSelectedFiles);
                            FileListFragment.this.setListViewAdapter(FileListFragment.this.mFileListAdapter);
                        } catch (Exception var3) {
                            FileListFragment.this.showNoFileMessage(var3.getMessage());
                        }

                        super.onPostExecute(fileInfos);
                    }
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new File[]{this.currentDir});
            } else {
                this.mLoadFilesTask = (new AsyncTask<File, Void, List<FileInfo>>() {
                    protected void onPreExecute() {
                        if (FileListFragment.this.fileTraverseType == 200) {
                            FileListFragment.this.showLoadingFileView();
                        }

                        super.onPreExecute();
                    }

                    protected List<FileInfo> doInBackground(File... params) {
                        FileListFragment.this.mFileInfoMessage = "";

                        try {
                            List<FileInfo> fileInfos = new ArrayList();
                            if (FileListFragment.this.fileTraverseType == 201) {
                                File[] files = params[0].listFiles(FileTypeUtils.ALL_FOLDER_AND_FILES_FILTER);
                                fileInfos = FileTypeUtils.getFileInfosFromFileArray(files);
                            } else if (FileListFragment.this.fileTraverseType == 200) {
                                switch (FileListFragment.this.fileFilterType) {
                                    case 1:
                                        fileInfos = FileTypeUtils.getTextFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_text);
                                        break;
                                    case 2:
                                        fileInfos = FileTypeUtils.getVideoFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_video);
                                        break;
                                    case 3:
                                        fileInfos = FileTypeUtils.getAudioFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_audio);
                                        break;
                                    case 4:
                                        fileInfos = FileTypeUtils.getOtherFilesInfo(FileListFragment.this.startDir);
                                        FileListFragment.this.mFileInfoMessage = FileListFragment.this.getString(string.rc_fr_file_category_title_other);
                                }
                            }

                            if (fileInfos == null) {
                                return new ArrayList();
                            } else if (this.isCancelled()) {
                                return new ArrayList();
                            } else {
                                Collections.sort((List) fileInfos, new FileNameComparator());
                                if (FileListFragment.this.mLoadingDialogFragment != null) {
                                    FileListFragment.this.mLoadingDialogFragment.dismiss();
                                }

                                return (List) fileInfos;
                            }
                        } catch (Exception var4) {
                            return new ArrayList();
                        }
                    }

                    protected void onCancelled() {
                        FileListFragment.this.mLoadFilesTask = null;
                        if (FileListFragment.this.mLoadingDialogFragment != null) {
                            FileListFragment.this.mLoadingDialogFragment.dismiss();
                        }

                        super.onCancelled();
                    }

                    protected void onPostExecute(List<FileInfo> fileInfos) {
                        FileListFragment.this.mFileLoadingLinearLayout.setVisibility(8);
                        FileListFragment.this.mFilesListView.setVisibility(0);
                        FileListFragment.this.mLoadFilesTask = null;

                        try {
                            FileListFragment.this.mFilesList = fileInfos;
                            if (FileListFragment.this.mFilesList.isEmpty()) {
                                FileListFragment.this.showNoFileMessage(FileListFragment.this.mFileInfoMessage);
                                return;
                            }

                            FileListFragment.this.mFileListAdapter = new FileListAdapter(FileListFragment.this.getActivity(), FileListFragment.this.mFilesList, FileListFragment.this.mSelectedFiles);
                            FileListFragment.this.setListViewAdapter(FileListFragment.this.mFileListAdapter);
                        } catch (Exception var3) {
                            FileListFragment.this.showNoFileMessage(var3.getMessage());
                        }

                        super.onPostExecute(fileInfos);
                    }
                }).execute(new File[]{this.currentDir});
            }

        }
    }

    private void setListViewAdapter(FileListAdapter fileListAdapter) {
        this.mFileListAdapter = fileListAdapter;
        if (this.mFilesListView != null) {
            this.mFilesListView.setAdapter(fileListAdapter);
        }

    }

    private void showLoadingFileView() {
        this.mFilesListView.setVisibility(8);
        this.mNoFileMessageTextView.setVisibility(8);
        this.mFileLoadingLinearLayout.setVisibility(0);
    }

    private void showNoFileMessage(String message) {
        this.mFilesListView.setVisibility(8);
        this.mFileLoadingLinearLayout.setVisibility(8);
        this.mNoFileMessageTextView.setVisibility(0);
        this.mNoFileMessageTextView.setText(this.getResources().getString(string.rc_fr_no_file_message, new Object[]{message}));
    }

    private void navigateTo(File folder) {
        FileListActivity activity = (FileListActivity) this.getActivity();
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString("directory", folder.getAbsolutePath());
        fragment.setArguments(args);
        activity.showFragment(fragment);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object selectedObject = parent.getItemAtPosition(position);
        if (selectedObject instanceof FileInfo) {
            FileInfo selectedFile = (FileInfo) selectedObject;
            if (selectedFile.isDirectory()) {
                this.navigateTo(new File(selectedFile.getFilePath()));
            } else {
                int fileMaxSize = RongConfigurationManager.getInstance().getFileMaxSize(this.getActivity());
                String unit = "MB";
                if (fileMaxSize >= 1024) {
                    unit = "GB";
                }

                if (selectedFile.getFileSize() > (long) fileMaxSize * 1024L * 1024L) {
                    if (unit.equals("GB")) {
                        fileMaxSize /= 1024;
                    }

                    Toast.makeText(this.getActivity(), String.format(this.getResources().getString(string.rc_fr_file_size_limit), fileMaxSize, unit), 0).show();
                    return;
                }

                if (this.mSelectedFiles.contains(selectedFile)) {
                    this.mSelectedFiles.remove(selectedFile);
                    this.mFileListAdapter.notifyDataSetChanged();
                } else if (!view.isSelected() && this.mSelectedFiles.size() < 20) {
                    this.mSelectedFiles.add(selectedFile);
                    this.mFileListAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this.getActivity(), this.getResources().getString(string.rc_fr_file_list_most_selected_files), 0).show();
                }

                if (this.mSelectedFiles.size() > 0) {
                    this.mFileSelectStateTextView.setClickable(true);
                    this.mFileSelectStateTextView.setSelected(true);
                    this.mFileSelectStateTextView.setText(this.getResources().getString(string.rc_ad_send_file_select_file, new Object[]{this.mSelectedFiles.size()}));
                } else {
                    this.mFileSelectStateTextView.setClickable(false);
                    this.mFileSelectStateTextView.setSelected(false);
                    this.mFileSelectStateTextView.setText(this.getResources().getString(string.rc_ad_send_file_no_select_file));
                }
            }
        }

    }

    public void onClick(View v) {
        if (v == this.mFileSelectStateTextView) {
            Intent intent = new Intent();
            intent.putExtra("selectedFiles", this.mSelectedFiles);
            this.getActivity().setResult(-1, intent);
            this.getActivity().finish();
        }

        if (v == this.mFileListTitleImageBack) {
            this.getActivity().finish();
        }

    }
}
