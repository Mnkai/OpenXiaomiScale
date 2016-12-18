package moe.minori.openxiaomiscale.objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by minori on 16. 8. 20.
 */
public class Weight
{
	byte[] data;
	Bits firstBit;

	public static final int CATTY = 0;
	public static final int LBS = 1;
	public static final int KG = 2;

	public Weight(byte[] raw)
	{
		data = raw;

		firstBit = new Bits(data[0]);
	}

	public int getMeasureSystem()
	{
		if (firstBit.data[7])
			return LBS;
		else
		{
			if (firstBit.data[3])
				return CATTY;
			else
				return KG;
		}
	}

	public Date getDate()
	{
		Date dataTime = null;

		final int year = ((data[4] & 0xFF) << 8) | (data[3] & 0xFF);
		final int month = (int) data[5];
		final int day = (int) data[6];
		final int hours = (int) data[7];
		final int min = (int) data[8];
		final int sec = (int) data[9];

		String dateString = year + "/" + month + "/" + day + "/" + hours + "/" + min + "/" + sec;
		try {
			 dataTime = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss").parse(dateString);
		} catch (ParseException e) {
			Log.d("Weight", "Unable to parse date foramt: " + e.getMessage());
		}

		return dataTime;
	}

	public float weight()
	{
		StringBuilder result = new StringBuilder();

		result.append(Integer.toHexString(data[2] & 0xFF));
		result.append(Integer.toHexString(data[1] & 0xFF));

		Long parseResult = Long.parseLong(result.toString(), 16);

		if (getMeasureSystem() == CATTY || getMeasureSystem() == LBS)
			return (float) (parseResult / 100.0);
		else
			return (float) (parseResult / 200.0);
	}

	public boolean isStabilized()
	{
		if (firstBit.data[2])
			return true;
		else
			return false;
	}

	public boolean isWeightRemoved()
	{
		if (firstBit.data[0])
			return true;
		else
			return false;

	}
}
