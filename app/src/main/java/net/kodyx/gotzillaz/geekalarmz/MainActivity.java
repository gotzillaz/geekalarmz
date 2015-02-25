package net.kodyx.gotzillaz.geekalarmz;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, SwitchCompat.OnCheckedChangeListener{

    private TextView mTimeTextView;
    private SwitchCompat mEnableSwitch;
    private CheckBox mVibrateCheckBox;
    private Context mContext;
    private TimePickerDialog mTimePickerDialog;
    private MediaPlayer mMediaPlayer;
    public int hour = 0;
    public int minute = 0;

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDaySet, int minuteSet) {
            hour = hourOfDaySet;
            minute = minuteSet;
            mTimeTextView.setText(String.format("%02d:%02d", hour, minute));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mTimeTextView = (TextView) findViewById(R.id.text_time);
        mEnableSwitch = (SwitchCompat) findViewById(R.id.switch_enable);
        mVibrateCheckBox = (CheckBox) findViewById(R.id.checkbox_vibrate);

        mMediaPlayer = MediaPlayer.create(this, R.raw.massive_war_alarm);
        mMediaPlayer.setLooping(true);

        mTimeTextView.setOnClickListener(this);
        mEnableSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.text_time) {
            DialogFragment newFragment = new TimePickerFragment(mTimeSetListener);
            newFragment.show(getFragmentManager(), "timePicker");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView.getId() == R.id.switch_enable) {
            Toast.makeText(this, "SWITCH "+isChecked, Toast.LENGTH_SHORT).show();
            if(isChecked) {
                mMediaPlayer.start();
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);
            }
            else {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            }
        }
    }

    public static class TimePickerFragment extends DialogFragment {
        TimePickerDialog.OnTimeSetListener listener;

        public TimePickerFragment(TimePickerDialog.OnTimeSetListener listener) {
            this.listener = listener;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this.listener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }
}
