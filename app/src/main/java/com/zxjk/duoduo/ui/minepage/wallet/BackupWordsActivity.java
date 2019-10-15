package com.zxjk.duoduo.ui.minepage.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxjk.duoduo.R;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.utils.AesUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BackupWordsActivity extends BaseActivity {

    private String words;
    private ImageView ivTop;
    private TextView tvWord;
    private TextView tvBackuptips;
    private Button btnNext;
    private RecyclerView recycler;
    private BaseQuickAdapter adapter;
    private ArrayList<String> wordsList = new ArrayList<>(12);

    private List<String> corrcetIndexList = new ArrayList<>(12);
    private int currentStep = -1;
    private ArrayList currentSelectedIndex = new ArrayList(12);

    private boolean correctFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_words);

        words = getIntent().getStringExtra("words");

        TextView title = findViewById(R.id.tv_title);
        title.setText(R.string.backup_words);
        findViewById(R.id.rl_back).setOnClickListener(v -> finish());

        ivTop = findViewById(R.id.ivTop);
        tvWord = findViewById(R.id.tvWord);
        tvBackuptips = findViewById(R.id.tvBackuptips);
        btnNext = findViewById(R.id.btnNext);
        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new GridLayoutManager(this, 3));

        if (!TextUtils.isEmpty(words)) {
            String s = AesUtil.getInstance().decrypt(words);
            String[] split = s.split(",");
            Collections.addAll(wordsList, split);
        }

        adapter = new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_backupwords, wordsList) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                if (currentStep == -1) {
                    helper.setText(R.id.tvIndex, helper.getAdapterPosition() + 1 + "")
                            .setText(R.id.tvWord, item);
                } else {
                    helper.setText(R.id.tvWord, helper.getAdapterPosition() + 1 + "")
                            .setText(R.id.tvIndex, "");
                }
                if (!currentSelectedIndex.contains(helper.getAdapterPosition())) {
                    helper.setBackgroundRes(R.id.rlroot, R.color.colorTheme);
                } else {
                    helper.setBackgroundRes(R.id.rlroot, R.color.backupwordUnable);
                }
            }
        };

        recycler.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            if (currentStep == -1 || currentStep == 12 || currentSelectedIndex.contains(position)) {
                return;
            }

            currentSelectedIndex.add(position);
            adapter.notifyItemChanged(position);

            if (currentStep != 11) {
                tvWord.setText("#" + (currentStep + 2) + "  " + adapter.getData().get(Integer.parseInt(corrcetIndexList.get(currentStep + 1))));
                if (!adapter.getData().get(Integer.parseInt(corrcetIndexList.get(currentStep))).equals(adapter.getData().get(position))) {
                    correctFlag = false;
                }
            }
            currentStep += 1;

        });
    }

    public void next(View view) {
        if (currentStep == -1) {
            currentStep += 1;
            correctFlag = true;
            currentSelectedIndex.clear();

            gennerateArray();
            ivTop.setVisibility(View.INVISIBLE);
            tvWord.setVisibility(View.VISIBLE);
            btnNext.setText(R.string.accomplish);
            tvBackuptips.setText(R.string.backup_words_tips3);
            tvWord.setText("#" + (currentStep + 1) + "  " + adapter.getData().get(Integer.parseInt(corrcetIndexList.get(currentStep))));
            adapter.notifyDataSetChanged();
            return;
        }

        //校验
        if (currentStep != 12) {
            ToastUtils.showShort(R.string.backup_words_tips2);
            return;
        }

        if (!correctFlag) {
            ToastUtils.showShort(R.string.backup_words_tips4);
            currentStep = -1;
            btnNext.setText(R.string.next);
            next(null);
        } else {
            ToastUtils.showShort(R.string.backup_words_tips5);
            finish();
        }
    }

    public void gennerateArray() {
        String[] index = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};
        corrcetIndexList = Arrays.asList(index);
        Collections.shuffle(corrcetIndexList);
    }
}
