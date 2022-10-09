package com.yitu.pictureshare.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yitu.pictureshare.bean.Picture;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<List<Picture>> livePersonPictureList;

    public ProfileViewModel() {
    }

    public MutableLiveData<List<Picture>> getLivePersonPictureList() {
        if (livePersonPictureList == null) {
            livePersonPictureList = new MutableLiveData<>();
            List<Picture> pictureList = new ArrayList<>();
            livePersonPictureList.setValue(pictureList);
        }
        return livePersonPictureList;
    }

    public void setLivePersonPictureList(MutableLiveData<List<Picture>> livePersonPictureList) {
        this.livePersonPictureList = livePersonPictureList;
    }
}