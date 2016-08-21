package moe.minori.openxiaomiscale.objects;

import android.provider.BaseColumns;

/**
 * Describes database structure
 * Created by minori on 16. 8. 21.
 */
public class Database
{
	private static final String TEXT_TYPE = " TEXT";
	private static final String COMMA_SEP = ",";

	public static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + DatabaseEntry.TABLE_NAME + " (" +
					DatabaseEntry.COLUMN_NAME_UNIXTIME + " LONG PRIMARY KEY," +
					DatabaseEntry.COLUMN_NAME_WEIGHT_VALUE + TEXT_TYPE + COMMA_SEP +
					DatabaseEntry.COLUMN_NAME_WEIGHT_UNIT + ")";

	public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + DatabaseEntry.TABLE_NAME;

	public static abstract class DatabaseEntry implements BaseColumns
	{
		public static final String TABLE_NAME = "weight_history";

		public static final String COLUMN_NAME_WEIGHT_VALUE = "weight_value";
		public static final String COLUMN_NAME_WEIGHT_UNIT = "weight_unit";
		public static final String COLUMN_NAME_UNIXTIME = "unixtime";
	}

	public Database ()
	{
		// Does nothing
	}
}
