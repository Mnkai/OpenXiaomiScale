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
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
		TextView scaleNameTextView = (TextView) findViewById(R.id.scaleNameText);
		scaleNameTextView.setText(R.string.scaleSearchingString);

		TextView scaleStatusTextView = (TextView) findViewById(R.id.scaleStatusText);
		scaleStatusTextView.setText(R.string.scaleSearchingStatusString);

		Log.d("MainActivity", "Starting scan...");
		BTUtils.startStopBLEScanning(this, true);

		// BMI layout
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);

		boolean isBmiEnabled = preference.getBoolean("settingsBMICalculation", false);

		TextView bmiLabelTextView = (TextView) findViewById(R.id.bmiLabelText);
		TextView bmiValueTextView = (TextView) findViewById(R.id.bmiValueText);
		TextView bmiInformationTextView = (TextView) findViewById(R.id.bmiInformationText);


		if ( isBmiEnabled )
		{
			bmiValueTextView.setText("");
			bmiInformationTextView.setText("");

			bmiLabelTextView.setVisibility(View.VISIBLE);
			bmiValueTextView.setVisibility(View.VISIBLE);
			bmiInformationTextView.setVisibility(View.VISIBLE);
		}
		else
		{
			bmiValueTextView.setText("");
			bmiInformationTextView.setText("");

			bmiLabelTextView.setVisibility(View.GONE);
			bmiValueTextView.setVisibility(View.GONE);
			bmiInformationTextView.setVisibility(View.GONE);
		}



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

			TextView scaleNameTextView = (TextView) findViewById(R.id.scaleNameText);
			scaleNameTextView.setText(R.string.scaleSearchingString);

			TextView scaleStatusTextView = (TextView) findViewById(R.id.scaleStatusText);
			scaleStatusTextView.setText(R.string.scaleSearchingStatusString);


			BTUtils.startStopBLEScanning(this, true);

			Button scanStartButton = (Button) findViewById(R.id.scanStartButton);
			scanStartButton.setVisibility(View.GONE);
		}
	}
}
