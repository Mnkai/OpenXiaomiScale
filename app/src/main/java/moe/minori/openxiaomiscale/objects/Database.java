package moe.minori.openxiaomiscale.objects;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;

/**
 * Describes database structure
 * Created by minori on 16. 8. 21.
 */
public class Database
{
	//private String DB_PATH = "/data/data/moe.minori.openxiaomiscale/databases/";
	private String DB_PATH;

	//Table name
	final public String WEIGHT_TABLE_NAME = "WEIGHT";
	final public String USER_TABLE_NAME = "USERS";

	final public String DATABASE_NAME = "openxiaomiscale.db";

	//Create table
	final public String CREATE_WEIGHT_TABLE_QUERY =
			"CREATE TABLE " + WEIGHT_TABLE_NAME + " (" +
					"UNIXTIME INTEGER PRIMARY KEY," +
					"WEIGHTVALUE REAL NOT NULL," +
					"WEIGHTUNIT INTEGER NOT NULL," +
					"USERID INTEGER," +
					"FOREIGN KEY(USERID) REFERENCES " + USER_TABLE_NAME + "(ID)" +
					")";

	final public String CREATE_USER_TABLE_QUERY =
			"CREATE TABLE " + USER_TABLE_NAME + " (" +
					"ID INTEGER PRIMARY KEY AUTOINCREMENT," +
					"USERNAME TEXT" +
					")";

	Activity activity = null;

	public Database(Activity a)
	{
		Log.d("Database", "Database constructor called!");
		activity = a;
		// Check if database file already exists

		DB_PATH = activity.getFilesDir().getPath() + "/" + DATABASE_NAME;

		if (!existsDatabase())
		{
			createDatabaseAndInitialize();
		}
	}

	public void insertElement (WeightDBElement element)
	{
		Log.d("Database", "Insert element called!");

		SQLiteDatabase database = null;

		try
		{
			database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

			database.setForeignKeyConstraintsEnabled(true);

			database.execSQL(
					"INSERT INTO " + WEIGHT_TABLE_NAME + " VALUES (" +
							element.unixTime + "," +
							element.weight + "," +
							element.weightUnit + "," +
							element.userID + ")"
			);
		}
		catch (SQLiteException e)
		{
			// Unhandled exception!
			Log.d("Database", "Exception in inserting weight element in database");
			e.printStackTrace();
			System.exit(-1);
		}
		finally
		{
			if ( database != null )
				database.close();
		}
	}

	/**
	 * Retrieves WeightDBElements from database, and returns ArrayList
	 *
	 * @param unixTimeFrom If -1, no limit low
	 * @param unixTimeUntil If -1, no limit high
	 * @param userId If -1, all users
	 * @return
	 */
	public ArrayList<WeightDBElement> getElements (long unixTimeFrom, long unixTimeUntil, int userId)
	{
		ArrayList<WeightDBElement> toReturn = new ArrayList<>();
		SQLiteDatabase database = null;

		try
		{
			database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);

			StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append("SELECT * FROM ");
			stringBuilder.append(WEIGHT_TABLE_NAME);

			if ( unixTimeFrom != -1 && unixTimeUntil == -1 ) // only low limit
			{
				stringBuilder.append(" WHERE ");

				stringBuilder.append("UNIXTIME > ");
				stringBuilder.append(unixTimeFrom);

				if ( userId != -1 )
				{
					stringBuilder.append(" AND ");

					stringBuilder.append("USERID = ");
					stringBuilder.append(userId);
				}

			}
			else if ( unixTimeFrom == -1 && unixTimeUntil != -1 ) // only high limit
			{
				stringBuilder.append(" WHERE ");

				stringBuilder.append("UNIXTIME < ");
				stringBuilder.append(unixTimeUntil);

				if ( userId != -1 )
				{
					stringBuilder.append(" AND ");

					stringBuilder.append("USERID = ");
					stringBuilder.append(userId);
				}
			}
			else if ( unixTimeFrom == -1 && unixTimeUntil == -1 ) // no limit in time
			{
				if ( userId != -1 )
				{
					stringBuilder.append(" WHERE ");

					stringBuilder.append("USERID = ");
					stringBuilder.append(userId);
				}
			}
			else if ( unixTimeFrom != -1 && unixTimeUntil != -1 ) // limit in time
			{
				stringBuilder.append(" WHERE ");

				stringBuilder.append("UNIXTIME > ");
				stringBuilder.append(unixTimeFrom);

				stringBuilder.append(" AND ");

				stringBuilder.append("UNIXTIME < ");
				stringBuilder.append(unixTimeUntil);

				if ( userId != -1 )
				{
					stringBuilder.append(" AND ");

					stringBuilder.append("USERID = ");
					stringBuilder.append(userId);
				}
			}

			Log.d("Database", "Query built: " + stringBuilder.toString());

			Cursor cursor = database.rawQuery(stringBuilder.toString(), null);

			cursor.moveToNext();

			while ( !cursor.isLast() )
			{
				long tempTime;
				float tempWeight;
				int tempWeightUnit;
				int tempUserId;

				tempTime = cursor.getLong(0);
				tempWeight = cursor.getFloat(1);
				tempWeightUnit = cursor.getInt(2);
				tempUserId = cursor.getInt(3);

				WeightDBElement tempElement = new WeightDBElement(tempTime, tempWeight, tempWeightUnit, tempUserId);

				toReturn.add(tempElement);

				cursor.moveToNext();
			}
		}
		catch (SQLiteException e)
		{
			Log.d("Database", "Exception in getElements!");
			e.printStackTrace();
			System.exit(-1);
		}
		finally
		{
			if ( database != null )
				database.close();
		}

		return toReturn;
	}

	private boolean existsDatabase()
	{
		SQLiteDatabase database = null;
		boolean isExist = true;

		try
		{
			database = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		}
		catch (SQLiteCantOpenDatabaseException e)
		{
			// No database
			Log.d("Database", "Database not found!");
			isExist = false;
		}
		finally
		{
			if ( database != null )
				database.close();
		}

		return isExist;
	}

	private void createDatabaseAndInitialize()
	{
		Log.d("Database", "Database initializing...");

		SQLiteDatabase database = null;

		try
		{
			database = activity.openOrCreateDatabase(DB_PATH, Context.MODE_PRIVATE, null);

			database.setForeignKeyConstraintsEnabled(true);

			// Drop previous tables
			database.execSQL("DROP TABLE IF EXISTS " + WEIGHT_TABLE_NAME);
			database.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);

			// Create new tables
			database.execSQL(CREATE_USER_TABLE_QUERY);
			database.execSQL(CREATE_WEIGHT_TABLE_QUERY);

			// Add default user to users table, it will have id 1
			database.execSQL("INSERT INTO " + USER_TABLE_NAME + " VALUES (1,'Default')");
		}
		catch (SQLiteException e)
		{
			// Unhandled exception!
			Log.d("Database", "Exception in createDatabaseAndInitialize!");
			e.printStackTrace();
			System.exit(-1);
		}
		finally
		{
			Log.d("Database", "Database initialize finished!");

			if ( database != null )
				database.close();
		}

	}

}
