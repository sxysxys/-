package com.shen.shengeunion.presenter.impl;

import com.shen.shengeunion.model.Api;
import com.shen.shengeunion.model.TicketParams;
import com.shen.shengeunion.model.domain.TicketResult;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.RetrofitManager;
import com.shen.shengeunion.utils.UrlUtils;
import com.shen.shengeunion.view.ITicketPagerCallBack;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {

    private List<ITicketPagerCallBack> callBacks = new ArrayList<>();

    private static final TicketPresenterImpl instance = new TicketPresenterImpl();
    private String cover;
    private TicketResult ticketResult;

    enum LoadState {
        LOADING, ERROR, NONE, SUCCESS;
    }

    private LoadState curState = LoadState.NONE;

    private TicketPresenterImpl(){}

    public static ITicketPresenter getInstance() {
        return instance;
    }

    @Override
    public void getTicket(String title, String url, String cover) {
        onTicketLoading();
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        String targetUrl = UrlUtils.getTicketUrl(url);
        Api api = retrofit.create(Api.class);
        Call<TicketResult> task= api.getTicket(new TicketParams(targetUrl, title));
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(TicketPresenterImpl.this, "code -> " + code);
                if (code == HttpsURLConnection.HTTP_OK) {
                    ticketResult = response.body();
                    TicketPresenterImpl.this.cover = cover;
//                    LogUtils.d(TicketPresenterImpl.this, "result -> " + ticketResult);
                    onLoadSuccess();
                } else {
                    // 请求失败
                    requestError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                requestError();
            }
        });

    }

    private void onLoadSuccess() {
        this.curState = LoadState.SUCCESS;
        for (ITicketPagerCallBack callBack : callBacks) {
            callBack.onTicketLoaded(TicketPresenterImpl.this.cover, ticketResult);
        }
    }

    private void requestError() {
        this.curState = LoadState.ERROR;
        for (ITicketPagerCallBack callBack : callBacks) {
            callBack.onNetError();
        }
    }

    /**
     * 注册，由于和网络传输是异步的关系，所以如果有必要的话需要再请求一次拿数据。
     * @param callback
     */
    @Override
    public void registerCallback(ITicketPagerCallBack callback) {
        callBacks.add(callback);
        if (curState != LoadState.NONE) {
            if (curState == LoadState.SUCCESS) {
                onLoadSuccess();
            } else if (curState == LoadState.ERROR) {
                requestError();
            } else {
                onTicketLoading();
            }
        }
    }

    private void onTicketLoading() {
        curState = LoadState.LOADING;
        for (ITicketPagerCallBack callBack : callBacks) {
            callBack.Loading();
        }
    }

    @Override
    public void unregisterCallback(ITicketPagerCallBack callback) {
        if (callBacks.contains(callback)) {
            callBacks.remove(callback);
        }
    }
}
