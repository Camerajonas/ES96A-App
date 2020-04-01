package pkg.es96a_app.ui.analyze;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AnalyzeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AnalyzeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}