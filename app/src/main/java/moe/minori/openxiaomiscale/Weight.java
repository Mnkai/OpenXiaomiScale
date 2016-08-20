package moe.minori.openxiaomiscale;

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
