package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by root on 14/8/15.
 */
public class HelpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity_layout);
    }

    public void support_click_handler(View view) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("plain/text");
        sendIntent.setData(Uri.parse("care@hopprapp.in"));
        sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"care@hopprapp.in"});
        sendIntent.putExtra(Intent.EXTRA_SUBJECT , "Support");
        startActivity(sendIntent);
    }

    public void faq_click_listener(View view) {
        Intent faqIntent = new Intent(getApplicationContext() , FAQActivity.class);
        startActivity(faqIntent);
    }
}
