package com.example.drawer.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drawer.R;
import com.example.drawer.ui.utils.Utils;
import com.example.drawer.utils.RxBusTags;
import com.example.drawer.utils.ToastUtils;
import com.hwangjr.rxbus.RxBus;

public class HomeActivity extends BaseActivity {
    private static MediaPlayer mMediaPlayer;
    @Override
    protected void onStart() {
        super.onStart();
        setTitle("功能设置");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_home_view);
        mMediaPlayer=MainActivity.getmMediaPlayer();
        SettingFragment settingFragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, settingFragment).commit();
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingFragment extends PreferenceFragment
            implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
        private Preference mSoundEffect;
        private Preference mFilterSize;
        private Preference mFilterTime;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);

            mSoundEffect = findPreference(getString(R.string.setting_key_sound_effect));
            mFilterSize = findPreference(getString(R.string.setting_key_filter_size));
            mFilterTime = findPreference(getString(R.string.setting_key_filter_time));
            mSoundEffect.setOnPreferenceClickListener(this);
            mFilterSize.setOnPreferenceChangeListener(this);
            mFilterTime.setOnPreferenceChangeListener(this);

            mFilterSize.setSummary(getSummary(String.valueOf(20), R.array.filter_size_entries,
                    R.array.filter_size_entry_values));
            mFilterTime.setSummary(getSummary(String.valueOf(20), R.array.filter_time_entries,
                    R.array.filter_time_entry_values));
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == mSoundEffect) {
                startEqualizer();
                return true;
            }
            return false;
        }

        private void startEqualizer() {
            if (Utils.isAudioControlPanelAvailable(getActivity())) {
                Intent intent = new Intent();
                String packageName = getActivity().getPackageName();
                intent.setAction(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                intent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, packageName);
                intent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION,mMediaPlayer.getAudioSessionId());

                try {
                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                    ToastUtils.show(R.string.device_not_support);
                }
            } else {
                ToastUtils.show(R.string.device_not_support);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (preference == mFilterSize) {
                mFilterSize.setSummary(getSummary(String.valueOf(20), R.array.filter_size_entries,
                        R.array.filter_size_entry_values));
                RxBus.get().post(RxBusTags.SCAN_MUSIC, 1);
                return true;
            } else if (preference == mFilterTime) {
                mFilterTime.setSummary(getSummary(String.valueOf(20), R.array.filter_time_entries,
                        R.array.filter_time_entry_values));
                RxBus.get().post(RxBusTags.SCAN_MUSIC, 1);
                return true;
            }
            return false;
        }

        private String getSummary(String value, int entries, int entryValues) {
            String[] entryArray = getResources().getStringArray(entries);
            String[] entryValueArray = getResources().getStringArray(entryValues);
            for (int i = 0; i < entryValueArray.length; i++) {
                String v = entryValueArray[i];
                if (TextUtils.equals(v, value)) {
                    return entryArray[i];
                }
            }
            return entryArray[0];
        }
    }
}
