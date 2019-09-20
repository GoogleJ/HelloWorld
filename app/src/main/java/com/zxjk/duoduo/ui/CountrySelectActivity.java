package com.zxjk.duoduo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zxjk.duoduo.R;
import com.zxjk.duoduo.bean.CountryEntity;
import com.zxjk.duoduo.ui.base.BaseActivity;
import com.zxjk.duoduo.ui.widget.LetterIndexView;
import com.zxjk.duoduo.ui.widget.SelectContryAdapter;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CountrySelectActivity extends BaseActivity {

    RecyclerView lv_list;
    ArrayList<CountryEntity> allCountryCodeList;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CountrySelectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_country_select);
        ButterKnife.bind(this);
        tvTitle.setText(getString(R.string.selectcountries));
        lv_list = findViewById(R.id.lv_list);
        initList();
        buildLitterIdx();
    }

    private void initList() {
        SelectContryAdapter selectContryAdapter = new SelectContryAdapter();
        selectContryAdapter.setOnClickListener(entity -> {
            Intent intent = new Intent();
            intent.putExtra("country", entity);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        lv_list.setLayoutManager(new LinearLayoutManager(this));
        lv_list.setAdapter(selectContryAdapter);
        allCountryCodeList = (ArrayList<CountryEntity>) getCountry();
        Collections.sort(allCountryCodeList, comparator);//排序
        selectContryAdapter.setData(allCountryCodeList);
    }

    private void buildLitterIdx() {
        LetterIndexView livIndex = findViewById(R.id.liv_index);
        livIndex.setNormalColor(getResources().getColor(R.color.contacts_letters_color));
        TextView litterHit = findViewById(R.id.tv_hit_letter);
        livIndex.setOnTouchingLetterChangedListener(new LetterIndexView.OnTouchingLetterChangedListener() {
            @Override
            public void onHit(String letter) {
                lv_list.scrollToPosition(getScrollPosition(letter));
                litterHit.setVisibility(View.VISIBLE);
                litterHit.setText(letter);
            }

            @Override
            public void onCancel() {
                litterHit.setVisibility(View.GONE);
            }
        });
    }

    private int getScrollPosition(String letter) {
        for (int i = 0; i < allCountryCodeList.size(); i++) {
            if (allCountryCodeList.get(i).pinyin.equals(letter)) {
                return i;
            }
        }
        return -1;
    }

    Comparator<CountryEntity> comparator = (o1, o2) -> o1.pinyin.compareTo(o2.pinyin);

    private List<CountryEntity> getCountry() {
        String[] country = getResources().getStringArray(R.array.country);
        String[] countryCode = getResources().getStringArray(R.array.country_code);
        List<CountryEntity> entities = new ArrayList<>(country.length);
        for (int i = 0; i < country.length; i++) {
            CountryEntity countryEntity = new CountryEntity();
            countryEntity.countryName = country[i];
            countryEntity.countryCode = countryCode[i];
            countryEntity.pinyin = PinYinUtils.converterToFirstSpell(countryEntity.countryName);
            entities.add(countryEntity);
        }
        return entities;
    }

    @OnClick(R.id.rl_back)
    public void onClick() {
        finish();
    }
}
