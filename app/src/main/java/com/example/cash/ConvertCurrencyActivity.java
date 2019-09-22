package com.example.cash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.util.Currency;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ConvertCurrencyActivity extends AppCompatActivity {

    private TextView currencyInput;
    private TextToSpeech optionChosen;
    private TextView currencyOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_currency);
        currencyInput = (TextView) findViewById(R.id.currencyInput);
        currencyOutput = (TextView) findViewById(R.id.currencyOutput);
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
                optionChosen.speak("  Please give your input in the following format: value Currency 1 to currency 2", TextToSpeech.QUEUE_FLUSH, null);
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
                    ArrayList<String> allowedOptions = new ArrayList<String>(Arrays.asList(new String[]{"indian rupee", "chinese yuan", "hong kong dollar", "polish zloty", "euro", "us dollar"}));
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    currencyInput.setText(result.get(0));
                    //System.out.println(result.toString());

                    String dataString = result.get(0).trim().toLowerCase();
                    int speechStatus;
                    int indexOfTo=dataString.indexOf("to");
                    int indexOfSpace=dataString.indexOf(" ");
                    String target=dataString.substring(indexOfTo+3).trim().toLowerCase();
                    String source=dataString.substring(indexOfSpace+1,indexOfTo-1).trim().toLowerCase();
                    String amount=dataString.substring(0,indexOfSpace).trim().toLowerCase();

                    System.out.println(source+" "+target+" "+amount);

                    double amt;
                    double s=0,t=0;
                    if(allowedOptions.contains(target) && allowedOptions.contains(source)){
                        try{
                            amt=Double.parseDouble(amount);
                            switch(source){
                                case "indian rupee":
                                    s=0.014;
                                    break;
                                case "chinese yuan":
                                    s=0.14;
                                    break;
                                case "hong kong dollar":
                                    s=0.13;
                                    break;
                                case "polish zloty":
                                    s=0.25;
                                    break;
                                case "euro":
                                    s=1.1;
                                    break;
                                case "us dollar":
                                    s=1;
                                    break;
                            }

                            switch(target){
                                case "indian rupee":
                                    t=71.18;
                                    break;
                                case "chinese yuan":
                                    t=7.09;
                                    break;
                                case "hong kong dollar":
                                    t=7.83;
                                    break;
                                case "polish zloty":
                                    t=3.94;
                                    break;
                                case "euro":
                                    t=0.91;
                                    break;
                                case "us dollar":
                                    t=1;
                                    break;
                            }

                            double converted=amt*s*t; //needs internet
                            converted=Math.round(converted*100.0)/100.0;

                            System.out.println("================"+converted);
                            String finalRes=amount+" "+source+" is equal to "+converted+" "+target;
                            currencyOutput.setText(finalRes);
                            speechStatus= optionChosen.speak(finalRes, TextToSpeech.QUEUE_FLUSH, null);
                            if (speechStatus == TextToSpeech.ERROR) {
                                Log.e("TTS", "Error in converting Text to Speech!");
                            }
                        }catch (NumberFormatException e) {
                            speechStatus= optionChosen.speak("Please select any one of given currencies and state input in the prescribed  format", TextToSpeech.QUEUE_FLUSH, null);
                            if (speechStatus == TextToSpeech.ERROR) {
                                Log.e("TTS", "Error in converting Text to Speech!");
                            }
                        }
                    }else{
                        speechStatus= optionChosen.speak("Please select any one of given currencies and state input in the prescribed format", TextToSpeech.QUEUE_FLUSH, null);
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
