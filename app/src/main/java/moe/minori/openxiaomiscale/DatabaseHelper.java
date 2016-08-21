package moe.minori.openxiaomiscale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import moe.minori.openxiaomiscale.objects.Database;

/**
 * Created by minori on 16. 8. 21.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
	// If you change the database schema, you must increment the database version.
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_NAME = "weight.db";

	public DatabaseHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase)
	{
		sqLiteDatabase.execSQL(Database.SQL_CREATE_ENTRIES);

	}


	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
	{
		//TODO: In case of database schema structure update, increment database version, implement upgrade and downgrade method
	}
}

