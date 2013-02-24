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

	void showQuestion() {				//չʾ���⣬ÿ�����������λ���ӷ���������ȷ�𰸴���answer
		int a = (int) (Math.random() * 100) + 1;
		int b = (int) (Math.random() * 100) + 1;
		questionTextView.setText("        " + a + "+" + b + "=       ");
		answer = a + b;
	}

	boolean isInteger(String value) { // �ж��ַ����Ƿ�Ϊ����
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
			if (hisAnswer == null || hisAnswer.isEmpty()) { // �������Ƿ�Ϊ��
				Toast.makeText(ProblemActivity.this, "�������Ϊ��~", 2000).show();
			} else if (!isInteger(hisAnswer)) { // �������Ƿ�Ϊ����
				Toast.makeText(ProblemActivity.this, "ֻ������������", 2000).show();
			} else {

				int result = Integer.parseInt(hisAnswer);
				if (result == answer) {						//�����ȷ������������棬�����ز���answer ֵΪtrue
					Intent intent = new Intent();
					intent.putExtra("done", true);
					setResult(RESULT_CODE, intent);
					finish();
				} else {
					Toast.makeText(ProblemActivity.this, "����� T^T ����!", 2000)
							.show();
					showQuestion();	//���³���
					answerEditText.selectAll();
				}
			}
		}
	}

	class BackButtonListener implements OnClickListener {	//�������⣬��������ҳ�棬�����ز���answer ֵΪfalse
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("done", false);
			setResult(RESULT_CODE, intent);
			finish();
		}
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) { // ����ʵ���
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

	public void onAttachedToWindow() { // ����Home��
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
		super.onAttachedToWindow();
	}
}
