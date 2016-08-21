package moe.minori.openxiaomiscale.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import moe.minori.openxiaomiscale.BTUtils;
import moe.minori.openxiaomiscale.R;
import moe.minori.openxiaomiscale.UIUtils;
import moe.minori.openxiaomiscale.objects.Log;

public class MainActivity extends Activity
{
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		if ( item.getItemId() == R.id.menu_settings)
		{
			startActivity(new Intent(this, SettingsActivity.class));
		}

		return super.onOptionsItemSelected(item);
	}

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



		Log.d("MainActivity", "Starting scan...");
		BTUtils.startStopBLEScanning(this, true);

		UIUtils.bmiLayoutVisibility(this, PreferenceManager.getDefaultSharedPreferences(this));
	}


	@TargetApi(Build.VERSION_CODES.M)
	private void androidMPermissionCheck()
	{
		final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.locationPermissionRequiredString);
		builder.setMessage(R.string.locationPermissionRequiredMessageString);
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
	protected void onPause()
	{
		super.onPause();
		BTUtils.startStopBLEScanning(this, false);
		BTUtils.stopGatt();
	}

	public void onClick (View v)
	{
		if ( v.getId() == R.id.scanStartButton )
		{
			Log.d("MainActivity", "Starting scan...");

			UIUtils.updateUIScaleInformation(this,
					getResources().getString(R.string.scaleSearchingString),
					getResources().getString(R.string.scaleSearchingStatusString),
					false);

			BTUtils.startStopBLEScanning(this, true);
		}
	}
}
