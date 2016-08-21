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
import android.widget.ShareActionProvider;

import moe.minori.openxiaomiscale.BTUtils;
import moe.minori.openxiaomiscale.R;
import moe.minori.openxiaomiscale.UIUtils;
import moe.minori.openxiaomiscale.objects.Database;
import moe.minori.openxiaomiscale.objects.Log;

public class MainActivity extends Activity
{
	private ShareActionProvider mShareActionProvider;
	public static boolean isShareIconEnabled = false;

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem item = menu.findItem(R.id.menu_item_share);

		if (isShareIconEnabled) {
			item.setEnabled(true);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		} else {
			// disabled
			item.setEnabled(false);
			item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);

		// Locate MenuItem with ShareActionProvider
		MenuItem item = menu.findItem(R.id.menu_item_share);

		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		return true;
	}

	// Call to update the share intent
	public void setShareIntent(Intent shareIntent)
	{
		if (mShareActionProvider != null)
		{
			mShareActionProvider.setShareIntent(shareIntent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{

		if ( item.getItemId() == R.id.menu_settings)
		{
			startActivity(new Intent(this, SettingsActivity.class));
		}
		else if ( item.getItemId() == R.id.menu_history)
		{
			startActivity(new Intent(this, HistoryActivity.class));
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
		final int PERMISSION_REQUEST_CODE = 1;

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.permissionRequiredString);
		builder.setMessage(R.string.permissionRequiredMessageString);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.setOnDismissListener(new DialogInterface.OnDismissListener()
		{
			public void onDismiss(DialogInterface dialog)
			{
				requestPermissions(
						new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
						PERMISSION_REQUEST_CODE);
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
