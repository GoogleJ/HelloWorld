package com.zxjk.moneyspace.ui.msgpage;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.zxjk.moneyspace.R;
import com.zxjk.moneyspace.ui.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SelectContactForGroupCardActivity extends BaseActivity {
    RecyclerView card_recycler_view;
    EditText search_select_contact;
    @BindView(R.id.tv_title)
    TextView tvTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact_for_group_card);
        ButterKnife.bind(this);
        initUI();
    }

    private void initUI() {
        tvTitle.setText(getString(R.string.select_group));
        card_recycler_view = findViewById(R.id.card_recycler_view);
        search_select_contact = findViewById(R.id.search_select_contact);

    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
