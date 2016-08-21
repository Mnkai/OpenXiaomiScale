package moe.minori.openxiaomiscale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import moe.minori.openxiaomiscale.objects.BMI;
import moe.minori.openxiaomiscale.objects.Weight;

/**
 * Created by minori on 16. 8. 21.
 */
public class UIUtils
{
	public static void updateUIWeightInformation (final Activity activity, final Weight weight, final SharedPreferences preference)
	{
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

	public static void updateUIScaleInformation (final Activity activity, final String scaleName, final String scaleInformation, final boolean isButtonEnabled)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				TextView scaleNameTextView = (TextView) activity.findViewById(R.id.scaleNameText);
				scaleNameTextView.setText(scaleName);

				TextView scaleStatusTextView = (TextView) activity.findViewById(R.id.scaleStatusText);
				scaleStatusTextView.setText(scaleInformation);

				Button scanStartButton = (Button) activity.findViewById(R.id.scanStartButton);

				if ( !isButtonEnabled )
				{
					scanStartButton.setVisibility(View.GONE);
				}
				else
				{
					scanStartButton.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	public static void bmiLayoutVisibility (final Activity activity, final SharedPreferences preference)
	{
		boolean isBmiEnabled = preference.getBoolean("settingsBMICalculation", false);

		TextView bmiLabelTextView = (TextView) activity.findViewById(R.id.bmiLabelText);
		TextView bmiValueTextView = (TextView) activity.findViewById(R.id.bmiValueText);
		TextView bmiInformationTextView = (TextView) activity.findViewById(R.id.bmiInformationText);


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
}
