package com.example.iroh.speech2health;

/**
 * Created by Josh on 11/12/2016.
 *
 * Note: For more information on how to set up a Global Variable Class, go to the following link:
 * https://www.youtube.com/watch?v=1807u_OmW5A
 */
import android.app.Application;
import android.support.v7.app.AppCompatActivity;

public class GlobalVariablesClass extends Application {

    private static GlobalVariablesClass instance;
    private static String patientFirstName;
    private String patientLastName;
    private String patientHeight;
    private String patientWeight;
    private String patientEmail;
    private String patientLimit;
    private String patientBirthday;
    private String patientGender;

    private GlobalVariablesClass(){}

    public String getPatientFirstName(){
        return GlobalVariablesClass.patientFirstName;
    }
    public void setPatientFirstName(String var){
        GlobalVariablesClass.patientFirstName = var;
    }

    public static synchronized GlobalVariablesClass getInstance(){
        if(instance == null){
            instance = new GlobalVariablesClass();
        }
        return instance;
    }
    public String getPatientLastName(){
        return patientLastName;
    }
    public void setPatientLastName(String var){
        patientLastName = var;
    }

    public String getPatientHeight(){
        return patientHeight;
    }
    public void setPatientHeight(String var){
        patientHeight = var;
    }

    public String getPatientWeight(){
        return patientWeight;
    }
    public void setPatientWeight(String var){
        patientWeight = var;
    }

    public String getPatientEmail(){
        return patientEmail;
    }
    public void setPatientEmail(String var){
        patientEmail = var;
    }

    public String getPatientLimit(){
        return patientLimit;
    }
    public void setPatientLimit(String var){
        patientLimit = var;
    }

    public String getPatientBirthday(){
        return patientBirthday;
    }
    public void setPatientBirthday(String var){
        patientBirthday = var;
    }

    public String getPatientGender(){
        return patientGender;
    }
    public void setPatientGender(String var){
        patientGender = var;
    }

}
