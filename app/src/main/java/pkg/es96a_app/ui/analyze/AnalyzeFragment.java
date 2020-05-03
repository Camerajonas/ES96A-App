package pkg.es96a_app.ui.analyze;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pkg.es96a_app.MainActivity;
import pkg.es96a_app.R;

public class AnalyzeFragment extends Fragment {

    public String sessionID = "";
    public Button analyze_btn;
    public String username = "ES96";
    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<BarEntry> darkentries = new ArrayList<>();
    ArrayList<String> states = new ArrayList<>();
    ArrayList<Integer> days = new ArrayList<>();
    ArrayList<String> xAxisLabel = new ArrayList<>();
    LinkedHashSet<String> options = new LinkedHashSet<>();
    String[] optionsList;
    public int count;
    public BarChart barchart;
    BarDataSet barDataSetDark;
    BarDataSet barDataSet;

    private pkg.es96a_app.ui.analyze.AnalyzeViewModel analyzeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        createSpinner();

        return inflater.inflate(R.layout.fragment_analyze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        final Button analyze_btn = getView().findViewById(R.id.analyze_btn);
        barchart = (BarChart) getView().findViewById(R.id.bargraph);
        barchart.setNoDataText("");


        // create on click listener
        // Make the request repeatedly after hitting the button
        analyze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                Log.d("JONAS", "Button Clicked");
                hideKeyboard(getActivity());
                xAxisLabel.clear();
                getRequest(sessionID);

            }
        });
    }

    // create class to make get requests
    public void getRequest(final String sessionID) {

        // instantiate client
        OkHttpClient client = new OkHttpClient();
        String url = "https://es96app.herokuapp.com/justdata?username=" + username + "&sessionID=" + sessionID;
        Log.d("JONAS GET REQUEST", url);
        final int[] stuntC = {0};
        final int[] hardC = {0};
        final int[] frC = {0};
        final int[] ripeC = {0};
        final int[] orC = {0};
        darkentries.clear();
        entries.clear();

        // build the request
        final Request request = new Request.Builder()
                .url(url)
                .build();

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
                    int total = 0;
                    int avg = 0;

                    // Parse JSON Response
                    try {
                        JSONArray JA = new JSONArray(response.body().string());
                        //Log.d("JONAS JA Array", String.valueOf(JA));

                        // iterate through JSON array and parse out the name key
                        for (int i = 0; i < JA.length(); i++) {
                            JSONObject JO = (JSONObject) JA.get(i);
                            if (JO.has("ripenessState")) {
                                states.add(JO.get("ripenessState").toString());
                                if (JO.get("ripenessState").toString().compareTo("Stunted")==0) {
                                    stuntC[0]++;
                                } else if (JO.get("ripenessState").toString().compareTo("Hard")==0) {
                                    hardC[0]++;
                                } else if (JO.get("ripenessState").toString().compareTo("Firm-Ripe")==0) {
                                    frC[0]++;
                                } else if (JO.get("ripenessState").toString().compareTo("Ripe")==0) {
                                    ripeC[0]++;
                                } else if (JO.get("ripenessState").toString().compareTo("Over-Ripe")==0) {
                                    orC[0]++;
                                }
                            }
                            if (JO.has("ripenessDays")) {
                                days.add(Integer.parseInt(JO.get("ripenessDays").toString()));
                            }
                        }
                        // Calculate Mean
                        for(int j = 0; j < days.size(); j++)
                        {
                            total += days.get(j);
                        }
                        avg = (int) total / days.size();
                        Log.d("JONAS Average", String.valueOf(avg));
                        // Set the barchart elements
                        count = JA.length();
                        darkentries.add(new BarEntry(1f, stuntC[0]));
                        entries.add(new BarEntry(2f, hardC[0]));
                        entries.add(new BarEntry(3f, frC[0]));
                        entries.add(new BarEntry(4f, ripeC[0]));
                        entries.add(new BarEntry(5f, orC[0]));
                        xAxisLabel.add("Stunted");
                        xAxisLabel.add("Hard");
                        xAxisLabel.add("Firm-Ripe");
                        xAxisLabel.add("Ripe");
                        xAxisLabel.add("Over-Ripe");

                        barDataSetDark = new BarDataSet(darkentries, "Stunted");
                        barDataSetDark.setColors(new int[]{getResources().getColor(R.color.colorPrimaryDarker)});
                        barDataSet = new BarDataSet(entries,"Normal");
                        barDataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});

                    } catch (JSONException e) {
                        Log.d("THERE WAS AN ERROR", "");
                        e.printStackTrace();
                    }

                    final String txt = "Session ID: " + sessionID + "\n" +
                            "Total Scans: " + String.valueOf(count) + "\n" +
                            "Average Days Until Ripe: " + String.valueOf(avg);

                    // Run on the main thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // DO ACTIONS HERE
                            Log.d("JONAS","Doing stuff on the main thread.");
                            Log.d("JONAS LABELS", String.valueOf(xAxisLabel));

                            // Create Bar Chart
                            BarData data = new BarData(barDataSetDark);
                            data.addDataSet(barDataSet);

                            // Stylize the Barchart
                            data.setValueTextSize(15f);
                            data.setBarWidth(0.9f);
                            XAxis xAxis = barchart.getXAxis();
                            xAxis.setGranularity(1.1f);
                            xAxis.setLabelRotationAngle(-10);
                            xAxis.setAvoidFirstLastClipping(true);
                            xAxis.setLabelCount(5,false);
                            xAxis.setGranularityEnabled(true);
                            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
                            xAxis.setDrawGridLines(false);
                            barchart.setFitBars(false);
                            barchart.setDrawValueAboveBar(true);
                            barchart.getAxisLeft().setEnabled(false);
                            barchart.getAxisRight().setDrawGridLines(false);
                            barchart.getAxisRight().setEnabled(false);
                            barchart.getAxisLeft().setDrawLabels(false);
                            barchart.getXAxis().setCenterAxisLabels(true);
                            barchart.getLegend().setEnabled(false);
                            barchart.getDescription().setEnabled(false);

                            // Create the BarChart
                            barchart.setData(data);
                            barchart.invalidate();
                            barchart.animateY(200);

                            // Set scans count
                            TextView totalScans = getView().findViewById(R.id.totalScans);
                            totalScans.setText(txt);
                            totalScans.setVisibility(View.VISIBLE);
                            // Show Yaxis label
                            TextView yAxisTitle = getView().findViewById(R.id.yAxisTitle);
                            yAxisTitle.setVisibility(View.VISIBLE);
                            // Show plot title
                            TextView title = getView().findViewById(R.id.plotTitle);
                            title.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }
        });
    }

    public void createSpinner() {
        // instantiate client
        OkHttpClient client = new OkHttpClient();
        String url = "https://es96app.herokuapp.com/justdata?username=" + username;
        Log.d("JONAS GET SESSIONS", url);

        // build the request
        final Request request = new Request.Builder()
                .url(url)
                .build();

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
                    Log.d("JONAS", "Session Response Received");

                    // Parse JSON Response
                    try {
                        JSONArray JA = new JSONArray(response.body().string());
                        Log.d("JONAS JAlength", String.valueOf(JA.length()));

                        // iterate through JSON array and parse out the name key
                        for (int i = 0; i < JA.length(); i++) {
                            //Log.d("JONAS LOOPING","HELLO MARK");
                            JSONObject JO = (JSONObject) JA.get(i);
                            if (JO.has("sessionID")) {
                                options.add((String) JO.get("sessionID").toString());
                                //Log.d("JONAS","adding option");
                            }
                        }
                        Log.d("JONAS OPTIONS async", options.toString());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // DO ACTIONS HERE
                                // Create spinner options
                                //final String[] optionsList = {"test", "no you"};
                                // to reverse LinkedHashSet contents
                                ArrayList<String> optionsAr = new ArrayList<String>(options);
                                Collections.reverse(optionsAr);
                                final String[] optionsList = optionsAr.toArray(new String[0]);
                                Spinner spinner = (Spinner) getView().findViewById(R.id.spinner);
                                ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                                        android.R.layout.simple_spinner_dropdown_item, optionsList);
                                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(myAdapter);
                                //Performing actions
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        sessionID = optionsList[position];
                                        Log.d("JONAS SpinSession", sessionID);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });

                            }
                        });
                    } catch (JSONException e) {
                        Log.d("JONAS", "Something bad happened");
                        e.printStackTrace();
                    }
                }
            }
        });
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
