package com.example.iroh.speech2health;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Josh on 11/13/2016.
 */
public class MyAccountMenu extends Activity implements View.OnClickListener{

    private GlobalVariablesClass globalVariable;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText newPassword2;
    private TextView accountName;
    private TextView accountEmail;
    private Button changePasswordButton;
    private RelativeLayout relativeLayout;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_my_account);

        globalVariable = GlobalVariablesClass.getInstance();
        accountName = (TextView) findViewById(R.id.nameTextViewMyAccount);
        accountEmail = (TextView) findViewById(R.id.emailTextViewMyAccount);
        changePasswordButton = (Button) findViewById(R.id.changePasswordButtonMyAccount);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayoutMyAccount);

        //set patients first and last names
        accountName.setText(globalVariable.getPatientFirstName() + " " + globalVariable.getPatientLastName());
        accountEmail.setText(globalVariable.getPatientEmail());

        changePasswordButton.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.changePasswordButtonMyAccount: changePassword();
                break;
            case R.id.relativeLayoutMyAccount: hideSoftKeyboard(v);
                break;
        }
    }

    public void changePassword(){

        currentPassword = (EditText) findViewById(R.id.currentPasswordEditTextMyAccount);
        newPassword = (EditText) findViewById(R.id.newPasswordEditTextMyAccount);
        newPassword2 = (EditText) findViewById(R.id.newPassword2EditTextMyAccount);

        if(currentPassword.length() == 0 || newPassword.length() == 0 || newPassword2.length() == 0){
            Toast.makeText(MyAccountMenu.this, "Fill in Current Password and New Password", Toast.LENGTH_LONG).show();
            return;
        }

        String password1 = newPassword.getText().toString();
        String password2 = newPassword2.getText().toString();

        if(password1.equals(password2)){
            //do the api call in here
            //TODO: request an API call to change passwords and include it below.
            Toast.makeText(MyAccountMenu.this, "Passwords match", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MyAccountMenu.this, "New passwords didn't match", Toast.LENGTH_LONG).show();
        }
    }

    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
