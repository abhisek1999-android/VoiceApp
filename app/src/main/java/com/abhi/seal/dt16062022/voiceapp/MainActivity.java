package com.abhi.seal.dt16062022.voiceapp;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.abhi.seal.dt16062022.voiceapp.Extra.EvaluateString;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements RecognitionListener {
    TextToSpeech textToSpeech;
    EditText sampleText;
    TextView statusText, textGot,quesNum;
    Button btnText;
    String userIp = "";
    private SpeechRecognizer speech;
    private Intent recognizerIntent;
    boolean isActive = true;
    private ProgressBar progressBar;
    EvaluateString evaluateString;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    int count=0;
    Boolean performAc=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sampleText = findViewById(R.id.Text);
        quesNum = findViewById(R.id.quesNum);
        btnText = findViewById(R.id.btnText);
        statusText = findViewById(R.id.statusText);
        textGot = findViewById(R.id.textGot);
        speech = null;
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        evaluateString = new EvaluateString();
        //multiplyNumbers("4");


        progressBar.setVisibility(View.INVISIBLE);
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 3000);


        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.i("TextToSpeech", "Language Not Supported");
                    }

                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            Log.i("TextToSpeech", "On Start");
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Log.i("TextToSpeech", "On Done");

                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    isActive=true;
                                    takeVcIp();
                                }
                            }, 300);


                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i("TextToSpeech", "On Error");
                        }
                    });

                } else {
                    Log.i("TextToSpeech", "Initialization Failed");
                }
            }
        });

        btnText.setOnClickListener(view -> {
            //       startActivity(new Intent(getApplicationContext(),VoiceDetect.class));
            try {
                performAction();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });

    }


    void performAction() throws InterruptedException {
            sampleText.setText(generateSyntax());
            quesNum.setText((count+1)+"");
            if (!sampleText.getText().toString().equals("")) {
                speak(sampleText.getText().toString(),"q");

            } else {
                speak("Filed Can't be empty","q");
            }
            if (count==10){
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);
                speech.stopListening();
                count=0;
            }
         isActive = true;

    }

    private void takeVcIp() {


        if (isActive) {

            isActive=false;
            AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
            amanager.setStreamMute(AudioManager.STREAM_ALARM, true);
            amanager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            amanager.setStreamMute(AudioManager.STREAM_RING, true);
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setIndeterminate(true);
            speech.startListening(recognizerIntent);


        }
        else {
            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);
        }

//        if (isActive){
//            isActive=false;
//            Intent intent
//                    = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
//                    Locale.getDefault());
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");
//
//            try {
//                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//            }
//            catch (Exception e) {
//                Toast.makeText(MainActivity.this, " " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }

    }


    public String generateSyntax(){

        int min=1;
        int max=10;
        int a = (int)(Math.random()*(max-min+1)+min);
        int b= (int)(Math.random()*(max-min+1)+min);
        return a+"*"+b;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speech != null) {
            speech.destroy();
            Log.i("LOG_TAG", "destroy");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                // Toast.makeText(this, Objects.requireNonNull(result).get(0), Toast.LENGTH_SHORT).show();

                try {
                    multiplyNumbers(Objects.requireNonNull(result).get(0));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void multiplyNumbers(String s) throws InterruptedException {


        if (!sampleText.getText().toString().equals("")) {
            try {
                statusText.setVisibility(View.VISIBLE);
                if (Integer.parseInt(s.trim()) == evaluateString.evaluate(sampleText.getText().toString())) {

                    statusText.setText("Correct :)");
                    statusText.setTextColor(Color.parseColor("#FF018786"));
                    statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg));
                    speak("Correct","ans");

                } else {

                    statusText.setText("Incorrect :(");
                    statusText.setTextColor(Color.RED);
                    statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg_red));
                    speak("incorrect, correct is "+evaluateString.evaluate(sampleText.getText().toString()),"ans");
                }
            } catch (Exception e) {

                statusText.setText("Incorrect :(");
                statusText.setTextColor(Color.RED);
                statusText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.edittext_bg_red));
                speak("incorrect, correct is "+evaluateString.evaluate(sampleText.getText().toString()),"ans");

            }

        }


    }

    private void speak(String s , String ans) throws InterruptedException {


        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_RING, false);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);

       // Thread.sleep(1000);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ans.equals("ans")){
                    if (count<10){
                        try {
                            performAction();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        count++;
                    }
                    else if (count>=10){
                        count=0;
                        progressBar.setIndeterminate(false);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                }
            }
        }, 3000);





    }


    @Override
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override
    public void onBeginningOfSpeech() {

        Log.i("LOG_TAG", "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);
    }

    @Override
    public void onRmsChanged(float v) {
        Log.i("LOG_TAG", "onRmsChanged: " + v);
        progressBar.setProgress((int) v);
    }

    @Override
    public void onBufferReceived(byte[] bytes) {
        Log.i("LOG_TAG", "onBufferReceived: " + bytes);
    }

    @Override
    public void onEndOfSpeech() {

        Log.i("LOG_TAG", "onEndOfSpeech");
        progressBar.setIndeterminate(true);

       isActive=false;
        AudioManager amanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        amanager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
        amanager.setStreamMute(AudioManager.STREAM_ALARM, false);
        amanager.setStreamMute(AudioManager.STREAM_MUSIC, false);
        amanager.setStreamMute(AudioManager.STREAM_RING, false);
        amanager.setStreamMute(AudioManager.STREAM_SYSTEM, false);

    }

    @Override
    public void onError(int i) {

        String errorMessage = getErrorText(i);
        Log.d("LOG_TAG", "FAILED " + errorMessage);
       textGot.setText(errorMessage);
//        toggleButton.setChecked(false);
    }

    @Override
    public void onResults(Bundle bundle) {

        Log.i("LOG_TAG", "onPartialResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";
        textGot.setText(text);
        try {
            multiplyNumbers(text);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.i("LOG_TAG", "onPartialResults");
        ArrayList<String> matches = bundle
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            text += result + "\n";

    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}