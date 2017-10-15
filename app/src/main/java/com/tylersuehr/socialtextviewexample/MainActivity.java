package com.tylersuehr.socialtextviewexample;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.tylersuehr.socialtextview.SocialTextView;

/**
 * Copyright 2017 Tyler Suehr
 * Created by tyler on 1/21/2017.
 */
public class MainActivity extends AppCompatActivity implements SocialTextView.OnLinkClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocialTextView textView = (SocialTextView)findViewById(R.id.text_view);
        textView.setOnLinkClickListener(this);
    }

    @Override
    public void onLinkClicked(int linkType, String matchedText) {
        Toast.makeText(this, matchedText, Toast.LENGTH_SHORT).show();
    }
}