package moe.minori.openxiaomiscale;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import moe.minori.openxiaomiscale.objects.BMI;
import moe.minori.openxiaomiscale.objects.Log;
import moe.minori.openxiaomiscale.objects.Weight;

/**
 * Created by minori on 16. 8. 20.
 */
public class BTUtils
{
	private static BluetoothAdapter bluetoothAdapter = null;
	private static Handler mHandler = null;

	private static BluetoothGatt bluetoothGatt;
	private static BluetoothGattCharacteristic weightCharacteristic;


	private static BluetoothAdapter.LeScanCallback scanCallback = null;
	private static BluetoothGattCallback gattCallback = null;

	private static void prepareBLECallback(final Activity activity)
	{
		// Prepare callback
		scanCallback = new BluetoothAdapter.LeScanCallback()
		{
			@Override
			public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
			{
				if (device.getAddress().replace(":", "").startsWith("880f10") ||
						device.getAddress().replace(":", "").startsWith("880F10")) // Xiaomi
				{
					//TODO: Actual device GATT information matching instead of naive device name
					if (device.getName().equals("MI_SCALE")) // It really is scale
					{
						// Register device in the shared preference
						//TODO: Deal with multiple scale, maybe signal strength threshold?

						startGatt(device, activity);
						startStopBLEScanning(activity, false);

						// Register GATT listener to receive weight event from scale device
						Log.d("MainActivity", "Scale found, starting Gatt connection");
					}
				}
			}
		};
	}

	private static void prepareBTAdapter(final Activity activity)
	{
		// Initializes Bluetooth adapter.
		BluetoothManager bluetoothManager =
				(BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);

		bluetoothAdapter = bluetoothManager.getAdapter();
	}

	private static void prepareGATTCallback(final Activity activity)
	{
		gattCallback = new BluetoothGattCallback()
		{
			@Override
			public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState)
			{
				super.onConnectionStateChange(gatt, status, newState);
				Log.d("GattCallback", "onConnectionStateChange");
				if (newState == BluetoothProfile.STATE_CONNECTED)
				{
					Log.d("GattCallback", "Gatt connected, attempting discovery...");
					gatt.discoverServices();
				}
				else if (newState == BluetoothProfile.STATE_DISCONNECTED)
				{
					Log.d("GattCallback", "Gatt disconnected");

					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							TextView scaleNameTextView = (TextView) activity.findViewById(R.id.scaleNameText);
							scaleNameTextView.setText(R.string.scaleDisconnectedString);

							TextView scaleStatusTextView = (TextView) activity.findViewById(R.id.scaleStatusText);
							scaleStatusTextView.setText(R.string.stringDisconnectedStatusString);

							Button scanStartButton = (Button) activity.findViewById(R.id.scanStartButton);
							scanStartButton.setVisibility(View.VISIBLE);
						}
					});

					stopGatt();
					startStopBLEScanning(activity, false);
				}
			}

			@Override
			public void onServicesDiscovered(final BluetoothGatt gatt, int status)
			{
				super.onServicesDiscovered(gatt, status);
				Log.d("GattCallback", "onServicesDiscovered");
				ArrayList<BluetoothGattService> serviceList = (ArrayList) gatt.getServices();

				for (BluetoothGattService one : serviceList)
				{
					if (one != null)
					{
						ArrayList<BluetoothGattCharacteristic> characteristicsList = (ArrayList) one.getCharacteristics();
						for (BluetoothGattCharacteristic two : characteristicsList)
						{
							if (two.getUuid().toString().startsWith("00002a9d"))
							{
								// Weight param discovered

								activity.runOnUiThread(new Runnable()
								{
									@Override
									public void run()
									{
										TextView scaleNameTextView = (TextView) activity.findViewById(R.id.scaleNameText);
										scaleNameTextView.setText(gatt.getDevice().getName() + "(" + gatt.getDevice().getAddress() + ")");

										TextView scaleStatusTextView = (TextView) activity.findViewById(R.id.scaleStatusText);
										scaleStatusTextView.setText(R.string.scaleFoundStatusString);
									}
								});

								weightCharacteristic = two;

								// Start constant notification
								bluetoothGatt.setCharacteristicNotification(weightCharacteristic, true);

								ArrayList<BluetoothGattDescriptor> descriptors = (ArrayList<BluetoothGattDescriptor>) weightCharacteristic.getDescriptors();

								if ( descriptors.size() == 1 )
								{
									descriptors.get(0).setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
									bluetoothGatt.writeDescriptor(descriptors.get(0));
								}
								else
								{
									//TODO: Deal with multiple descriptors
									Log.d("GattCallback", "Multiple descriptors found in weight characteristic, unhandled exception!");
									System.exit(-1);
								}
							}
						}

					}
				}

			}

			@Override
			public void onCharacteristicWrite(BluetoothGatt gatt,
											  BluetoothGattCharacteristic characteristic,
											  int status)
			{
				super.onCharacteristicWrite(gatt, characteristic, status);
				Log.d("GattCallback", "onCharacteristicWrite");

			}

			@Override
			public void onDescriptorRead(BluetoothGatt gatt,
										 BluetoothGattDescriptor descriptor,
										 int status)
			{
				super.onDescriptorRead(gatt, descriptor, status);
				Log.d("GattCallback", "onDescriptorRead");

			}

			@Override
			public void onDescriptorWrite(BluetoothGatt gatt,
										  BluetoothGattDescriptor descriptor,
										  int status)
			{
				super.onDescriptorWrite(gatt, descriptor, status);
				Log.d("GattCallback", "onDescriptorWrite");



			}

			@Override
			public void onReliableWriteCompleted(BluetoothGatt gatt, int status)
			{
				super.onReliableWriteCompleted(gatt, status);
				Log.d("GattCallback", "onReliableWriteCompleted");

			}

			@Override
			public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status)
			{
				super.onReadRemoteRssi(gatt, rssi, status);
				Log.d("GattCallback", "onReadRemoteRssi");

			}

			@Override
			public void onMtuChanged(BluetoothGatt gatt, int mtu, int status)
			{
				super.onMtuChanged(gatt, mtu, status);
				Log.d("GattCallback", "onMtuChanged");

			}

			@Override
			public void onCharacteristicRead(BluetoothGatt gatt,
											 BluetoothGattCharacteristic characteristic,
											 int status)
			{
				super.onCharacteristicRead(gatt, characteristic, status);
				Log.d("GattCallback", "onCharacteristicRead");
			}

			@Override
			public void onCharacteristicChanged(BluetoothGatt gatt,
												BluetoothGattCharacteristic characteristic)
			{
				super.onCharacteristicChanged(gatt, characteristic);
				Log.d("GattCallback", "onCharacteristicChanged");
				//TODO: Read weight data and parse to UI

				final Weight weight = new Weight (characteristic.getValue());

				Log.d("GattCallback", "Weight data: " + weight.weight());
				Log.d("GattCallback", "IsStabilized: " + weight.isStabilized());
				Log.d("GattCallback", "IsWeightRemoved: " + weight.isWeightRemoved());

				activity.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						TextView weightTextView = (TextView) activity.findViewById(R.id.scaleWeightText);
						TextView measureUnitTextView = (TextView) activity.findViewById(R.id.measurementUnitText);
						TextView stabilizedTextView = (TextView) activity.findViewById(R.id.scaleStabilizationText);
						TextView unloadWeightTextView = (TextView) activity.findViewById(R.id.unloadWeightText);

						TextView bmiValueTextView = (TextView) activity.findViewById(R.id.bmiValueText);
						TextView bmiInformationTextView = (TextView) activity.findViewById(R.id.bmiInformationText);

						weightTextView.setText(weight.weight() + "");

						if ( weight.getMeasureSystem() == Weight.CATTY )
							measureUnitTextView.setText(R.string.measurementUnitCattyString);
						else if ( weight.getMeasureSystem() == Weight.LBS )
							measureUnitTextView.setText(R.string.measurementUnitLbsString);
						else if ( weight.getMeasureSystem() == Weight.KG )
							measureUnitTextView.setText(R.string.measurementUnitKgString);

						if ( weight.isStabilized() )
							stabilizedTextView.setText(R.string.stabilizedString);
						else
							stabilizedTextView.setText("");

						if ( weight.isWeightRemoved() )
							unloadWeightTextView.setText(R.string.weightRemovedString);
						else
							unloadWeightTextView.setText("");

						SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(activity);

						float bmi = new BMI(
								Float.parseFloat(preference.getString("settingsHeight", "100")),
								preference.getBoolean("settingsBMIImperial", false),
								weight.weight(),
								weight.getMeasureSystem()).getBMI();

						bmiValueTextView.setText(bmi + "");

						if ( bmi < 18.5 )
						{
							bmiInformationTextView.setText("Underweight");
						}
						else if ( bmi < 25 )
						{
							bmiInformationTextView.setText("Normal weight");

						}
						else if ( bmi < 30)
						{
							bmiInformationTextView.setText("Overweight");

						}
						else if ( bmi < 40 )
						{
							bmiInformationTextView.setText("Obese");

						}
						else
						{
							bmiInformationTextView.setText("Extremely Obese");

						}
					}
				});

			}
		};
	}

	/**
	 * Starts and stops BLE scanning from device
	 * Returns without changing mScanning value when BT is not on.
	 *
	 * @param activity activity of application to use handler
	 * @param enable   If true, start scanning for 10 seconds
	 *                 If false, stop scanning.
	 *                 Scanning will stop automatically after 10 seconds without explicit function call
	 */
	public static void startStopBLEScanning(final Activity activity, final boolean enable)
	{
		final int REQUEST_ENABLE_BT = 2;

		prepareBTAdapter(activity);

		if ( mHandler == null )
			mHandler = new Handler();

		if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
		{
			Log.d("StartStopBLEScanning", "BT not on");
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return;
		}

		if (scanCallback == null)
		{
			Log.d("StartStopBLEScanning", "No callback method, making one...");
			prepareBLECallback(activity);
		}


		if (enable)
		{
			mHandler.postDelayed(new Runnable()
			{
				@Override
				public void run()
				{
					Log.d("StartStopBLEScanning", "Scan expired because of time");
					bluetoothAdapter.stopLeScan(scanCallback);

					activity.runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							TextView scaleNameTextView = (TextView) activity.findViewById(R.id.scaleNameText);
							scaleNameTextView.setText(R.string.scaleNotFoundString);

							TextView scaleStatusTextView = (TextView) activity.findViewById(R.id.scaleStatusText);
							scaleStatusTextView.setText(R.string.scaleNotFoundStatusString);

							Button scanStartButton = (Button) activity.findViewById(R.id.scanStartButton);
							scanStartButton.setVisibility(View.VISIBLE);
						}
					});

				}
			}, Long.parseLong(PreferenceManager.getDefaultSharedPreferences(activity).getString("settingsScanMaxDuration", "10000")));

			bluetoothAdapter.startLeScan(scanCallback);

		}
		else
		{
			Log.d("StartStopBLEScanning", "Scan exited gracefully");
			mHandler.removeCallbacksAndMessages(null);
			bluetoothAdapter.stopLeScan(scanCallback);
		}

	}

	public static void startGatt(BluetoothDevice device, Activity activity)
	{
		if (bluetoothAdapter == null)
		{
			Log.d("StartGatt", "BT adapter variable not initialized, this should not happen, but working around...");
			prepareBTAdapter(activity);
		}


		if (gattCallback == null)
		{
			Log.d("StartGatt", "No callback method, making one...");
			prepareGATTCallback(activity);
		}


		Log.d("StartGatt", "Starting Gatt...");
		bluetoothGatt = device.connectGatt(activity, false, gattCallback);
	}

	public static void stopGatt()
	{
		Log.d("StopGatt", "Gatt stopped");
		if (bluetoothGatt == null)
		{
			return;
		}
		bluetoothGatt.close();
		bluetoothGatt = null;

	}
}
