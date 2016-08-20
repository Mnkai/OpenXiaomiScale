package moe.minori.openxiaomiscale;

/**
 * Parses int (32bit) into 8 items of boolean
 * Created by minori on 16. 8. 20.
 */
public class Bits
{
	boolean [] data;

	int mask [] = {128, 64, 32, 16, 8, 4, 2, 1};

	public Bits(int raw)
	{
		data = new boolean[8];

		for (int i=0; i<8; i++)
		{
			if ( (mask[i] & raw) == mask[i] )
			{
				data[i] = true;
			}
			else
			{
				data[i] = false;
			}
		}
	}
}

