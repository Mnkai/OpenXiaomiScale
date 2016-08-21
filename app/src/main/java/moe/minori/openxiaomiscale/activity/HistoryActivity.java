package moe.minori.openxiaomiscale.activity;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;

import moe.minori.openxiaomiscale.R;
import moe.minori.openxiaomiscale.objects.Database;
import moe.minori.openxiaomiscale.objects.Log;
import moe.minori.openxiaomiscale.objects.WeightDBElement;

public class HistoryActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Database database = new Database(this);

		ArrayList<WeightDBElement> list = database.getElements(-1, -1, -1);

		Log.d("HistoryActivity", "Foo");
	}
}
