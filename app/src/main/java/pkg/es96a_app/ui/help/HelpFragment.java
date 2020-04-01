package pkg.es96a_app.ui.help;

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

public class HelpFragment extends Fragment {

    private pkg.es96a_app.ui.help.HelpViewModel HelpViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HelpViewModel =
                ViewModelProviders.of(this).get(pkg.es96a_app.ui.help.HelpViewModel.class);
        return inflater.inflate(R.layout.fragment_help, container, false);
    }
}