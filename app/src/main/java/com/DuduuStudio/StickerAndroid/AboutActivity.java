package com.DuduuStudio.StickerAndroid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class AboutActivity extends BaseActivity {
    private static final String TAG = "AboutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.about_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setupTextView(getResources().getString(R.string.app_website), R.id.view_webpage);
        setupTextView(getResources().getString(R.string.app_privacy_policy_website), R.id.privacy_policy);
        setupTextView(getResources().getString(R.string.app_creator_website), R.id.footnote);

        final TextView sendEmail = findViewById(R.id.send_email);
        sendEmail.setOnClickListener(v -> launchEmailClient(getResources().getString(R.string.app_email)));
    }

    private void setupTextView(String url, int textViewResId) {
        TextView textView = findViewById(textViewResId);
        textView.setOnClickListener(v -> launchWebpage(url));
    }

    private void launchWebpage(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error launching webpage: " + url, e);
        }
    }

    private void launchEmailClient(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.info_send_email_to_prompt)));
    }
} 