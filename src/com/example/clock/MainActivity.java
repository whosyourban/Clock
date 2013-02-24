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
	private Button timeButton; // ��������ʱ��
	private Button startButton; // ��������
	private Button cancelButton; // ȡ������
	private TextView alarmTimeTextView; // ��ʾ����ʱ��
	private TextView isSetTextView; // ��ʾ�����ǿ������ǹر�
	private boolean isSet = false; // �ж������ǿ������ǹر�
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

		readSQLite(); // ��ȡ���ݿ��д洢��ʱ��
		isSetTextView.setText("       " + (isSet ? "����" : "�ر�"));
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
			setAlarmClockTime(hour,minutes);//����ʱ��
			upgrade(hour, minutes); // ��������ʱ��
		}
	}

	void upgrade(int hour, int minutes) // ��������ʱ��
	{
		String period = "����";
		if (hour > 12) {
			hour -= 12;
			period = "����";
		}
		alarmTimeTextView.setText("����ʱ�䣺" + hour + ":" + minutes + "  "
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

	class TimeButtonListener implements OnClickListener { // �����ť������ʱ��ѡ���
		public void onClick(View v) {
			showDialog(TIME_PICKER_ID);
		}
	}

	/**
	 * @author Ban �õ���TimePickerDiaolog���õ�ʱ�� ��������setAlarmClockTime��������
	 */
	TimePickerDialog.OnTimeSetListener onTimeListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setAlarmClockTime(hourOfDay, minute);
		}
	};

	/**
	 * @author Ban ����ʱ����������
	 */
	void setAlarmClockTime(int hourOfDay, int minute) {
		manager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
		c = Calendar.getInstance();
		intent = new Intent(MainActivity.this, AlarmActivity.class);
		pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
		c.setTimeInMillis(System.currentTimeMillis());

		// c.set(Calendar.DAY_OF_WEEK,Calendar.SATURDAY);
		c.set(Calendar.HOUR_OF_DAY, hourOfDay); // �����û�ѡ��ʱ��������Calendar����
		c.set(Calendar.MINUTE, minute);
		c.set(Calendar.SECOND, 0); // �˴���������õĻ�������ʱ�侫ȷ����
		/*
		 * if (c.getTimeInMillis() <= System.currentTimeMillis()) {
		 * System.out.println("��ʱ"); c.set(Calendar.MINUTE, minute + 1); }
		 */
		writeSQLite(hourOfDay, minute);
		upgrade(hourOfDay, minute);
	}

	class StartButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				// ����AlarmManager����Calendar��Ӧ��ʱ������ָ�����
				manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pi); //
				// ��ʾ���óɹ���ʾ��Ϣ
				isSet = true;
				isSetTextView.setText("       ����");
				Toast.makeText(
						MainActivity.this,
						"���������廹��: "
								+ (c.getTimeInMillis() - System
										.currentTimeMillis()) / 1000 + " ��",
						5000).show();
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, "������������ʱ��", 3000).show();
			}
		}

	}

	class CancelButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			try {
				manager.cancel(pi);
				isSet = false;
				isSetTextView.setText("       �ر�");
				Toast.makeText(MainActivity.this, "������ȡ��", 5000).show();
			}catch (Exception e) {
				Toast.makeText(MainActivity.this, "û����������", 3000).show();
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
