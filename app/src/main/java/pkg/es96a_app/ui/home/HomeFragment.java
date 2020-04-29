package pkg.es96a_app.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pkg.es96a_app.MainActivity;
import pkg.es96a_app.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    public TextView TextViewClassification;
    public String sessionID = "";
    public String name;
    public String names = "";
    public String newID = "";
    public JSONObject mostRecentJO;
    public String timeString = "";
    // Create Date Formatting Objects
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
    public Date convertedDate = null;
    //public Date mostRecentDate = new Date();
    public Date mostRecentDate;
    int count = 0;
    public TextView counter;
    // instantiate client
    OkHttpClient client = new OkHttpClient();
    String url = "https://es96app.herokuapp.com/justdata";
    String url_post = "https://es96app.herokuapp.com/test";
    // build the request
    final Request request = new Request.Builder()
            .url(url)
            .build();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // instantiate mostRecentDate outside of repeated code
        try {
           mostRecentDate = formatter.parse("2000-01-01:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.d("JONAS CURRENT DATE",String.valueOf((mostRecentDate)));

        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // This section updates the text of a text view after
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("JONAS","The view is created.");

        // Find the views for everything
        counter = getView().findViewById(R.id.counter);
        final EditText text_sessionID = getView().findViewById(R.id.text_sessionID);
        final Button scan_btn = getView().findViewById(R.id.scan_btn);

        // create text watcher
        text_sessionID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // get the session ID
                sessionID = text_sessionID.getText().toString().replaceAll(" ", "");
                scan_btn.setEnabled(!sessionID.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Log.d("JONAS SESSION ID", sessionID.toString());

        // Implement Http request
        // Find TextView
        TextViewClassification = getView().findViewById(R.id.text_classification);

        // Make the request repeatedly after hitting the button
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(getActivity());
                repeatRequest(client, request, 8000);
            }
        });
    }

    // this class takes a client and a request and repeats the request intermittently
    public void repeatRequest(final OkHttpClient client, Request request, int milliseconds) {
        // Display Refresh Count
        counter.setText(String.valueOf(count));

        // New Call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("JONAS", "FAILURE TO RECEIVE RESPONSE");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("JONAS", "Get Response Received");


                    // Parse JSON Object
                    try {
                        JSONArray JA = new JSONArray(response.body().string());
                        //Log.d("JONAS JA Array", String.valueOf(JA));
                        names = "";
                        // iterate through JSON array and parse out the name key
                        for (int i = 0; i < JA.length(); i++) {
                            JSONObject JO = (JSONObject) JA.get(i);
                            if (JO.has("time")) {
                                name = JO.get("username") + "\n";
                                names = names + name;
                                //Log.d("JONAS NAME",name);

                                // Pull out the time stamp and convert to date object
                                timeString = String.valueOf(JO.get("time"));
                                try {
                                    convertedDate = (Date) formatter.parse(timeString);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //Log.d("JONAS DATE",String.valueOf(convertedDate));

                                if (mostRecentDate.compareTo(convertedDate) < 0) {
                                    mostRecentDate = convertedDate;
                                    mostRecentJO = JO;
                                    Log.d("JONAS JSON Object", String.valueOf(mostRecentJO));
                                }
                            }
                        }
                        // if the new names string is not different from the old one then the view
                        // is not altered
                        if (newID != mostRecentJO.get("_id").toString()) {
                            Log.d("JONAS ID CHECK", String.valueOf(newID != mostRecentJO.get("_id").toString()));
                            newID = randomString(24);
                            count++; // Update counter

                            final String fnl_str = mostRecentJO.get("username").toString(); // declare string to be printed
                            Log.d("JONAS FINAL STRING",fnl_str);

                            // Run on the main thread
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // DO ACTIONS HERE

                                    postData(mostRecentJO, client);
                                    // Set the text view to some text
                                    TextViewClassification.setText(fnl_str);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        Log.d("THERE WAS AN ERROR", "");
                        e.printStackTrace();
                    }
                }
            }
        });

        // refresh after 5 seconds
        refresh(milliseconds);
    }

    // This class implements a post request
    public void postData(JSONObject JO, OkHttpClient client) {

        Log.d("JONAS POST", "POSTDATA Called");

        // Append the session ID to the JSON object
        try {
            JO.put("_id", newID);
            JO.put("username", "JONAS");
            JO.put("sessionID", sessionID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JONAS JO FOR POST", JO.toString());
        MediaType mediaType = MediaType.parse("application/json");
        //RequestBody body = RequestBody.create(mediaType, "{\n \"name\" : \"JONAS\",\n \"birthplace\" : \"Pocatello\"\n}");
        RequestBody body = RequestBody.create(mediaType, String.valueOf(JO));

        // build the request
        Request postRequest = new Request.Builder()
                .url(url_post)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Content-Type", "text/plain")
                .build();

        // Make the request
        client.newCall(postRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("JONAS", "FAILURE TO RECEIVE POST RESPONSE");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("JONAS", "POST Response Received");
                    Log.d("JONAS Post Response", response.toString());

                }
            }
        });
    }

    // this class implements the intermittent refreshing
    private void refresh(final int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                repeatRequest(client, request, milliseconds);
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }

    // this class generates a random string to be used as JSON ID
    public static String randomString(int i) {
        final String characters = "abcdefghhijklmnopqrstuvwxyz1234567890";
        StringBuilder result = new StringBuilder();
        while (i > 0) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            i--;
        }
        return result.toString();
    }

    public static void hideKeyboard(Activity activity) {
        // from https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}


