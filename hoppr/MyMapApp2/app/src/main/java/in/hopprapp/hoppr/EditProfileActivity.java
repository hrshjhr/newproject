package in.hopprapp.hoppr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by root on 21/9/15.
 */
public class EditProfileActivity extends Activity {

    String firstName;
    String lastName;
    String password;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);
        Intent intent = getIntent();
        firstName = intent.getStringExtra("firstName");
        lastName = intent.getStringExtra("lastName");
        email = intent.getStringExtra("email");
        SharedPreferences preferences = getSharedPreferences("Hoppr App", MODE_PRIVATE);
        password = preferences.getString("password" , "");
    }

    public void saveChanges(View view) {
        Intent intent = new Intent();
        EditText nameEditText = (EditText) findViewById(R.id.first_name);
        EditText lastNameEditText = (EditText) findViewById(R.id.last_name);
        /*EditText passwordNewEditText = (EditText)findViewById(R.id.new_password);
        EditText passwordOldEditText = (EditText) findViewById(R.id.current_password);*/
        EditText emailEditText = (EditText) findViewById(R.id.email_edit);

        if(!nameEditText.getText().toString().trim().equals("")){
            intent.putExtra("firstName"  , nameEditText.getText().toString());
            firstName = nameEditText.getText().toString().trim();
        }

        if(!lastNameEditText.getText().toString().trim().equals("")){
            intent.putExtra("lastName" , lastNameEditText.getText().toString());
            lastName = lastNameEditText.getText().toString().trim();
        }
        if(!emailEditText.getText().toString().trim().equals("")){
            intent.putExtra("email" , emailEditText.getText().toString().trim());
            email = emailEditText.getText().toString().trim();
        }
        /*if(!passwordNewEditText.getText().toString().trim().equals("")){
            if(passwordOldEditText.getText().toString().trim().equals("")){
                Toast.makeText(getApplicationContext() , "Please Enter the Old Password" , Toast.LENGTH_SHORT ).show();
            }
            else {
                SharedPreferences settings = getSharedPreferences("Hoppr App", 0);
                String passwordSaved = settings.getString("password1" , "");
                if(!passwordSaved.equals(passwordOldEditText.getText().toString().trim())){
                    Toast.makeText(getApplicationContext() , "Old Password Doesn't Seem To Match" , Toast.LENGTH_SHORT).show();
                }
                else {
                    if (passwordNewEditText.getText().toString().trim().length() < 6){
                        Toast.makeText(getApplicationContext() , "Password cannot be less than 6 words" , Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("password1", passwordNewEditText.getText().toString().trim() );
                        editor.apply();
                    }
                }
            }
        }*/
        setResult(RESULT_OK , intent);
        new EditProfileCall().execute();
        finish();
    }

    public void cancelChanges(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED , intent);
        finish();
    }

    private class EditProfileCall extends AsyncTask<Void , Void , Integer> {
        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            return null;
        }
    }
}
