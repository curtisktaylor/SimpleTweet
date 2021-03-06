package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_TWEET_LENGTH = 280;

    EditText etCompose;
    Button btnTweet;
    TextView tvCharLimit;
    TextView tvTweetError;

    boolean validTweet = false;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCharLimit = findViewById(R.id.tvCharLimit);
        tvTweetError = findViewById(R.id.tvTweetError);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int tweetLength = etCompose.getText().toString().length();
                tvCharLimit.setText(tweetLength + "/" + MAX_TWEET_LENGTH);

                if(tweetLength < MAX_TWEET_LENGTH && tweetLength > 0){
                    validTweet = true;
                } else{
                    validTweet = false;
                }

                if(validTweet){
                    btnTweet.setBackgroundColor(Color.rgb(108, 173, 222));
                    tvCharLimit.setTextColor(Color.rgb(145, 145, 145));
                } else{
                    btnTweet.setBackgroundColor(Color.rgb(216, 212, 212));
                    tvCharLimit.setTextColor(Color.rgb(145, 0, 0));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //set click listener on tweet button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    tvTweetError.setText("Your Tweet can't be empty!");
                    return;
                }
                if(tweetContent.length() > MAX_TWEET_LENGTH){
                    tvTweetError.setText("Your Tweet is too long!");
                    return;
                }
                Toast.makeText(ComposeActivity.this, "Your Tweet", Toast.LENGTH_LONG).show();
                //make API call to Twitter to publish tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });
            }
        });


    }
}