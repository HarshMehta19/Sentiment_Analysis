package com.example.sentimentspecimen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText editText;
    Button btn_analyze;
    String sentiment;

    private static int SPLASH_TIME = 2000;


    private class AskSentimentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            String result="";
            URL url;
            HttpURLConnection urlConnection;

            System.out.println(editText.getText());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText("Working...!!");
                }
            });

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(is);

                int data = reader.read();

                while(data!=-1) {

                    char current =(char)data;
                    result+=current;
                    data = reader.read();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            sentiment = "text sentimet";
            System.out.println(result);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {

                JSONObject jsonObject = new JSONObject(result);
                String sentimentPos = jsonObject.getString("positive");
                String sentimentNeg = jsonObject.getString("negative");

                double pos = Double.parseDouble(sentimentPos);
                double neg = Double.parseDouble(sentimentNeg);

                DecimalFormat df = new DecimalFormat("##.##");
                double dif = pos-neg;

                if(dif > 0.1) {

                    textView.setText("Positive with: " + df.format(pos*100)+ "%");
                }

                else if (neg >= 0.59)  {

                    textView.setText("Negative with: " + df.format(neg*100) + "%");
                }
                else if( dif < 0.12 ) {

                    textView.setText("Neutral");
                }



                //textView.setText("Positive: "+sentimentPos+"\nNegative: "+sentimentNeg);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        textView = (TextView) findViewById(R.id.analyzedText);
        editText = (EditText) findViewById(R.id.text);
        btn_analyze = (Button) findViewById(R.id.btn_analyze);

        //When button pressed
        btn_analyze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("Logging to the console"+ editText);
                textView.setText("Displaying the sentiment for " + editText.getText());

                findSentiment();
            }
        });
    }

    private void findSentiment() {

        InputMethodManager mgr = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(editText.getWindowToken(), 0);

        try {
            String encodeWord = URLEncoder.encode(editText.getText().toString(), "UTF-8");

            AskSentimentTask task = new AskSentimentTask();

            task.execute("https://api.uclassify.com/v1/uClassify/Sentiment/classify/?readKey=2L56yKfVaext&text=" + encodeWord);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

