package com.zxjk.duoduo.ui.base;

public abstract class BaseLazyFragment extends BaseFragment {
    private boolean isFirstLoad = true;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            loadData();
            isFirstLoad = false;
        }
    }
    
    public void loadData() {

    }

}
