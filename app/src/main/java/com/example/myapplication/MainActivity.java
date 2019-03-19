package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    Button button;

    EditText editText, editText2;

    Button speakButton;

    SeekBar volumeSeekBar;
    String  MESSAGE_BODY = "MESSAGE_BODY";
    LocalBroadcastManager lbm;
    TTSReceiver ttsReceiver;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestAllPermissions();

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity .this, Manifest.permission.SEND_SMS))
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.SEND_SMS}, 1);
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.SEND_SMS}, 1);
            }
        }
        else
        {
            //do nothing
        }
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        speakButton = (Button)findViewById(R.id.speakButton);
        volumeSeekBar = (SeekBar)findViewById(R.id.volumeSeekBar);



        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        Log.d("MainActivity", "max vol : " + String.valueOf(maxVol));
        volumeSeekBar.setMax(maxVol);
        //int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        setCurrentVolumeLevel();
        lbm = LocalBroadcastManager.getInstance(this);
        ttsReceiver = new TTSReceiver();

        lbm.registerReceiver(ttsReceiver,new IntentFilter("com.example.myapplication.message"));
        Intent intent = new Intent("com.example.myapplication.message");
        intent.putExtra(MESSAGE_BODY,"" );
        lbm.sendBroadcast(intent);

        speakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String outText = editText2.getText().toString();
                if (outText != null) {
                    Intent intent = new Intent("com.example.myapplication.message");
                    intent.putExtra(MESSAGE_BODY,outText );
                    lbm.sendBroadcast(intent);
                    Log.d("MainActivity", "Button click ended!");
                }
            }
        });

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), String.valueOf(progress),Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                Log.d("MainActivity", "progress : " + String.valueOf(progress));
                editor.putInt("volumeLevel",progress);
                editor.apply();
                editor.commit();
            }
        });

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String number = editText.getText().toString();
                String sms = editText2.getText().toString();

                try
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(number, null, sms , null, null);
                    Toast.makeText(MainActivity.this, "Send!", Toast.LENGTH_SHORT).show();
                }catch (Exception e)
                {
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode)
        {
            case 1:
            {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(this, "No Permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void setCurrentVolumeLevel() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int volumeLevel = 0;
        volumeLevel = preferences.getInt("volumeLevel", 3);
        volumeSeekBar.setProgress(volumeLevel);
    }

    private void requestAllPermissions() {
        String permission = Manifest.permission.RECEIVE_SMS;
        String permission2 = Manifest.permission.READ_CONTACTS;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if ( grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[2];
            permission_list[0] = permission;
            permission_list[1] = permission2;
            ActivityCompat.requestPermissions(this, permission_list, 2);
        }
    }

}
