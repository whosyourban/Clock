package com.example.clock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;

	// 必须要有以下构造函数
	public DataBaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DataBaseHelper(Context context, String name, int version) {
		this(context, name, null, version);
		// TODO Auto-generated constructor stub
	}

	public DataBaseHelper(Context context, String name) {
		this(context, name, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		System.out.println("create a DataBase");
		db.execSQL("create table clock(id integer primary key autoincrement,hour int not null,minutes int not null,isSet boolean)");
		db.execSQL("insert into clock values(1,0,0,'false')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.println("update a DataBase");
	}
}
