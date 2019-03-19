package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by Roger on 4/10/2017.
 */

public class TTSReceiver extends BroadcastReceiver {
    private TextToSpeech tts;
    private Set<Voice> allVoices;
    private String messageBodyText;
    private TextToSpeech.OnInitListener ttsListener;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("MainActivity", "onReceive() fired!");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        int volumeLevel = 0;
        volumeLevel = preferences.getInt("volumeLevel",3);
        Log.d("MainActivity", "volumeLevel : " + String.valueOf(volumeLevel));
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(am.STREAM_MUSIC, volumeLevel, 0);

        messageBodyText =  intent.getStringExtra("MESSAGE_BODY");

        if (ttsListener == null) {
            ttsListener = new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // tts.speak(messageBodyText, TextToSpeech.QUEUE_ADD, null, "1st1");
                }
            };
        }

        if (tts == null) {
            tts = new TextToSpeech(context, ttsListener);
        }

        tts.speak(messageBodyText, TextToSpeech.QUEUE_ADD, null, "2nd1");

    }

}
