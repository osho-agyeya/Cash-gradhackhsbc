package com.example.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FileComplaintActivity extends AppCompatActivity {

    private TextView helpOption;
    private TextToSpeech optionChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_complaint);
        helpOption = (TextView) findViewById(R.id.helpOption);
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
                    ArrayList<String> allowedOptions = new ArrayList<String>(Arrays.asList(new String[]{"call","email"}));
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    helpOption.setText(result.get(0));
                    String dataString = result.get(0).toLowerCase().trim();
                    int speechStatus;
                    if(allowedOptions.contains(dataString)){
                        Intent intent = new Intent();
                        switch(dataString){
                            case "call":
                                speechStatus= optionChosen.speak("Dialling helpline", TextToSpeech.QUEUE_FLUSH, null);
                                if (speechStatus == TextToSpeech.ERROR) {
                                    Log.e("TTS", "Error in converting Text to Speech!");
                                }
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:7800191911"));

                                break;
                            case "email":

                                break;
                        }
                        startActivity(intent);
                    }else{
                        speechStatus= optionChosen.speak("Please select any one of given options", TextToSpeech.QUEUE_FLUSH, null);
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

