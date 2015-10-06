package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by root on 14/8/15.
 */
public class AboutActivity extends Activity {
    String appPackageName = "in.hopprapp.hoppr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity_layout);
    }

    public void facebookIntentLoader(View view) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("fb://page/881797225233906"));
            intent.setPackage("com.facebook.katana");
            startActivity(intent);
        }
        catch (Exception e){
            Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("http://www.facebook.com/hopprtech"));
            startActivity(intent);
        }
    }

    public void googlePlayLoader(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (Exception e){
            startActivity(new Intent(Intent.ACTION_VIEW , Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
