package com.example.clock;

import java.util.Calendar;

import com.example.clock.R;
import com.example.clock.DataBaseHelper;
import com.example.clock.MainActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	private AlarmManager manager;
	private final static int TIME_PICKER_ID = 1;
	private Button timeButton; // 设置闹铃时间
	private Button startButton; // 启动闹钟
	private Button cancelButton; // 取消闹铃
	private TextView alarmTimeTextView; // 显示闹铃时间
	private TextView isSetTextView; // 显示闹钟是开启还是关闭
	private boolean isSet = false; // 判断闹钟是开启还是关闭
	private PendingIntent pi;
	private Intent intent;
	private Calendar c;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		timeButton = (Button) findViewById(R.id.timebutton);
		startButton = (Button) findViewById(R.id.startbutton);
		cancelButton = (Button) findViewById(R.id.cancelbutton);
		alarmTimeTextView = (TextView) findViewById(R.id.alarmtime);
		isSetTextView = (TextView) findViewById(R.id.isset);

		readSQLite(); // 读取数据库中存储的时间
		isSetTextView.setText("       " + (isSet ? "开启" : "关闭"));
		timeButton.setOnClickListener(new TimeButtonListener());
		startButton.setOnClickListener(new StartButtonListener());
		cancelButton.setOnClickListener(new CancelButtonListener());

	}

	void readSQLite() {
		DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this,
				"alarmclock_db");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("clock", new String[] { "id", "hour",
				"minutes", "isSet" }, "id=?", new String[] { "1" }, null, null,
				null);
		int hour, minutes;
		if (cursor.moveToNext()) {
			hour = Integer.parseInt(cursor.getString((cursor
					.getColumnIndex("hour"))));
			minutes = Integer.parseInt(cursor.getString((cursor
					.getColumnIndex("minutes"))));
			setAlarmClockTime(hour,minutes);//设置时间
			upgrade(hour, minutes); // 更新闹铃时间
		}
	}

	void upgrade(int hour, int minutes) // 更新闹铃时间
	{
		String period = "上午";
		if (hour > 12) {
			hour -= 12;
			period = "下午";
		}
		alarmTimeTextView.setText("闹铃时间：" + hour + ":" + minutes + "  "
				+ period);
	}

	void writeSQLite(int hour, int minutes) {

		ContentValues values = new ContentValues();
		values.put("hour", hour);
		values.put("minutes", minutes);
		DataBaseHelper dbHelper = new DataBaseHelper(MainActivity.this,
				"alarmclock_db");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.update("clock", values, "id=?", new String[] { "1" });
		upgrade(hour, minutes);
		db.close();
	}

	class TimeButtonListener implements OnClickListener { // 点击按钮，弹出时间选择框
		public void onClick(View v) {
			showDialog(TIME_PICKER_ID);
		}
	}

	/**
	 * @author Ban 得到在TimePickerDiaolog设置的时间 ，并调用setAlarmClockTime设置闹钟
	 */
	TimePickerDialog.OnTimeSetListener onTimeListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setAlarmClockTime(hourOfDay, minute);
		}
	};

	/**
	 * @author Ban 根据时间设置闹钟
	 */
	void setAlarmClockTime(int hourOfDay, int minute) {
		manager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		c = Calendar.getInstance();
		intent = new Intent(MainActivity.this, AlarmActivity.class);
		pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
		c.setTimeInMillis(System.currentTimeMillis());

		// c.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay); // 根据用户选择时间来设置Calendar对象
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0); // 此处如果不设置的话，响铃时间精确至秒
		/*
		 * if (c.getTimeInMillis() <= System.currentTimeMillis()) {
		 * System.out.println("过时"); c.set(Calendar.MINUTE, minute + 1); }
		 */
		writeSQLite(hourOfDay, minute);
		upgrade(hourOfDay, minute);
	}

	class StartButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// 设置AlarmManager将在Calendar对应的时间启动指定组件
				manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi); //
				// 显示设置成功提示信息
				isSet = true;
				isSetTextView.setText("       开启");
				Toast.makeText(
						MainActivity.this,
						"距闹钟响铃还有: "
								+ (c.getTimeInMillis() - System
										.currentTimeMillis()) / 1000 + " 秒",
						5000).show();
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, "请先设置闹钟时间", 3000).show();
			}
		}

	}

	class CancelButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				manager.cancel(pi);
				isSet = false;
				isSetTextView.setText("       关闭");
				Toast.makeText(MainActivity.this, "闹钟已取消", 5000).show();
			}catch (Exception e) {
				Toast.makeText(MainActivity.this, "没有启动闹钟", 3000).show();
			}
		}
	}

	protected Dialog onCreateDialog(int id) {
		Calendar currentTime = Calendar.getInstance();
		return new TimePickerDialog(this, onTimeListener,
				currentTime.get(Calendar.HOUR_OF_DAY),
				currentTime.get(Calendar.MINUTE), false);
	}
}
