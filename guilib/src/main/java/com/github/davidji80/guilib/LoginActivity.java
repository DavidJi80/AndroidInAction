package com.github.davidji80.guilib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class LoginActivity  extends AppCompatActivity {
    private TopView topView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        topView = (TopView) findViewById(R.id.top_view);

        topView.setOnclickLeft(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginActivity.this, "点击了返回按钮", Toast.LENGTH_SHORT).show();
            }
        });
        topView.setRightTitle("设置");
        topView.setTitle("登录");
    }
}