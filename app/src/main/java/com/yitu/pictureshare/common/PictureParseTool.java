package com.yitu.pictureshare.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureParseTool {

    public static String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream btString = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, btString);
        byte[] bytes = btString.toByteArray();
        return Base64.encodeToString(bytes,Base64.URL_SAFE);
    }

    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = Base64.decode(base64String.trim(), Base64.URL_SAFE);
        return BitmapFactory.decodeByteArray(decode, 0, decode.length);
    }

    public static List<Map<String, Object>> jsonJXGetPicture(String data) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(data != null) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {

                    Long id = jsonArray.getJSONObject(i).getLong("id");
                    Long userId = jsonArray.getJSONObject(i).getLong("userId");
                    String title = jsonArray.getJSONObject(i).getString("title");
                    Integer likeNum = jsonArray.getJSONObject(i).getInt("likeNum");
                    String pictureData = jsonArray.getJSONObject(i).getString("pictureData");
                    String author = jsonArray.getJSONObject(i).getString("name");

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("userId", userId);
                    map.put("title",title);
                    map.put("likeNum",likeNum);
                    map.put("pictureData",pictureData);
                    map.put("author", author);
                    list.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<Map<String, Object>> jsonJXGetPictureNoName(String data) {//解析json数据

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if(data != null) {
            try {
                JSONArray jsonArray = new JSONArray(data);
                for (int i = 0; i < jsonArray.length(); i++) {

                    Long id = jsonArray.getJSONObject(i).getLong("id");
                    Long userId = jsonArray.getJSONObject(i).getLong("userId");
                    String title = jsonArray.getJSONObject(i).getString("title");
                    Integer likeNum = jsonArray.getJSONObject(i).getInt("likeNum");
                    String pictureData = jsonArray.getJSONObject(i).getString("pictureData");

                    Map<String, Object> map = new HashMap<>();
                    map.put("id", id);
                    map.put("userId", userId);
                    map.put("title",title);
                    map.put("likeNum",likeNum);
                    map.put("pictureData",pictureData);
                    list.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
