package com.abhi.seal.dt16062022.voiceapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.seal.dt16062022.voiceapp.Extra.EvaluateString;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    TextToSpeech textToSpeech;
    EditText sampleText;
    TextView statusText;
    Button btnText;
    String userIp="";

    boolean isActive=true;
    EvaluateString evaluateString;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sampleText = findViewById(R.id.Text);
        btnText = findViewById(R.id.btnText);
        statusText=findViewById(R.id.statusText);


        evaluateString=new EvaluateString();
      //  multiplyNumbers("4");

        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS){
                    int result=textToSpeech.setLanguage(Locale.UK);

                    if (result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("TextToSpeech","Language Not Supported");
                    }

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i("TextToSpeech","On Start");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TextToSpeech","On Done");
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                  takeVcIp();
                                }
                            }, 100);

                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i("TextToSpeech","On Error");
                        }
                    });

                }else {
                    Log.i("TextToSpeech","Initialization Failed");
                }
            }
        });

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sampleText.getText().toString().equals("")){
                    textToSpeech.speak(sampleText.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                }
                else {
                    textToSpeech.speak("Filed Can't be empty",TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                }
                isActive=true;

            }
        });


    }

    private void takeVcIp() {

        if (isActive){
            isActive=false;
            Intent intent
                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
               // Toast.makeText(this, Objects.requireNonNull(result).get(0), Toast.LENGTH_SHORT).show();

                multiplyNumbers(Objects.requireNonNull(result).get(0));

            }
        }
    }

    public void multiplyNumbers(String s) {


        if (!sampleText.getText().toString().equals("")){
            try{
                statusText.setVisibility(View.VISIBLE);
                if (Integer.parseInt(s.trim()) == evaluateString.evaluate(sampleText.getText().toString())){


                    textToSpeech.speak("Correct",TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                    statusText.setText("Correct :)");
                    statusText.setTextColor(Color.parseColor("#FF018786"));
                    statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg));
                }else{
                    textToSpeech.speak("incorrect",TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);

                    statusText.setText("Incorrect :(");
                    statusText.setTextColor(Color.RED);
                    statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg_red));
                }}catch (Exception e){
                textToSpeech.speak("incorrect",TextToSpeech.QUEUE_FLUSH,null,TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                statusText.setText("Incorrect :(");
                statusText.setTextColor(Color.RED);
                statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg_red));
            }

        }







    }



}