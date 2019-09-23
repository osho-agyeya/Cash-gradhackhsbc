package com.example.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView inputReceived;
    private TextToSpeech optionChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputReceived = (TextView) findViewById(R.id.inputReceived);
        optionChosen = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = optionChosen.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                    int speechStatus = optionChosen.speak("Welcome to your bank account. Choose one of the following options. 1 call helpline, 2 convert currency, 3 my account, 4 reed bill, 5 cash reader", TextToSpeech.QUEUE_FLUSH, null);
                    if (speechStatus == TextToSpeech.ERROR) {
                        Log.e("TTS", "Error in converting Text to Speech!");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void getSpeechInput(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> allowedOptions = new ArrayList<String>(Arrays.asList("call helpline", "convert currency", "my account", "cash reader", "read bill"));
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    inputReceived.setText(result.get(0));
                    String dataString = inputReceived.getText().toString().toLowerCase().trim();
                    int speechStatus;
                    if (allowedOptions.contains(dataString)) {
                        Intent intent = new Intent();
                        speechStatus = optionChosen.speak(dataString, TextToSpeech.QUEUE_FLUSH, null);
                        if (speechStatus == TextToSpeech.ERROR) {
                            Log.e("TTS", "Error in converting Text to Speech!");
                        }
                        switch (dataString) {
                            case "call helpline":
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:8080435676"));
                                        try {
                                            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                // TODO: Consider calling
                                                //    Activity#requestPermissions
                                                // here to request the missing permissions, and then overriding
                                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                //                                          int[] grantResults)
                                                // to handle the case where the user grants the permission. See the documentation
                                                // for Activity#requestPermissions for more details.
                                                return;
                                            }
                                            startActivity(intent);
                                        }catch (IllegalStateException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }, 1000);

                                break;
                            case "convert currency":
                                intent = new Intent(this,ConvertCurrencyActivity.class);
                                break;
                            case "my account":
                                intent = new Intent(this,MyAccountActivity.class);
                                break;
                            case "cash reader":
                                intent = new Intent(this,CashReaderActivity.class);
                                break;

                            case "read bill":
                                intent = new Intent(this,OCRActivity.class);
                                break;
                        }
                        if(!dataString.equalsIgnoreCase("call helpline"))
                            {startActivity(intent);}
                    }else{
                        speechStatus= optionChosen.speak("Choose one of the following options. 1 call helpline, 2 convert currency, 3 my account, 4 reed bill, 5 cash reader", TextToSpeech.QUEUE_FLUSH, null);
                        if (speechStatus == TextToSpeech.ERROR) {
                            Log.e("TTS", "Error in converting Text to Speech!");
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (optionChosen != null) {
            optionChosen.stop();
            optionChosen.shutdown();
        }
    }
}
