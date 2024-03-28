package com.astatin3.scoutingapp2025.ui.TBA;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TBAViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TBAViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");

    }

    public LiveData<String> getText() {
        return mText;
    }
}