package com.dottmed.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.dingbei.diagnose.utils.DiagnoseUtil;


public class MainActivity extends AppCompatActivity {

    private EditText mEd_id;
    private View mTxStart;
    private EditText mEd_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEd_id = findViewById(R.id.ed_id);
        mEd_id.setText("440513199405192410");
        mEd_name = findViewById(R.id.ed_name);
        mEd_name.setText("xxx");

        mTxStart = findViewById(R.id.tx_start);
        mTxStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiagnoseUtil.diagnose(MainActivity.this, MyApplication.APP_KEY,
                        "", mEd_name.getText().toString(), mEd_id.getText().toString(), null);
            }
        });

        mTxStart.setOnFocusChangeListener(this::changeFocus);

        mEd_id.setOnFocusChangeListener(this::changeFocus);

    }

    public void changeFocus(View view, boolean hasFocus){
        if (hasFocus) {
            view.animate().scaleX(1.05f).scaleY(1.2f).setDuration(300).start();
        } else {
            view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        int action = event.getAction();
        return handleKeyEvent(action, keyCode)||super.dispatchKeyEvent(event);
    }

    private boolean handleKeyEvent(int action, int keyCode) {
        if (action != KeyEvent.ACTION_DOWN) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
              	//确定键enter
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_DPAD_UP:
                //向上键
                //向下键
                if (mTxStart.isFocused()) {
                    mEd_id.requestFocus();
                } else if (mEd_id.isFocusable()){
                    mTxStart.requestFocus();
                } else {
                    mTxStart.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            	//向左键
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            	//向右键
                break;
            default:
                break;
        }
        return false;
    }

}
