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
import com.zxjk.duoduo.ui.msgpage.widget.IndexView;
import com.zxjk.duoduo.ui.widget.SelectContryAdapter;
import com.zxjk.duoduo.utils.PinYinUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.OnClick;


public class CountrySelectActivity extends BaseActivity {

    private RecyclerView lv_list;
    private ArrayList<CountryEntity> allCountryCodeList;
    private TextView tvTitle;

    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, CountrySelectActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_country_select);
        tvTitle = findViewById(R.id.tv_title);
        lv_list = findViewById(R.id.lv_list);

        tvTitle.setText(getString(R.string.selectcountries));

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
        Collections.sort(allCountryCodeList, comparator);
        selectContryAdapter.setData(allCountryCodeList);
    }

    private void buildLitterIdx() {
        IndexView livIndex = findViewById(R.id.liv_index);
        TextView litterHit = findViewById(R.id.tv_hit_letter);

        livIndex.setShowTextDialog(litterHit);
        livIndex.setOnTouchingLetterChangedListener(letter -> {
            lv_list.scrollToPosition(getScrollPosition(letter));
            litterHit.setVisibility(View.VISIBLE);
            litterHit.setText(letter);
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
