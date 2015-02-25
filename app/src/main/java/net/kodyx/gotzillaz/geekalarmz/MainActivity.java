package net.kodyx.gotzillaz.geekalarmz;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.format.DateFormat;
import android.util.Log;
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
    private AlarmManager mAlarmManager;
    private Intent intent;
    private PendingIntent pendingIntent;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private NotificationManager mNotiManager;
    public int hour = 0;
    public int minute = 0;

    TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDaySet, int minuteSet) {
            hour = hourOfDaySet;
            minute = minuteSet;
            mEditor = mSharedPreferences.edit();
            mEditor.putInt("hour", hourOfDaySet);
            mEditor.putInt("minute", minuteSet);
            mEditor.apply();
            mTimeTextView.setText(String.format("%02d:%02d", hour, minute));
        }
    };

    @Override
    protected  void onNewIntent(Intent it){
        super.onNewIntent(it);
        Log.d("enableStatus", "onNewIntent");
        boolean enableStatus = it.getBooleanExtra("isEnable", false);
        mEnableSwitch = (SwitchCompat) findViewById(R.id.switch_enable);
        mEnableSwitch.setChecked(enableStatus);
        Log.d("enableStatus", enableStatus+"");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();
        mTimeTextView = (TextView) findViewById(R.id.text_time);
        mEnableSwitch = (SwitchCompat) findViewById(R.id.switch_enable);
        mVibrateCheckBox = (CheckBox) findViewById(R.id.checkbox_vibrate);

//        mMediaPlayer = MediaPlayer.create(this, R.raw.massive_war_alarm);
//        mMediaPlayer.setLooping(true);

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlarmActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mSharedPreferences = getSharedPreferences("alarm", Context.MODE_PRIVATE);
        hour = mSharedPreferences.getInt("hour", 0);
        minute = mSharedPreferences.getInt("minute", 0);

        mTimeTextView.setText(String.format("%02d:%02d", hour, minute));
        mEnableSwitch.setChecked(getIntent().getBooleanExtra("isEnable",false));

        mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

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
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);

                mAlarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                Intent notiIntent = new Intent(this, MainActivity.class);
                notiIntent.putExtra("isEnable", true);
                PendingIntent notiPendingIntent = PendingIntent.getActivity(this, 1, notiIntent, 0);
                Notification.Builder notiBuilder = new Notification.Builder(this);
                notiBuilder.setSmallIcon(R.drawable.ic_alarm_white_48dp)
                    .setContentTitle("Upcoming GeekAlarmZ")
                    .setContentText(String.format("%02d:%02d", hour, minute))
                    .setContentIntent(notiPendingIntent);
                Notification notification = notiBuilder.build();
                notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;;
                mNotiManager.notify(0, notification);
//                mMediaPlayer.start();
//                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
//                // Vibrate for 500 milliseconds
//                v.vibrate(500);
            }
            else {
                mAlarmManager.cancel(pendingIntent);
                mNotiManager.cancel(0);
//                mMediaPlayer.pause();
//                mMediaPlayer.seekTo(0);
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
