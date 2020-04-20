package pkg.es96a_app.ui.analyze;

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

import pkg.es96a_app.R;

public class AnalyzeFragment extends Fragment {

    private pkg.es96a_app.ui.analyze.AnalyzeViewModel analyzeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //analyzeViewModel = ViewModelProviders.of(this).get(pkg.es96a_app.ui.analyze.AnalyzeViewModel.class);
        return inflater.inflate(R.layout.fragment_analyze, container, false);

    }
}
