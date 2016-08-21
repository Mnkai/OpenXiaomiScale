package moe.minori.openxiaomiscale.objects;

import moe.minori.openxiaomiscale.BuildConfig;

/**
 * Android Log wrapper
 * Created by minori on 16. 8. 20.
 */
public class Log
{
	public static void d (String name, String content)
	{
		if (BuildConfig.DEBUG)
		{
			android.util.Log.d(name, content);
		}
		else
		{
			// Quieten
		}
	}
}
