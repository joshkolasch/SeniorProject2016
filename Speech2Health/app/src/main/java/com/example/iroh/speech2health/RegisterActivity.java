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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;


/**
 * Updated by Josh on 11/15/2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mMonth;
    private EditText mDay;
    private EditText mYear;
    private EditText mHeight;
    private EditText mWeight;
    private RadioButton mMale;
    private RadioButton mFemale;
    private Button mSubmitButton;
    private RequestQueue queue;
    private String mBirthday;
    private String gender;
    private String filename;
    private GlobalVariablesClass globalVariable;

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
    }

    public void postData() {
        /*Do all the logic for the variables here*/
        /*TODO:create a function to do all the checking for the variables right here*/

        mBirthday = mYear.getText().toString() + "-" + mMonth.getText().toString() + "-" + mDay.getText().toString();

        Boolean flag = validateInput();
        if(flag == false)
        {
            return;
        }


        //NOTE: this is for debugging purposes
        /*HashMap<String, String> params = new HashMap<String, String>();
        params.put("firstname", "josh");
        params.put("lastname", "kol");
        params.put("email", "j2@hotmail.com");
        params.put("password", "1");
        params.put("birthday", "1212-12-12");
        params.put("height", "120");
        params.put("weight", "130");
        params.put("gender", "Male");
        params.put("type", "client");
        params.put("limit", "2000");*/

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("firstname", mFirstName.getText().toString());
        params.put("lastname", mLastName.getText().toString());
        params.put("password", mPassword.getText().toString());
        params.put("email", mEmail.getText().toString());
        params.put("birthday", mBirthday);
        params.put("height", mHeight.getText().toString());
        params.put("weight", mWeight.getText().toString());
        params.put("gender", gender);
        params.put("type", "client");
        params.put("limit", "2000");

        //param1 is type of method get/post/update, param2 can be replaced with a variable for which api to call, param3 is if it works, or if it didn't
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://159.203.204.9/api/v1/register", new JSONObject(params), new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                String test = response.toString();

                String apiKey = null;
                try {
                    if(response.getString("error") == "false"){

                        apiKey = response.getString("api_key");
                        writeID(filename, apiKey);
                        //set global variables
                        globalVariable = GlobalVariablesClass.getInstance();
                        globalVariable.setPatientFirstName(response.getString("firstname"));
                        globalVariable.setPatientLastName(response.getString("lastname"));
                        globalVariable.setPatientEmail(response.getString("email"));
                        globalVariable.setPatientBirthday(response.getString("birthday"));
                        globalVariable.setPatientGender(response.getString("gender"));
                        globalVariable.setPatientHeight(response.getString("height"));
                        globalVariable.setPatientWeight(response.getString("weight"));
                        globalVariable.setPatientLimit(response.getString("limit"));
                        startIntent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Content-Type","application/json");
                return headers;
            }
        };

        queue.add(jor);
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

    public Boolean validateInput(){
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

}
