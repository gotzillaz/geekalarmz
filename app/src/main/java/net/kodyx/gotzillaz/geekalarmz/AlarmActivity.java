package net.kodyx.gotzillaz.geekalarmz;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AlarmActivity extends ActionBarActivity {

    private Button mSubmitButton;
    private EditText mAnswerEditText;
    private TextView mTimeTextView;
    private String weekDay;
    private int hour;
    private int minute;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler;
    private Runnable mVibrateRun;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        mSubmitButton = (Button) findViewById(R.id.button_submit);
        mAnswerEditText = (EditText) findViewById(R.id.edit_text_answer);
        mTimeTextView = (TextView) findViewById(R.id.text_time);

        mSharedPreferences = getSharedPreferences("alarm", Context.MODE_PRIVATE);

        hour = mSharedPreferences.getInt("hour", 0);
        minute = mSharedPreferences.getInt("minute", 0);
        mTimeTextView.setText(String.format("%02d:%02d", hour, minute));

        mMediaPlayer = MediaPlayer.create(this, R.raw.massive_war_alarm);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

        mContext = getApplicationContext();

        mHandler = new Handler();

        mVibrateRun = new Runnable() {
            @Override
            public void run() {
                Vibrator v = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(1000);
                mHandler.postDelayed(mVibrateRun, 2000);
            }
        };

        if(getIntent().getBooleanExtra("isVibrate", false)) {
            mVibrateRun.run();
        }

        NotificationManager mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);;
        Intent notiIntent = new Intent(this, AlarmActivity.class);
        PendingIntent notiPendingIntent = PendingIntent.getActivity(this, 0, notiIntent, 0);
        Notification.Builder notiBuilder = new Notification.Builder(this);
        notiBuilder.setSmallIcon(R.drawable.ic_alarm_white_48dp)
                .setContentTitle("Open GeekAlarmZ now !!")
                .setContentText(String.format("%02d:%02d", hour, minute))
                .setContentIntent(notiPendingIntent);
        Notification notification = notiBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;;
        mNotiManager.notify(0, notification);

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        getSupportActionBar().hide();

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");

        Calendar calendar = Calendar.getInstance();
        weekDay = dayFormat.format(calendar.getTime());

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerEditText.getText().toString().toLowerCase().equals(weekDay.toLowerCase())) {
                    Toast.makeText(AlarmActivity.this, "Correct Answer !!", Toast.LENGTH_SHORT).show();
                    mMediaPlayer.pause();
                    mHandler.removeCallbacks(mVibrateRun);
                    finish();
                }
                else {
                    Toast.makeText(AlarmActivity.this, "Incorrect Answer !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
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
}
