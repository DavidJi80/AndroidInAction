package com.github.davidji80.eventbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
    private Button mButton;
    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //注册事件
        EventBus.getDefault().register(this);
    }

    private void initView() {
        mButton = findViewById(R.id.btn1);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
            }
        });
        tv1 = findViewById(R.id.tv1);
        tv1.setText("I am tv1");
        tv2 = findViewById(R.id.tv2);
        tv2.setText("I am tv2");
        tv3 = findViewById(R.id.tv3);
        tv3.setText("I am tv3");
    }

    @Subscribe(threadMode = ThreadMode.MAIN,priority = 1)
    public void messageEvent1(MessageEvent messageEvent) {
        tv1.setText("MessageEvent1:"+messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEvent2(MessageEvent messageEvent) {
        tv2.setText("MessageEvent1:"+messageEvent.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void stringEvent(String message) {
        tv3.setText("String:"+message);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        EventBus.getDefault().unregister(this);
    }
}
