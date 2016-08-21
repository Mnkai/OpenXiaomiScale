package moe.minori.openxiaomiscale.objects;

/**
 * Created by minori on 16. 8. 21.
 */
public class WeightDBElement
{
	final long unixTime;
	final float weight;
	final int weightUnit;

	final int userID;

	public WeightDBElement(Weight w, int uid)
	{
		unixTime = System.currentTimeMillis() / 1000L;
		weight = w.weight();
		weightUnit = w.getMeasureSystem();

		userID = uid;
	}

	public WeightDBElement(long uT, float w, int wU, int uid)
	{
		unixTime = uT;
		weight = w;
		weightUnit = wU;

		userID = uid;
	}
}
