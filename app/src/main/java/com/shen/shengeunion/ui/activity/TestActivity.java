package com.shen.shengeunion.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.shen.shengeunion.R;
import com.shen.shengeunion.ui.custom.TextFlowLayout;
import com.shen.shengeunion.utils.LogUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.text_flow_layout)
    TextFlowLayout textFlowLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

//        JsonCacheUtils jsonCacheUtils = JsonCacheUtils.getInstance();
//        Histories his = jsonCacheUtils.getVal("key_histories", Histories.class);
//        List<String> histories = his.getHistories();
        textFlowLayout.setItemViews(new ArrayList<String>(){
            {
                add("无糖可乐");
                add("有糖可乐");
                add("哈哈哈哈哈哈哈哈哈哈或或或或或或");
                add("嘻嘻嘻嘻嘻嘻嘻");
                add("小蛋蛋");
                add("无糖可乐");
                add("有糖可乐");
                add("哈哈哈哈哈哈哈哈哈哈或或或或或或");
                add("嘻嘻嘻嘻嘻嘻嘻");
                add("小蛋蛋");
            }
        });
        textFlowLayout.setOnItemClick(new TextFlowLayout.OnItemClick() {
            @Override
            public void onFlowClick(String tag) {
                LogUtils.d(TestActivity.this, "tag" + tag + "被点击了...");
            }
        });
    }


}