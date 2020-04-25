package pkg.es96a_app.ui.analyze;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.text.ParseException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pkg.es96a_app.R;

public class AnalyzeFragment extends Fragment {

    public String sessionID = "";
    public Button analyze_btn;

    private pkg.es96a_app.ui.analyze.AnalyzeViewModel analyzeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //analyzeViewModel = ViewModelProviders.of(this).get(pkg.es96a_app.ui.analyze.AnalyzeViewModel.class);
        return inflater.inflate(R.layout.fragment_analyze, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText text_sessionID = getView().findViewById(R.id.text_analyze_sessionID);
        final Button analyze_btn = getView().findViewById(R.id.analyze_btn);

        // create text watcher
        text_sessionID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // get the session ID
                sessionID = text_sessionID.getText().toString();
                analyze_btn.setEnabled(!sessionID.isEmpty());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // create on click listener
        // Make the request repeatedly after hitting the button
        analyze_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // DO STUFF
                Log.d("JONAS", "Button Clicked");
                getRequest(sessionID);
            }
        });
    }

    // create class to make get requests
    public void getRequest(String sessionID) {

        // instantiate client
        OkHttpClient client = new OkHttpClient();
        String url = "https://es96app.herokuapp.com/justdata?sessionID=" + sessionID;

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

                    final String txt = response.body().string();

                    // Run on the main thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // DO ACTIONS HERE
                            Log.d("JONAS","Doing stuff on the main thread.");
                            TextView textView = getView().findViewById(R.id.display_JSON);
                            textView.setText(txt);
                        }
                    });
                }
            }
        });
    }
}
