package com.example.akash.textingai;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.lang.UCharacter;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    TextView stateView;
    BroadcastReceiver broadcastReceiver;
    public static String phoneNumber;
    Handler handler = new Handler();
    SmsManager smsManager = SmsManager.getDefault();
    int state =0;
    //Ai responses
    String [] greetingState = {"Hello, we offer various classes","Hi, want to join a class","Hey, we got a lot of classes"};
    String [] questionState = {"Do you think you deserve to come to class?", "Are you qualified for the class?", "Do you want to come to the class?"};
    String [] answeringState = {"Congratulations, You have been accepted!", "I am sorry, your are not selected", "Don't worry, you are placed on the waiting list"};
    String [] noState = {"Would you like to join any other class?", "Ok, Goodbye"};
    String [] finalState = {"Your seat in a class is confirmed, I hope to see you in class tomorrow", "Unfortunately you wont be able to come to class because of your bad behavior", "You are not selected, Good luck next year"};
    String [] byeState = {"Thank you for your time", "See you next time", "Good Luck"};
    String [] confusedState = {"I don't know what you are talking about", "I am unable to comprehend you", "You are mean", "Please clarify"};


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(broadcastReceiver, intentFilter);
        Log.d("registerran","registerran");



    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.textView2);
        stateView = (TextView)findViewById(R.id.textView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 0);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 0);
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle myBundle = intent.getExtras();
                SmsMessage [] messages = null;

                if (myBundle != null)
                {
                    Object [] pdus = (Object[]) myBundle.get("pdus");

                    messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < messages.length; i++)
                    {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = myBundle.getString("format");
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        }
                        else {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        String Message = messages[i].getMessageBody();
                        phoneNumber = messages[i].getOriginatingAddress();
                        textView.setText(Message);
                        runAI(Message);
                    }
                }
            }
        };

    }
    public void sendText(final String message,int time){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                smsManager.sendTextMessage(phoneNumber,null,message,null,null);
            }
        }, time);
    }

    public void runAI(String message){
        int random = (int)(Math.random()*3);
        if((message.equals("Hi")||message.equals("Hello")||message.equals("Hey"))&& state >=0) {
            if (random == 0){
                sendText(greetingState[0],5000);
            }
            else if (random == 1){
                sendText(greetingState[1],5000);
            }
            else{
                sendText(greetingState[2],5000);
            }
            state++;
            stateView.setText("Greeting State");
        }
        else if ((message.equals("I want to come to a class")||message.equals("I am interested in a class")||message.equals("Yes, I want to join")||message.equals("I want to join a class"))&&state>0){
            int random4 = (int)(Math.random()*3);
            if (random4 == 0){
                sendText(questionState[0],8000);
            }
            else if (random4 == 1){
                sendText(questionState[1],8000);
            }
            else{
                sendText(questionState[2],8000);
            }
            state++;
            stateView.setText("Questioning State");
        }
        else if ((message.equals("Yes")||message.equals("No")||message.equals("Maybe"))&&state>1){
            switch (message){
                case "Yes":
                    if (random == 0){
                        sendText(answeringState[0],10000);
                    }
                    else if (random == 1){
                        sendText(answeringState[1],10000);
                    }
                    else{
                        sendText(answeringState[2],10000);
                    }
                    break;
                case "No": int random2 = (int)(Math.random()*2);
                    if (random2 == 0){
                        sendText(noState[0],10000);
                    }
                    else{
                        sendText(noState[1],10000);
                    }
                    break;
                case "Maybe": int random3 = (int)(Math.random()*3);
                    if (random3 == 0){
                        sendText(answeringState[0],10000);
                    }
                    else if (random3 == 1){
                        sendText(answeringState[1],10000);
                    }
                    else{
                        sendText(answeringState[2],10000);
                    }
                    break;
            }
            state++;
            stateView.setText("Questioning State");
        }
        else if ((message.equals("Ok, what should I do next")||message.equals("Ok")||message.equals("understood"))&&state>2){
            int random8 = (int)(Math.random()*3);
            if (random8 == 0) {
                sendText(finalState[0], 12000);
            }
            else if (random8 == 1){
                sendText(finalState[1],12000);
            }
            else{
                sendText(finalState[2],12000);
            }
            state++;
            stateView.setText("Confirmation State");
        }
        else if ((message.equals("Bye")||message.equals("Good bye")||message.equals("Thank You"))&&state>2){
            int random7 = (int)(Math.random()*3);
            if (random7 == 0){
                sendText(byeState[0],3000);
            }
            else if (random7 == 1){
                sendText(byeState[1],3000);
            }
            else{
                sendText(byeState[2],3000);
            }
            state++;
            stateView.setText("Good bye State");
        }
        else{
            int random5 = (int)(Math.random()*4);
            if (random5 == 0){
                sendText(confusedState[0],5000);
            }
            else if (random5 == 1){
                sendText(confusedState[1],5000);
            }
            else if (random5 == 2){
                sendText(confusedState[2],5000);
            }
            else{
                sendText(confusedState[3],5000);
            }
            stateView.setText("Confused State");
        }
    }



}
