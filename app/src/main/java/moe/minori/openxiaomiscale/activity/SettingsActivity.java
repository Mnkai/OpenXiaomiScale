package moe.minori.openxiaomiscale.activity;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import moe.minori.openxiaomiscale.R;

public class SettingsActivity extends PreferenceActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.pref_general);
	}
}
