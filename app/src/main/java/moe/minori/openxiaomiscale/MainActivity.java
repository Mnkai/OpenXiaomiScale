package moe.minori.openxiaomiscale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		// Android M Permission check
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			Log.d("MainActivity", "Is over Android M");
			if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
			{
				Log.d("MainActivity", "Permission not declared, trying to get permission...");
				androidMPermissionCheck();
				return;
			}
		}
		TextView scaleNameTextView = (TextView) findViewById(R.id.scaleNameText);
		scaleNameTextView.setText("(Searching...)");

		TextView scaleStatusTextView = (TextView) findViewById(R.id.scaleStatusText);
		scaleStatusTextView.setText("If not found, step up.");

		Log.d("MainActivity", "Starting scan...");
		Utils.startStopBLEScanning(this, true);

	}


	@TargetApi(Build.VERSION_CODES.M)
	private void androidMPermissionCheck()
	{
		final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Permission required");
		builder.setMessage("Starting from Android M, location permission is required to access BLE scan data from background.");
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			public void onDismiss(DialogInterface dialog)
			{
				requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
			}

		});
		builder.show();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Utils.startStopBLEScanning(this, false);
		Utils.stopGatt();
	}
}
