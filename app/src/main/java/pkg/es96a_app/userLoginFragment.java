/** Tutorial source : https://www.youtube.com/watch?v=x0I5vJfaRIU **/

package pkg.es96a_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import pkg.es96a_app.MainActivity;
import pkg.es96a_app.R;

public class userLoginFragment extends Fragment implements View.OnClickListener {

    public Button loginButton;
    public EditText loginUsername, loginPassword;

    // when the page is opened / clicked, the xml file is inflated and the page shows up
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_userlogin, container, false);
    }

    // once the page is created (i.e. the xml file has been inflated), we can use the page
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // initialize variables
        // may have to use "getView().findViewById(...)" instead of "view.findViewById(...)" here
        loginUsername = view.findViewById(R.id.loginUsername);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginButton = view.findViewById(R.id.loginButton);

        // onClickListeners wait for a click to do something
        loginButton.setOnClickListener(this);
    }

    // When the login button is clicked, the method below is notified. If we have multiple
    // OnClickListeners on login, this method will be notified, but we won't know what notified it.
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {

        // the if statement gets the ID of the view to notify the OnClick method
        if (view.getId() == R.id.loginButton) {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}
