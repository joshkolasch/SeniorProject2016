package com.example.iroh.speech2health;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Iroh on 10/3/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    protected EditText mFirstName;
    protected EditText mLastName;
    protected EditText mEmail;
    protected EditText mPassword;
    protected EditText mConfirmPassword;
    protected EditText mMonth;
    protected EditText mDay;
    protected EditText mYear;
    protected EditText mHeight;
    protected EditText mWeight;
    protected RadioButton mMale;
    protected RadioButton mFemale;
    protected Button mSubmitButton;
    protected RequestQueue queue;
    protected String mBirthday;
    protected String gender;
    protected String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize variables
        mFirstName = (EditText) findViewById(R.id.firstNameRegistrationText);
        mLastName = (EditText) findViewById(R.id.lastNameRegistrationText);
        mEmail = (EditText) findViewById(R.id.emailRegistrationText);
        mPassword = (EditText) findViewById(R.id.passwordRegistrationText);
        mConfirmPassword = (EditText) findViewById(R.id.confirmPasswordRegistrationText);
        mMonth = (EditText) findViewById(R.id.monthRegistrationText);
        mDay = (EditText) findViewById(R.id.dayRegistrationText);
        mYear = (EditText) findViewById(R.id.yearRegistrationText);
        mHeight = (EditText) findViewById(R.id.heightRegistrationText);
        mWeight = (EditText) findViewById(R.id.weightRegistrationText);
        mMale = (RadioButton) findViewById(R.id.maleRegistrationRadioButton);
        mFemale = (RadioButton) findViewById(R.id.femaleRegistrationRadioButton);
        mSubmitButton = (Button) findViewById(R.id.submitRegistrationButton);
        queue = Volley.newRequestQueue(this);
        filename = "userID";
        //listen to Register Button click
        mSubmitButton.setOnClickListener(this);

        /*Attempt to hide the keyboard when clicking out of the EditText boxes*/
        RelativeLayout mRelLay = (RelativeLayout) findViewById(R.id.relativeLayoutRegister);
        mRelLay.setOnClickListener(this);
        /*
        View v = findViewById(R.id.myView);
        InputMethodManager imm = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mFirstName.getWindowToken(), 0);*/


    }

    private void postData() {
        /*Do all the logic for the variables here*/
        /*create a function to do all the checking for the variables right here*/

        mBirthday = mYear.getText().toString() + "-" + mMonth.getText().toString() + "-" + mDay.getText().toString();

        Boolean flag = validateInput();
        if(flag == false)
        {
            return;
        }
        //TODO:fix the test.substring. It's currently hardcoded for a 3 digit number. Dont expect that
        //param1 is type of method get/post/update, param2 can be replaced with a variable for which api to call, param3 is if it works, or if it didn't
        StringRequest sr = new StringRequest(Request.Method.POST, "http://159.203.204.9/task_manager/v1/register", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String test = response.toString();
                if (test.contains("false")) {
                    //TODO:substring is probably wrong in this case- don't hard code this
                    //use string.index of
                    //locate ":" to start + 1 or (pid + some #)
                    //locate "," to end
                    // call them int begin, end; instead of 7 and 9
                    //substring(start, end) begins at start and stops at end - 1
                    String variable = test.substring(7, 9);
                    writeID(filename, test.substring(7, 9));
                    startIntent();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast t = Toast.makeText(getApplicationContext(),
                        "Opps! Your device couldn't connect to the server",
                        Toast.LENGTH_SHORT);
                t.show();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", mFirstName.getText().toString());
                params.put("lastname", mLastName.getText().toString());
                params.put("password", mPassword.getText().toString());
                params.put("email", mEmail.getText().toString());
                params.put("birthday", mBirthday);
                params.put("height", mHeight.getText().toString());
                params.put("weight", mWeight.getText().toString());
                params.put("gender", gender);
                params.put("type", "client");
                return params;
            }

            /*@Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", "josh");
                params.put("lastname", "kol");
                params.put("password", "123");
                params.put("email", "j@j.com");
                params.put("birthday", "1212-12-12");
                params.put("height", "120");
                params.put("weight", "130");
                params.put("gender", "Male");
                params.put("type", "client");
                return params;
            }*/

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/x-www-form-urlencoded");
                return headers;
            }
        };

        queue.add(sr);

        //TODO: put a check here to make sure the user was registered and given an id
        startIntent();
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.submitRegistrationButton: postData();
                break;
            case R.id.relativeLayoutRegister: hideSoftKeyboard(v);
        }
    }
    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch(v.getId()) {
            case R.id.maleRegistrationRadioButton:
                if(checked) {
                    gender = "Male";
                    break;
                }
            case R.id.femaleRegistrationRadioButton:
                if(checked) {
                    gender = "Female";
                    break;
                }

        }
    }

    private Boolean validateInput(){
        Boolean flag = true;
        //were any of the variables not entered
        if(mFirstName.getText().toString() == null)
        {
            flag = false;
        }
        if(mLastName.getText().toString() == null)
        {
            flag = false;
        }
        if(mPassword.getText().toString() == null)
        {
            flag = false;
        }
        if(mEmail.getText().toString() == null)
        {
            flag = false;
        }
        if(mBirthday == null)
        {
            flag = false;
        }
        if(mHeight.getText().toString() == null)
        {
            flag = false;
        }
        if(mWeight.getText().toString() == null)
        {
            flag = false;
        }
        if(gender == null)
        {
            flag = false;
        }
        if(mDay.getText().toString() == null)
        {
            flag = false;
        }
        if(mMonth.getText().toString() == null)
        {
            flag = false;
        }
        if(mYear.getText().toString() == null)
        {
            flag = false;
        }

        //validate the email
        if(!mEmail.getText().toString().contains("@"))
        {
            flag = false;
        }
        if(!mEmail.getText().toString().contains(".com") && !mEmail.getText().toString().contains(".net") && !mEmail.getText().toString().contains(".org") && !mEmail.getText().toString().contains(".gov"))
        {
            flag = false;
        }

        return flag;
    }

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void startIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void writeID(String file_name, String ID){
        try {
            FileOutputStream outStream = openFileOutput(file_name, MODE_PRIVATE);
            outStream.write(ID.getBytes());
            outStream.close();
            Toast.makeText(getApplicationContext(), "Message Saved", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Hides keyboard when touching the screen
    //works if the user selects an editText box then touches an empty space on their screen
    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed(){}
}
