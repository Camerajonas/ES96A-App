package pkg.es96a_app.ui.history;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pkg.es96a_app.R;

public class Historyfragment extends Fragment {

    private HistoryViewModel historyViewModel;
    public TextView crates;
    public JSONArray jData;



    // instantiate client
    OkHttpClient client = new OkHttpClient();
    String url = "https://es96app.herokuapp.com/justdata?username=Jamie+AvoScanner";
    String url_post = "https://es96app.herokuapp.com/test";
    // build the request
    final Request request = new Request.Builder()
            .url(url)
            .build();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        final TextView textView = root.findViewById(R.id.text_history);
        // I have commented this line because it was over writing the layout info
        // textView.setText(getText(R.string.help_page));
        return root;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("Mark","The view is created.");

        // Find the crates
        //crates = getView().findViewById(R.id.crates);

        // Implement Http request
        // request for the Jsonarray
        reqJdata(client, request, 8000);

        //instantiate the stuff we want to display
        ArrayList<String> birthplace = new ArrayList<String>();



        init();

    }

    public void reqJdata(final OkHttpClient client, Request request, int milliseconds){

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Mark", "Did not connect to api");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    try {
                        jData = new JSONArray(response.body().string());
                    } catch (JSONException e) {
                        Log.d("Mark", "JsonArray was not set");
                        e.printStackTrace();
                    }


//                    Log.d("Mark",);
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            // DO ACTIONS HERE
//
//                            // Set the text view to some text
//
//                        }
//                    });

                }
            }
        });

        refresh(milliseconds);
    }

    @SuppressLint("ResourceType")
    public void init(){

        // Find the table and sets it to table 1
        TableLayout table1 = (TableLayout) getView().findViewById(R.id.table1);


        //Create the tablerows
        TableRow tr_head1 =new TableRow(getContext());
            tr_head1.setId(21);
            tr_head1.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        //This sets the text to the header
        TextView text = new TextView(getContext());
        text.setId(32);
        text.setText("hi bitch");
        text.setPadding(5, 5, 5, 5);
        tr_head1.addView(text);

        //Set the header to the table
        table1.addView(tr_head1, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

    }

    // this class implements the intermittent refreshing
    private void refresh(final int milliseconds) {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                reqJdata(client, request, milliseconds);
            }
        };
        handler.postDelayed(runnable, milliseconds);
    }
}