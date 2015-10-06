package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by root on 21/9/15.
 */
public class SignInSignUpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_sign_out_layout);
    }

    public void getSignUpActivity(View view) {
        Intent intent = new Intent(getApplicationContext() , LoginActivity.class);
        startActivity(intent);
    }

    public void getLoginActivity(View view) {
        Intent intent = new Intent(getApplicationContext() , SignInActivity.class);
        startActivity(intent);
    }
}
