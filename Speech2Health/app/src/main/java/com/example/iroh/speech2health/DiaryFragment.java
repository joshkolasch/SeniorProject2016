package com.example.iroh.speech2health;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import info.androidhive.materialtabs.R;
/**
 * Created by Iroh on 9/30/2016.
 *
 * DiaryFragment.java
 */

//imports needed from previous version of the app
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ai.api.AIConfiguration;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.AIDataService;

public class DiaryFragment extends Fragment implements View.OnClickListener {

    private static final SimpleDateFormat sFilenameFormat = new SimpleDateFormat("yyyy-MM-dd-HH'.txt'", Locale.US);
    private static final File sDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/S2TDirectory");
    private BufferedOutputStream mFileOutputStream;
    private PowerManager.WakeLock mWakeLock;
    private long mHour = -1;
    private long mCurrentHour = -1;
    protected static final int RESULT_SPEECH = 1;
    private RequestQueue queue;

    private ImageButton btnSpeak;
    private TextView responseTextView;
    private EditText mET;
    private RelativeLayout relLayout;

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
    String formattedDate = df.format(c.getTime());
    String formattedDate1 = df1.format(c.getTime());

    private String aiFoodName;
    private String aiSize;
    private String aiTime;
    private String finalSubmission;

    //private AIService aiService;
// Now formattedDate have current date/time

    protected Button submitButton;
    private String filename;

    public DiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_diary, container, false);

        sDirectory.mkdirs();
        //TODO: Note-> this was changed to this.getContext()
        queue = Volley.newRequestQueue(this.getContext());
        //txtText = (TextView) findViewById(R.id.txtText);
        mET = (EditText) mView.findViewById(R.id.submissionVoiceText);
        responseTextView = (TextView) mView.findViewById(R.id.responseMessageVoiceText);
        filename = "userID";

        btnSpeak = (ImageButton) mView.findViewById(R.id.btnSpeak);
        submitButton = (Button) mView.findViewById(R.id.submitVoiceButton);
        submitButton.setOnClickListener(this);
        relLayout = (RelativeLayout) mView.findViewById(R.id.relativeLayoutVoice);
        relLayout.setOnClickListener(this);
        btnSpeak.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //aiService.startListening();
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                    //mET.setText("");
                } catch (ActivityNotFoundException a) {
                    /*Toast t = Toast.makeText(v.getContext(),
                            "Opps! Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();*/
                }
            }
        });

        return mView;
    }



    //This is where the speech gets processed by Google's api. Once it gets processed, it gets output to the screen (mET)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SPEECH: {

                //NOTE: the -1 was a RESULT_OK value previously
                if (resultCode == -1 && null != data) {

                    File file = new File(sDirectory, formattedDate1+".txt");//sFilenameFormat.format(DateFormat.getDateTimeInstance()));
                    try {
                        mFileOutputStream = new BufferedOutputStream(new FileOutputStream(file, true));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    mET.setText(text.get(0));
                    //startVolley(txtText.getText().toString());
                    //Toast.makeText(this.getContext(), formattedDate, Toast.LENGTH_SHORT).show();

                    String mData = formattedDate + " " + mET.getText()  ;
                    try {
                        mFileOutputStream.write(mData.getBytes());
                        mFileOutputStream.write('\n');
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        mFileOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mFileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mFileOutputStream = null;
                }


                break;
            }

        }
    }

    //input is what the user spoke into the app == mET
    public void startVolley(final String input) {

        final AIConfiguration config = new AIConfiguration("b421b100a49248f1bd410b6570c23e72",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIDataService aiDataService = new AIDataService(this.getContext(), config);

        final AIRequest aiRequest = new AIRequest();
        //TODO:Should this be input instead of mET?
        aiRequest.setQuery(input);

        responseTextView.setText("");

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    // process aiResponse here
                    Result result = aiResponse.getResult();

                    // Get parameters
                    String parameterString = "";
                    if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                        for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                            parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                        }
                    }

                    //String test;
                    //test = result.getStringParameter("FoodNames");
                    // Show results in TextView.

                    if(parameterString.contains("FoodNames")){

                        /*int start = parameterString.indexOf("FoodNames,") + 12;
                        int end = parameterString.indexOf("\"", start);
                        aiFoodName = parameterString.substring(start, end);*/
                        aiFoodName = result.getStringParameter("FoodNames");
                    }
                    if(parameterString.contains("Size")){

                        /*int start = parameterString.indexOf("Size,") + 7;
                        int end = parameterString.indexOf("\"", start);
                        aiSize = parameterString.substring(start, end);*/
                        aiSize = result.getStringParameter("Size");
                    }
                    if(parameterString.contains("Time")){

                        /*int start = parameterString.indexOf("Time,") + 7;
                        int end = parameterString.indexOf("\"", start);
                        aiTime = parameterString.substring(start, end);*/
                        aiTime = result.getStringParameter("Time");
                    }

                    //if all of the required stuff for the database is gathered, set up a string to be sent to the database
                    //TODO: Note this is probably where the codes breaks occasionally. Do some more testing with this segment of code
                    if(aiFoodName != null && aiFoodName != "" && aiSize != null && aiSize != "" && aiTime != null && aiTime != ""){
                        finalSubmission = "I had " + aiSize + " " + aiFoodName + " at " + aiTime + ".";
                    }
                    //if there is a missing parameter, query the user for the missing parameter
                    else {
                        responseTextView.setText(result.getFulfillment().getSpeech()
                                //+ "\n" + aiFoodName + " " + aiSize + " " + aiTime
                        );
                        //DEBUGGER-CODE:displays the values received from
                        //responseTextView.append("\n" + aiFoodName + " " + aiSize + " " + aiTime);
                    }

                    if(finalSubmission != null && finalSubmission != ""){

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("input", finalSubmission);
                        params.put("foodname", aiFoodName);

                        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://159.203.204.9/api/v1/user/postFood", new JSONObject(params), new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                //String test = response.toString();
                                String test = response.toString();

                                try {
                                    JSONObject jsonObject = response.getJSONObject("calories");
                                    int calValue = jsonObject.getInt("calorie");
                                    responseTextView.append("Great!\n" +
                                            "Total Calories: " + calValue);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                //mET.append("\n\nTotal Calories: " + calValue);

                                //reset these back to null
                                //TODO:is there a way i can reset the AI api? I don't want it to remember this query anymore
                                aiFoodName = "";
                                aiTime = "";
                                aiSize = "";
                                finalSubmission = "";
                                mET.setText("");

                            }


                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                String test2 = "error";
                            }

                        }){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/x-www-form-urlencoded");
                            headers.put("Api-Key", readID("userID"));
                            //headers.put("Authorization", "205");
                            return headers;
                        }
                        };
                        queue.add(jor);
                    }

                }
            }
        }.execute(aiRequest);


    }

    public String readID(String file_name) {
        try {
            String Message;
            FileInputStream inputStream = getActivity().openFileInput(file_name);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(streamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((Message=buffReader.readLine()) != null) {
                stringBuffer.append(Message);
            }

            return stringBuffer.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.submitVoiceButton: startVolley(mET.getText().toString());
                break;
            case R.id.relativeLayoutVoice: hideSoftKeyboard(v);
                //TODO: should there be a break here?
        }
    }


    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
