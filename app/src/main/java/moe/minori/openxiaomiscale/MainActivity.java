package moe.minori.openxiaomiscale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

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

		// When activity is resumed, check for SharedPref for previous scale mac
		SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
		String scaleMac = sharedPreferences.getString("scaleMac", null);

		if (scaleMac == null) // Scale is not registered
		{
			Log.d("MainActivity", "Scale is not registered");
			Intent intent = new Intent(this, ScaleRegisterActivity.class);
			startActivity(intent);

		}
		else
		{
			// Register GATT listener to receive weight event from scale device
			Log.d("MainActivity", "Scale registered, start BLE scanning for GATT connection");

			Utils.startStopBLEScanning(this, true);
		}
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
}
