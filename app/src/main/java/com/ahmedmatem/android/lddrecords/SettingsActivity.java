package com.ahmedmatem.android.lddrecords;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_key_email_to")) {
            Preference emailToPreference = findPreference("pref_key_email_to");
            emailToPreference.setSummary(sharedPreferences.getString("pref_key_email_to",
                    getString(R.string.email_to_default)));
        }
        if (key.equals("pref_key_email_subject")) {
            Preference emailSubjectPreference = findPreference("pref_key_email_subject");
            emailSubjectPreference.setSummary(sharedPreferences.getString("pref_key_email_subject",
                    getString(R.string.email_subject_default)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
