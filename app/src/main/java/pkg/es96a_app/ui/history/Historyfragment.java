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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pkg.es96a_app.R;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    public TextView crates;

    //instantiate the stuff we want to display
    ArrayList<String> birthplace = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();

    // instantiate client
    OkHttpClient client = new OkHttpClient();
    String url = "https://es96app.herokuapp.com/justdata";
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

        // Find the crates
        //crates = getView().findViewById(R.id.crates);

        // Implement Http request
        // request for the Jsonarray
        reqJdata(client, request, 8000);
        Log.v("main", String.valueOf(birthplace.size()));
        init();

    }

//    public void parseData(){
//        for (int i= 0; i < jData.length(); i++){
//
//            Log.d("Parse", "this is working");
//
//            try {
//                JSONObject JO = (JSONObject) jData.get(i);
//
//                if (JO.has("birthplace")){
//                    birthplace.add(JO.get("birthplace").toString());
//                    Log.v("Mark", "There is a birthplace" );
//                }
//                if (JO.has("time")){
//                    time.add(JO.get("time").toString());
//                }
//
//            } catch (JSONException e) {
//                Log.v("Mark", "could not retrieve object");
//                e.printStackTrace();
//            }
//
//        }
//    }

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
                        JSONArray jData = new JSONArray(response.body().string());
                        response.body().close();

                        for (int i= 0; i < jData.length(); i++){

                                JSONObject JO = (JSONObject) jData.get(i);

                                if (JO.has("birthplace")){
                                    birthplace.add(JO.get("birthplace").toString());

                                }
                                if (JO.has("time")){
                                    time.add(JO.get("time").toString());
                                }
                        }
                        Log.v("inside", String.valueOf(birthplace.size()));

                    } catch (JSONException e) {
                        Log.d("Mark", "JsonArray was not set");
                        e.printStackTrace();
                    }
                    Log.v("outside", String.valueOf(birthplace.size()));


                }
                Log.v("outsideif", String.valueOf(birthplace.size()));
            }


        });
        Log.v("outsideclient", String.valueOf(birthplace.size()));

    }


    @SuppressLint("ResourceType")
    public void init(){

        // Find the table and sets it to table 1
        TableLayout table1 = (TableLayout) getView().findViewById(R.id.table1);

        //instantiate a tablerow and column
        TableRow [] row = new TableRow[time.size()];
        TextView [] col = new TextView[2]; //The size is dependent on # of columns

        //Create the tablerows
        for(int i =0; i< row.length; i++ ){
            row[i] =new TableRow(getContext());
            //tr_head1.setId(21);
            row[i].setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

            //This sets the text to the header
            for (int j = 0; j < col.length; j++ ) {
                col [j] = new TextView(getContext());
                if(j == 0) {
                    col[j].setText(birthplace.get(i));
                }else{
                    col[j].setText(time.get(i));
                }
                col [j].setPadding(5, 5, 5, 5);
                row[i].addView(col[j]);

            }

            //Set the header to the table
            table1.addView(row[i], new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

        }


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