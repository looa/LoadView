package org.looa.loadview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.looa.widget.LoadView;


public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView ivAppLogo;
    private TextView tvAppName;
    private LoadView loadView;

    private Button btStart;
    private Button btStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivAppLogo = (ImageView) findViewById(R.id.iv_app_logo);
        tvAppName = (TextView) findViewById(R.id.tv_app_name);
        loadView = (LoadView) findViewById(R.id.load);

        btStart = (Button) findViewById(R.id.bt_start);
        btStop = (Button) findViewById(R.id.bt_stop);

        btStart.setOnClickListener(this);
        btStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btStart) {
            ivAppLogo.setVisibility(View.VISIBLE);
            tvAppName.setVisibility(View.VISIBLE);
            loadView.show();
        } else if (v == btStop) {
            ivAppLogo.setVisibility(View.GONE);
            tvAppName.setVisibility(View.GONE);
            loadView.hide();
        }
    }
}
