package pkg.es96a_app.ui.history;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import pkg.es96a_app.R;
import pkg.es96a_app.ui.history.HistoryViewModel;

public class Historyfragment extends Fragment {

    private HistoryViewModel historyViewModel;



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




//        @Override
//        protected String doInBackground(String... params) {
//            String urlString = params[0]; // URL to call
//            String data = params[1]; //data to post
//            OutputStream out = null;
//
//            //establishes the connection
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                out = new BufferedOutputStream(urlConnection.getOutputStream());
//
//                //starts writing the actual data
//                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
//                writer.write(data);
//                writer.flush();
//                writer.close();
//                out.close();
//
//                urlConnection.connect();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }

}
