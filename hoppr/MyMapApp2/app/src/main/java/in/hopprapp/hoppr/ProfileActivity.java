package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by root on 14/8/15.
 */
public class ProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity_layout);
        TextView textViewFirstName = (TextView) findViewById(R.id.first_name_profile);
        TextView textViewLastName = (TextView) findViewById(R.id.last_name_profile);
        TextView textViewPhone = (TextView) findViewById(R.id.phone_number);
        TextView textViewEmail = (TextView) findViewById(R.id.profile_email);
        Intent intent = this.getIntent();
        if(!(intent.getStringExtra("firstName").equals(""))){
            textViewFirstName.setText(intent.getStringExtra("firstName"));
        }

        if(!(intent.getStringExtra("email").equals(""))){
            textViewEmail.setText(intent.getStringExtra("email"));
        }

        if(!(intent.getStringExtra("lastName").equals(""))){
            textViewLastName.setText(intent.getStringExtra("lastName"));
        }
        if(!(intent.getStringExtra("phoneNumber").equals(""))){
            textViewPhone.setText(intent.getStringExtra("phoneNumber"));
        }
    }

    public void editProfile(View view) {
        Intent intent = new Intent(getApplicationContext() , EditProfileActivity.class);
        intent.putExtra("firstName" , intent.getStringExtra("firstName"));
        intent.putExtra("email" , intent.getStringExtra("email"));
        intent.putExtra("lastName" , intent.getStringExtra("lastName"));
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode , int resultCode , Intent data){
        super.onActivityResult(requestCode , resultCode , data);
        if(requestCode == 1){
            if(resultCode == RESULT_CANCELED){

            }
            else if(resultCode == RESULT_OK){
                if(!(data.getStringExtra("firstName") == null)){
                    TextView nameText = (TextView) findViewById(R.id.first_name_profile);
                    nameText.setText(data.getStringExtra("firstName"));
                }

                if(!(data.getStringExtra("lastName") == null)){
                    TextView lastNameText = (TextView) findViewById(R.id.last_name_profile);
                    lastNameText.setText(data.getStringExtra("lastName"));
                }
                if(!(data.getStringExtra("email") == null)){
                    TextView emailNameText = (TextView) findViewById(R.id.profile_email);
                    emailNameText.setText(data.getStringExtra("email"));
                }
            }
        }
    }

    public void signOut(View view) {
        SharedPreferences settings = getSharedPreferences("Hoppr App", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.commit();
        Intent goHome = new Intent(getApplicationContext() , SplashScreenActivity.class);
        goHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        goHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(goHome);
    }
}
