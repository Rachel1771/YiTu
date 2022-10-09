package com.yitu.pictureshare.ui.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yitu.pictureshare.IndexActivity;
import com.yitu.pictureshare.bean.Picture;
import com.yitu.pictureshare.common.PictureParseTool;
import com.yitu.pictureshare.databinding.FragmentProfileBinding;
import com.yitu.pictureshare.ui.adapter.PersonPictureAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ProfileFragment extends Fragment {

    String userId;
    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    PersonPictureAdapter pictureAdapter = new PersonPictureAdapter(null, getActivity());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        pictureAdapter = new PersonPictureAdapter(getActivity(), profileViewModel);
        userId = ((IndexActivity)getActivity()).getUserID();
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerviewProfile;
        recyclerView.setAdapter(pictureAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        profileViewModel.getLivePersonPictureList().observe(getViewLifecycleOwner(), new Observer<List<Picture>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Picture> pictures) {
                pictureAdapter.setPictureList(pictures);
                pictureAdapter.notifyDataSetChanged();
            }
        });
        getPersonPicture();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getPersonPicture(){

//        String url="http://35.241.95.124:8081/picture/getPersonPictures";
        String url="http://10.0.2.2:8081/picture/getPersonPictures";
        OkHttpClient client=new OkHttpClient();
        HashMap<String,String> map=new HashMap<>();
        map.put("id", userId);
        Gson gson=new Gson();
        String data=gson.toJson(map);
        //添加请求体
        RequestBody formBody;
        formBody=RequestBody.create(IndexActivity.JSON,data);
        Request request=new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        System.out.println(request.toString());
        //异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("person", "连接失败" + e.getLocalizedMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if(result!=null) {
                    Log.d("person", result);
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    list.addAll(PictureParseTool.jsonJXGetPictureNoName(result));
                    Message message = new Message();
                    message.obj = list;
                    message.what = 1;
                    handler.sendMessage(message);
                    Log.d("person", "成功");
                }
                else
                    Log.d("MainActivityPost---", "失败");
                response.body().close();
            }
        });
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    List<Picture> pictureList = new ArrayList<>() ;
                    List<Map<String, Object>> list = ( List<Map<String, Object>>) msg.obj;
                    for(int i = 0 ; i < list.size() ; i ++){
                        Map<String,Object> map = list.get(i);
                        Picture picture = new Picture() ;
                        picture.setId(map.get("id").toString());
                        picture.setUserId( map.get("userId").toString());
                        picture.setLikeNum(Integer.valueOf(map.get("likeNum").toString()));
                        picture.setPictureData( PictureParseTool.base64ToBitmap(map.get("pictureData").toString()));
                        picture.setTitle(map.get("title").toString());
                        pictureList.add(picture);
                        Log.d("person", String.valueOf(picture.getId()));
                    };
                    MutableLiveData<List<Picture>> livePictureList = profileViewModel.getLivePersonPictureList();
                    livePictureList.setValue(pictureList);
                    profileViewModel.setLivePersonPictureList(livePictureList);
                    break;
            }
        }
    };
}