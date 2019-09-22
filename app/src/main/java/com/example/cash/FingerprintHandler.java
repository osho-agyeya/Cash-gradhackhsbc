package com.example.cash;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppComponentFactory;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.concurrent.TimeUnit;


@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private TextToSpeech tts;



    private Context context;

    public FingerprintHandler(final Context context){

        this.context = context;
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = tts.setLanguage(Locale.US);
                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");


                } else {
                    Toast.makeText(context, "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){

        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);

    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {

        try {
            this.update("There was an Auth Error. " + errString, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationFailed() {

        try {
            this.update("Auth Failed ", false);
            tts.speak("Authentication failed", TextToSpeech.QUEUE_FLUSH, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {

        try {
            this.update("Error: " + helpString, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        try {

            this.update("VERIFIED!", true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void update(String s, boolean b) throws InterruptedException {

        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);

        paraLabel.setText(s);



        if(b == false){

            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

        } else {


                paraLabel.setTextColor(ContextCompat.getColor(context, R.color.success));
                imageView.setImageResource(R.mipmap.done);


                if (paraLabel.getText().toString().indexOf("VERIFIED!") != -1) {

                    tts.speak("Authentication successful", TextToSpeech.QUEUE_FLUSH, null);



                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }
                    }, 1000);








                }

                    }



        }

}



