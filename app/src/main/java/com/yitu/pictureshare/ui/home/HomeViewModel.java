package com.yitu.pictureshare.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.yitu.pictureshare.bean.Picture;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Picture>> livePictureList;

    public HomeViewModel() {
    }

    public MutableLiveData<List<Picture>> getLivePictureList() {
        if (livePictureList == null) {
            livePictureList = new MutableLiveData<>();
            List<Picture> pictureList = new ArrayList<>();
            livePictureList.setValue(pictureList);
        }
        return livePictureList;
    }

    public void setLivePictureList(MutableLiveData<List<Picture>> pictureList) {
        this.livePictureList = pictureList;
    }
}