package moe.minori.openxiaomiscale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;

public class MainActivity extends Activity
{
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	private static final long SCAN_PERIOD = 10000;

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

		// When activity is resumed, check for SharedPref for previous scale mac
		SharedPreferences sharedPreferences = getSharedPreferences("default", MODE_PRIVATE);
		String scaleMac = sharedPreferences.getString("scaleMac", null);

		if ( scaleMac == null ) // Scale is not registered
		{
			//TODO: Start scale register activity. Scale register activity should register scale data in SharedPref.
		}
	}

	/**
	 * Starts and stops BLE scanning from device
	 *
	 * @param enable
	 *               If true, start scanning for 10 seconds
	 *               If false, stop scanning.
	 *               Scanning will stop automatically after 10 seconds without explicit function call
	 */
	private void startBLEScanning(final boolean enable)
	{
		if (enable)
		{
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);

		}
		else
		{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}


	}

	/**
	 * Executed when BLE device is detected
	 */
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
	{
		@Override
		public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
		{
			// TODO: Do something with scanned device
		}
	};
}
