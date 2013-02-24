package com.example.clock;

import java.io.IOException;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmActivity extends Activity {
	private Button stopButton; // ֹͣ���� ����ֹͣ�������
	public MediaPlayer mediaPlayer; // ������������
	private Vibrator vibrator; // ��
	private static final String MUSIC_PATH = new String("/sdcard/"); // ����·��
	private PowerManager.WakeLock wl; // ������Ļ����
	private KeyguardLock kl; // ��������Ļ
	private final static int REQUEST_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		stopButton = (Button) findViewById(R.id.stop);
		mediaPlayer = new MediaPlayer();
		stopButton.setOnClickListener(new StopButtonListener());
		vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

		unlockScreen();
		startVibrate(vibrator); // ��ʼ��
		playMusic(MUSIC_PATH + "onepiece.mp3"); // ��ʼ����
	}

	void unlockScreen() {
		KeyguardManager km = (KeyguardManager) getSystemService(Service.KEYGUARD_SERVICE);
		kl = km.newKeyguardLock("unLock");
		kl.disableKeyguard();
		//��ֹ����
		//getWindow().setFlags( WindowManager.LayoutParams.TYPE_KEYGUARD, WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);// ��ȡ��Դ����������
		wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.FULL_WAKE_LOCK, "bright");
		// ��ȡPowerManager.WakeLock���󣬺���Ĳ���|��ʾͬʱ��������ֵ��������LogCat���õ�Tag
		wl.acquire();// ������Ļ
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		for(int i=1;i<=5;i++)
		am.adjustVolume(AudioManager.ADJUST_RAISE, 1);
		
		// wl.release();// �ͷ�
	}

	void startVibrate(Vibrator vibrator) { // ��Ƶ������
		long[] pattern = new long[] { 1000, 1000, 1000 };
		vibrator.vibrate(pattern, 0);
	}

	class StopButtonListener implements OnClickListener { // ֹͣ����
		public void onClick(View v) {
			Intent intent = new Intent(AlarmActivity.this,
					ProblemActivity.class);
			startActivityForResult(intent, REQUEST_CODE);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE
				&& resultCode == ProblemActivity.RESULT_CODE) {
			Bundle bundle = data.getExtras();
			boolean succes = bundle.getBoolean("done");
			System.out.println("���سɹ���");
			if (succes) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.reset(); // ֹͣ����
				}
				vibrator.cancel(); // ֹͣ��
				wl.release(); // �ر���Ļ
				kl.reenableKeyguard(); // ����
				finish(); // ��������
			}

		}
	}

	private void playMusic(String path) { // ������������
		try {
			mediaPlayer.reset(); // ����
			mediaPlayer.setDataSource(path); // �����ļ���·��
			mediaPlayer.prepare(); // ׼��
			mediaPlayer.start(); // ��ʼ����
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() { // һ�ײ�����ɺ�Ĳ���
						public void onCompletion(MediaPlayer arg0) {
							playMusic(MUSIC_PATH + "onepiece.mp3"); // ����ѭ��
						}
					});
		} catch (IOException e) {
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) { // ����ʵ���
		switch (keyCode) {
		case KeyEvent.KEYCODE_HOME: ;
			return true;
		case KeyEvent.KEYCODE_BACK:
			return true;
		case KeyEvent.KEYCODE_CALL:
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			return true;
		 case KeyEvent.KEYCODE_SYM:
             return true;
         case KeyEvent.KEYCODE_STAR:
             return true;
         case KeyEvent.KEYCODE_POWER:
        	 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void onAttachedToWindow() { // ����Home��
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

}
