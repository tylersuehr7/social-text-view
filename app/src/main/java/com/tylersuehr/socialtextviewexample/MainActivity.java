package com.tylersuehr.socialtextviewexample;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tylersuehr.socialtextview.LinkMode;
import com.tylersuehr.socialtextview.SocialTextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SocialTextView textView = (SocialTextView)findViewById(R.id.text_view);
        textView.setLinkClickListener(new SocialTextView.LinkClickListener() {
            @Override
            public void onLinkClicked(LinkMode mode, String matched) {
                Toast.makeText(MainActivity.this, mode.toString()
                        + " " + matched, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static int sizeOf(Object object) {
        try {
            if (object == null) {
                return -1;
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream obOs = new ObjectOutputStream(os);
            obOs.writeObject(object);
            obOs.flush();
            obOs.close();

            byte[] bytes = os.toByteArray();
            return bytes != null ? bytes.length : 0;
        } catch (IOException ex) {
            Log.e("sizeOf()", "Couldn't object!", ex);
            return -1;
        }
    }
}