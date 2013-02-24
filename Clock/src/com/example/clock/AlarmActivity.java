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
	private Button stopButton; // 停止程序 包括停止响铃和震动
	public MediaPlayer mediaPlayer; // 播放闹钟铃声
	private Vibrator vibrator; // 震动
	private static final String MUSIC_PATH = new String("/sdcard/"); // 铃声路径
	private PowerManager.WakeLock wl; // 控制屏幕亮度
	private KeyguardLock kl; // 控制锁屏幕
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
		startVibrate(vibrator); // 开始震动
		playMusic(MUSIC_PATH + "onepiece.mp3"); // 开始响铃
	}

	void unlockScreen() {
		KeyguardManager km = (KeyguardManager) getSystemService(Service.KEYGUARD_SERVICE);
		kl = km.newKeyguardLock("unLock");
		kl.disableKeyguard();
		//禁止锁屏
		//getWindow().setFlags( WindowManager.LayoutParams.TYPE_KEYGUARD, WindowManager.LayoutParams.TYPE_KEYGUARD);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);// 获取电源管理器对象
		wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.FULL_WAKE_LOCK, "bright");
		// 获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是LogCat里用的Tag
		wl.acquire();// 点亮屏幕
		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		for(int i=1;i<=5;i++)
		am.adjustVolume(AudioManager.ADJUST_RAISE, 1);
		
		// wl.release();// 释放
	}

	void startVibrate(Vibrator vibrator) { // 震动频率设置
		long[] pattern = new long[] { 1000, 1000, 1000 };
		vibrator.vibrate(pattern, 0);
	}

	class StopButtonListener implements OnClickListener { // 停止程序
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
			System.out.println("返回成功！");
			if (succes) {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.reset(); // 停止播放
				}
				vibrator.cancel(); // 停止震动
				wl.release(); // 关闭屏幕
				kl.reenableKeyguard(); // 锁屏
				finish(); // 结束程序
			}

		}
	}

	private void playMusic(String path) { // 播放铃声设置
		try {
			mediaPlayer.reset(); // 重置
			mediaPlayer.setDataSource(path); // 播放文件的路径
			mediaPlayer.prepare(); // 准备
			mediaPlayer.start(); // 开始播放
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() { // 一首播放完成后的操作
						public void onCompletion(MediaPlayer arg0) {
							playMusic(MUSIC_PATH + "onepiece.mp3"); // 单曲循环
						}
					});
		} catch (IOException e) {
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) { // 屏蔽实体键
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
	public void onAttachedToWindow() { // 屏蔽Home键
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

}
