package com.shen.shengeunion.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseFragment;
import com.shen.shengeunion.model.domain.SearchResult;
import com.shen.shengeunion.model.domain.Histories;
import com.shen.shengeunion.model.domain.RecommendResult;
import com.shen.shengeunion.presenter.ISearchPresenter;
import com.shen.shengeunion.presenter.ISkipInfo;
import com.shen.shengeunion.presenter.impl.SearchPresenter;
import com.shen.shengeunion.ui.adapter.LinearItemAdapter;
import com.shen.shengeunion.ui.custom.TextFlowLayout;
import com.shen.shengeunion.utils.KeyboardUtil;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.SizeUtils;
import com.shen.shengeunion.utils.SkipUtils;
import com.shen.shengeunion.utils.ToastUtils;
import com.shen.shengeunion.view.ISearchViewCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchViewCallBack, LinearItemAdapter.OnItemClickListener, TextFlowLayout.OnItemClick {


    private ISearchPresenter mPresenter;

    @BindView(R.id.search_history_view)
    TextFlowLayout historySearch;

    @BindView(R.id.hot_search)
    TextFlowLayout hotSearch;

    @BindView(R.id.histories_search_container)
    LinearLayout hisSearchContainer;

    @BindView(R.id.hot_search_container)
    LinearLayout hotSearchContainer;

    @BindView(R.id.del_image)
    ImageView delImage;

    @BindView(R.id.search_btn)
    TextView btnDel;

    @BindView(R.id.search_clean)
    ImageView searchClean;

    @BindView(R.id.search_box)
    EditText searchBox;

    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;

    @BindView(R.id.search_result_list)
    RecyclerView searchList;
    private LinearItemAdapter mSearchAdapter;

    @Override
    protected void onRetryClick() {
        super.onRetryClick();
        mPresenter.reSearch();
    }

    @Override
    protected View getLayout(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.search_fragment_layout, container, false);
    }

    @Override
    protected int getViewId() {
        return R.layout.search_fragment;
    }

    @Override
    protected void initView(View view) {
        searchList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchAdapter = new LinearItemAdapter();
        searchList.setAdapter(mSearchAdapter);
        searchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.right = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.left = SizeUtils.dip2px(getContext(), 2.5f);
            }
        });
        mRefreshLayout.setEnableRefresh(false);
        mRefreshLayout.setEnableOverScroll(true);
        mRefreshLayout.setEnableLoadmore(true);
    }

    @Override
    protected void initPresent() {
        super.initPresent();
        mPresenter = SearchPresenter.getInstance();
        mPresenter.registerCallback(this);
        // 获取关键词
//        mPresenter.doSearch("键盘");
        mPresenter.getHistories();
        mPresenter.getRecommend();
    }

    @Override
    protected void initListener() {
        super.initListener();
        historySearch.setOnItemClick(this);
        hotSearch.setOnItemClick(this);
        delImage.setOnClickListener(v -> {
            mPresenter.delHistories();
        });

        mSearchAdapter.setOnItemClickListener(this);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(this, "搜索页面加载更多中...");
                mPresenter.loadMore();
            }
        });
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String keyword = searchBox.getText().toString();
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mPresenter != null && !TextUtils.isEmpty(keyword)) {
                    LogUtils.d(this, "action -> " + actionId);
                    toSearch(keyword);
                }
                return false;
            }
        });

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchClean.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 点击就删除
        searchClean.setOnClickListener(v -> {
            switchToHot();
        });

        btnDel.setOnClickListener(v -> {
            switchToHot();
        });
    }

    /**
     * 点击叉或者取消进入这里
     */
    private void switchToHot() {
        searchBox.setText("");
        if (historySearch.getContentCount() > 0) {
            hisSearchContainer.setVisibility(View.VISIBLE);
        }
        if (hotSearch.getContentCount() > 0) {
            hotSearchContainer.setVisibility(View.VISIBLE);
        }
        mRefreshLayout.setVisibility(View.GONE);
        mPresenter.getHistories();
    }

    @Override
    protected void release() {
        if (mPresenter != null) {
            mPresenter.unregisterCallback(this);
        }
    }

    @Override
    public void onHistoryLoad(Histories histories) {
        if (histories == null || histories.getHistories() == null || histories.getHistories().size() == 0) {
            hisSearchContainer.setVisibility(View.GONE);
        } else {
            hisSearchContainer.setVisibility(View.VISIBLE);
        }
        historySearch.setItemViews((histories == null || histories.getHistories() == null) ? new ArrayList<>() : histories.getHistories());
    }

    @Override
    public void onHistoriesDel() {
        // 重新去加载历史记录
        mPresenter.getHistories();
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setCurState(State.SUCCESS);
        LogUtils.d(this, "result ->" + result);

        // 隐藏掉历史记录和推荐
        hisSearchContainer.setVisibility(View.GONE);
        hotSearchContainer.setVisibility(View.GONE);
        // 显示搜索界面
        mRefreshLayout.setVisibility(View.VISIBLE);
        // 设置数据
        mSearchAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());

        // 把键盘给弄下去
        KeyboardUtil.hide(getContext(), getView());
    }

    @Override
    public void onReSearchSuccess(SearchResult result) {

    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        // 此时拿到了加载更多的数据
        LogUtils.d(this, "result ->" + result);
        List<SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean> map_data = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchAdapter.addData(map_data);
        mRefreshLayout.finishLoadmore();
        ToastUtils.showToast("加载了" + map_data.size() + "条数据");
    }

    @Override
    public void onMoreLoadedError() {
        ToastUtils.showToast("网络错误，请重试...");
    }

    @Override
    public void onMoreLoadedEmpty() {
        ToastUtils.showToast("数据已经滑到底了...");
    }

    /**
     * 推荐回调
     * @param recommends
     */
    @Override
    public void onRecommendLoaded(List<RecommendResult.DataBean> recommends) {
//        LogUtils.d(this, "recommend -> " + recommends.get(0).getKeyword());
        if (recommends == null || recommends.size() == 0) {
            hotSearchContainer.setVisibility(View.GONE);
        } else {
            List<String> recommendWords = new ArrayList<>();
            for (RecommendResult.DataBean recommend : recommends) {
                recommendWords.add(recommend.getKeyword());
            }
            hotSearch.setItemViews(recommendWords);
            hotSearchContainer.setVisibility(View.VISIBLE);
        }
        setCurState(State.SUCCESS);
    }

    @Override
    public void onNetError() {
        setCurState(State.NET_ERROR);
    }

    @Override
    public void onEmpty() {
        setCurState(State.EMPTY);
    }

    @Override
    public void Loading() {
        setCurState(State.LOADING);
    }

    @Override
    public void onItemClick(ISkipInfo dataBean) {
        SkipUtils.skipPage(getContext(), dataBean);
    }

    @Override
    public void onFlowClick(String tag) {
        searchBox.setText(tag);
        if (mPresenter != null) {
            toSearch(tag);
        }
    }

    private void toSearch(String tag) {
        // 每次点击让其滑动到顶部，然后将editText的指针指向最前面。
        if (mPresenter != null) {
            searchList.scrollToPosition(0);
            searchBox.setFocusable(true);
            searchBox.requestFocus();
            searchBox.setSelection(tag.length(),tag.length());
            mPresenter.doSearch(tag);
        }
    }
}
