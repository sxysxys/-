package com.shen.shengeunion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shen.shengeunion.R;
import com.shen.shengeunion.base.BaseActivity;
import com.shen.shengeunion.model.domain.TicketResult;
import com.shen.shengeunion.presenter.ITicketPresenter;
import com.shen.shengeunion.presenter.impl.TicketPresenterImpl;
import com.shen.shengeunion.ui.custom.LoadingView;
import com.shen.shengeunion.utils.LogUtils;
import com.shen.shengeunion.utils.ToastUtils;
import com.shen.shengeunion.utils.UrlUtils;
import com.shen.shengeunion.view.ITicketPagerCallBack;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallBack {

    private ITicketPresenter mTicketPresenter;

    @BindView(R.id.img_ticket)
    public ImageView ticketCode;

    @BindView(R.id.text_tao_edit)
    public EditText ticketEdit;

    @BindView(R.id.text_get_ticket)
    public TextView textButton;

    @BindView(R.id.ticket_return)
    public ImageView imageReturn;

    @BindView(R.id.loading_ticket_cover)
    public LoadingView loadingCover;

    @BindView(R.id.ticket_load_retry)
    public TextView textError;
    private boolean mHasTaoApp;


    @Override
    protected void initPresenter() {
        // 先通过单例拿到那个presenter
        mTicketPresenter = TicketPresenterImpl.getInstance();
        // 需要注意，有可能在注册之前数据已经返回了。
        mTicketPresenter.registerCallback(this);
        // 看手机是否安装淘宝应用
        PackageManager manager = getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaoApp = packageInfo != null;
            LogUtils.d(this, "packageInfo -> " + packageInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            mHasTaoApp = false;
            e.printStackTrace();
        }
        textButton.setText(mHasTaoApp ? "打开淘宝领劵" : "复制淘口令");
    }

    @Override
    protected void initListener() {
        this.imageReturn.setOnClickListener(v -> {
            // 直接将这个activity给返回
            finish();
        });

        this.textButton.setOnClickListener(v -> {
            // 复制到粘贴板
            String s = ticketEdit.getText().toString().trim();
            ClipboardManager systemService = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("shen_taobao_code", s);
            systemService.setPrimaryClip(clipData);

            // 看是否有淘宝应用，如果没有就提示复制成功，如果有就跳转
            if (mHasTaoApp) {
                Intent taoIntent = new Intent();
                ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                taoIntent.setComponent(componentName);
                startActivity(taoIntent);
            } else {
                ToastUtils.showToast("复制成功，请打开淘宝进行复制");
            }
        });
    }

    @Override
    protected int getResId() {
        return R.layout.activity_ticket;
    }

    /**
     * 当加载成功的时候回调
     * @param cover
     * @param ticketResult
     */
    @Override
    public void onTicketLoaded(String cover, TicketResult ticketResult) {
        if (ticketCode != null && !TextUtils.isEmpty(cover)) {
//            ViewGroup.LayoutParams params = ticketCode.getLayoutParams();
//            int width = params.width;
//            LogUtils.d(this,"width -> " + width);
//            String ticketUrl = UrlUtils.getCoverPath(cover,width);
            String ticketUrl = UrlUtils.getTicketUrl(cover);
            Glide.with(this).load(ticketUrl).into(ticketCode);
        }
        if (ticketResult != null && ticketResult.getData().getTbk_tpwd_create_response() != null) {
            this.ticketEdit.setText(ticketResult.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (loadingCover != null) {
            loadingCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetError() {
        if (loadingCover != null) {
            loadingCover.setVisibility(View.GONE);
        }
        if (textError != null) {
            textError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void Loading() {
        if (textError != null) {
            textError.setVisibility(View.GONE);
        }
        if (loadingCover != null) {
            loadingCover.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterCallback(this);
        }
    }
}
