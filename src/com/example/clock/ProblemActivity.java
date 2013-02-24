package com.example.clock;

import com.example.clock.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ProblemActivity extends Activity {
	private Button submitButton;
	private Button backButton;
	private TextView questionTextView;
	private EditText answerEditText;
	final static int RESULT_CODE = 1;
	int answer = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		submitButton = (Button) findViewById(R.id.submit);
		backButton = (Button) findViewById(R.id.back);
		questionTextView = (TextView) findViewById(R.id.question);
		answerEditText = (EditText) findViewById(R.id.answer);

		submitButton.setOnClickListener(new SubmitButtonListener());
		backButton.setOnClickListener(new BackButtonListener());
		showQuestion();
	}

	void showQuestion() {				//展示问题，每次随机生成两位数加法，并把正确答案传至answer
		int a = (int) (Math.random() * 100) + 1;
		int b = (int) (Math.random() * 100) + 1;
		questionTextView.setText("        " + a + "+" + b + "=       ");
		answer = a + b;
	}

	boolean isInteger(String value) { // 判断字符串是否为整数
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	class SubmitButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			String hisAnswer = answerEditText.getText().toString();
			if (hisAnswer == null || hisAnswer.isEmpty()) { // 输入结果是否为空
				Toast.makeText(ProblemActivity.this, "结果不能为空~", 2000).show();
			} else if (!isInteger(hisAnswer)) { // 输入结果是否为整数
				Toast.makeText(ProblemActivity.this, "只能输入正整数", 2000).show();
			} else {

				int result = Integer.parseInt(hisAnswer);
				if (result == answer) {						//结果正确，返回响铃界面，并返回参数answer 值为true
					Intent intent = new Intent();
					intent.putExtra("done", true);
					setResult(RESULT_CODE, intent);
					finish();
				} else {
					Toast.makeText(ProblemActivity.this, "答错了 T^T 再来!", 2000)
							.show();
					showQuestion();	//重新出题
					answerEditText.selectAll();
				}
			}
		}
	}

	class BackButtonListener implements OnClickListener {	//放弃答题，返回响铃页面，并返回参数answer 值为false
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("done", false);
			setResult(RESULT_CODE, intent);
			finish();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) { // 屏蔽实体键
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			return true;
		case KeyEvent.KEYCODE_CALL:
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onAttachedToWindow() { // 屏蔽Home键
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
}
