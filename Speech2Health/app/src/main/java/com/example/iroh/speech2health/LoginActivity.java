package com.example.iroh.speech2health;

/**
 * Created by Iroh on 10/3/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText emailTextBox;
    private EditText passwordTextBox;
    private TextView errorTextBox;
    private Button loginButton;
    private Button registerButton;
    private RequestQueue queue;
    private String filename;
    private RelativeLayout relativeLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutLogin);
        emailTextBox = (EditText) findViewById(R.id.email);
        passwordTextBox = (EditText) findViewById(R.id.password);
        errorTextBox = (TextView) findViewById(R.id.errorTextView);
        loginButton = (Button) findViewById(R.id.email_sign_in_button);
        registerButton = (Button) findViewById(R.id.registerLoginButton);
        queue = Volley.newRequestQueue(this);
        filename = "userID";
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);
    }

    public void attemptLogin() {

        if (emailTextBox.getText().length() == 0 || passwordTextBox.getText().length() == 0) {
            errorTextBox.setText("Please enter a username and password");
            return;
        }

        if (!emailTextBox.getText().toString().contains("@")) {
            errorTextBox.setText("Please enter a valid email address");
            return;
        }

        HashMap<String, String> params = new HashMap<String, String> ();
        params.put("email", emailTextBox.getText().toString());
        params.put("password", passwordTextBox.getText().toString());


        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://159.203.204.9/api/v1/login", new JSONObject(params), new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                String test = response.toString();
                String error;
                String api_key;
                try {
                    error = response.getString("error");
                    api_key = response.getString("api_key");
                    //false indicates that there were no errors
                    if(error == "false")
                    {
                        writeID(filename, api_key);
                        startIntent();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_LONG).show();
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test = "error";
            }

        });

        queue.add(jor);
    }

    public void goToRegisterPage(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.email_sign_in_button: attemptLogin();
                break;
            case R.id.registerLoginButton: goToRegisterPage();
                break;
            case R.id.relativeLayoutLogin: hideSoftKeyboard(v);
                break;
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

    @Override
    public void onBackPressed(){}

    //Activities are NOT Fragments, they do not have getActivity() member functions
    //that is why
    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
