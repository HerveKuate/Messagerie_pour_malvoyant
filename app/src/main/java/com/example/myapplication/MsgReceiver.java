package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MsgReceiver extends BroadcastReceiver {

    String  MESSAGE_BODY = "MESSAGE_BODY";
    LocalBroadcastManager lbm;
    TTSReceiver ttsReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (lbm == null && ttsReceiver == null) {
            lbm = LocalBroadcastManager.getInstance(context);
            ttsReceiver = new TTSReceiver();
            lbm.registerReceiver(ttsReceiver,new IntentFilter("us.raddev.vext.message"));
        }

        SmsMessage message = null;
        String from = null;

        message = GetMessage(intent);
        from = message.getOriginatingAddress();

        if (message == null){
            message = GetMessage(intent);
        }
        Log.d("MainActivity", "got SMS success!");
        from = message.getOriginatingAddress();
        Log.d("MainActivity", "from : " + from);
        String body = message.getMessageBody();
        Log.d("MainActivity", "message body : " + body);
//        long receivedDate = message.getTimestampMillis();
//        Date date = new Date(receivedDate);
//        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm:ss");
//        String formattedDate = formatter.format(date);

        ///Resolving the contact name from the contacts.
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(from));
        Cursor c = context.getContentResolver().query(lookupUri, new String[]{ContactsContract.Data.DISPLAY_NAME},null,null,null);
        try {
            Intent ttsIntent = new Intent("us.raddev.vext.message");
            boolean isSuccess = c.moveToFirst();
            if (isSuccess) {
                // got a contact name
                String displayName = c.getString(0);
                String ContactName = displayName;
                Log.d("MainActivity", "ContactName : " + ContactName);
                ttsIntent.putExtra(MESSAGE_BODY,"Incoming from " + ContactName );
            }
            else{
                //doesn't have name in contacts
                ttsIntent.putExtra(MESSAGE_BODY,"Incoming message");
            }
            lbm.sendBroadcast(ttsIntent);
            lbm.unregisterReceiver(ttsReceiver);
        }
        catch (Exception e) {
            // TODO: handle exception
        }finally{
            c.close();
        }

        Intent ttsIntent = new Intent("us.raddev.vext.message");
        ttsIntent.putExtra(MESSAGE_BODY,body );
        lbm.sendBroadcast(ttsIntent);
        lbm.unregisterReceiver(ttsReceiver);

        Log.d("MainActivity", "from : " + from);
        Log.d("MainActivity", "body : " + body);
        //Log.d("MainActivity", "receivedDate : " + formattedDate);

    }

    private SmsMessage GetMessage(Intent intent)
    {
        SmsMessage message = null;
        if (Build.VERSION.SDK_INT >= 19) {
            Log.d("MainActivity", "I'm a NEWER API LEVEL : " + Build.VERSION.SDK_INT);
            SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            Log.d("MainActivity", "got SMS message");
            message = msgs[0];
            Log.d("MainActivity", msgs[0].toString());
        } else {
            Log.d("MainActivity", "I'm an older API LEVEL : " + Build.VERSION.SDK_INT);
            Bundle bundle = intent.getExtras();
            Object pdus[] = (Object[]) bundle.get("pdus");
            message = SmsMessage.createFromPdu((byte[]) pdus[0]);
        }
        return message;
    }
}

