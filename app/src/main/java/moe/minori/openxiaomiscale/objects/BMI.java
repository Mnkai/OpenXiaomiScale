package moe.minori.openxiaomiscale.objects;

/**
 * Created by minori on 16. 8. 20.
 */
public class BMI
{
	float metricHeightCentimeters;
	float metricWeight;

	public BMI (float height, boolean isImperialHeight, float weight, int weightUnitType)
	{
		if ( weightUnitType == Weight.CATTY )
			weight = weight * 2;
		else if ( weightUnitType == Weight.LBS )
			weight = (float) (weight * 0.45359237);

		if ( isImperialHeight )
		{
			// Assume input is feet
			height = (float) (height * 30.48);
		}

		metricHeightCentimeters = height;
		metricWeight = weight;
	}

	public float getBMI ()
	{
		return metricWeight / ((metricHeightCentimeters * metricHeightCentimeters) / 10000);
	}
}
