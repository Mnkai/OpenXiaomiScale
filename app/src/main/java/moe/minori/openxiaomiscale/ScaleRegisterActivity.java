package moe.minori.openxiaomiscale;

import android.app.Activity;
import android.os.Bundle;

public class ScaleRegisterActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scale_register);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		Utils.startStopBLEScanning(this, true);
	}
}
