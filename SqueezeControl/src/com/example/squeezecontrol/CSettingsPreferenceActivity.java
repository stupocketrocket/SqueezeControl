package com.example.squeezecontrol;

import com.example.squeezecontrol.R;

import android.app.Activity;
import android.os.Bundle;


public class CSettingsPreferenceActivity extends Activity 
{

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		 super.onCreate(savedInstanceState);
		 getFragmentManager().beginTransaction().replace(android.R.id.content,
	                new CPrefsFragment()).commit();
	}

}
