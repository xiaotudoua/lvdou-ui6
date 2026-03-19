package com.lvdoui6.android.tv.lvdou;

import android.util.Log;

import com.orhanobut.hawk.Hawk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HawkMovie {

    private static final String MOVIE_DETAILS_KEY = "movie_details";

    // 保存影片详情到 Hawk 中
    public static void saveMovieDetails(String name, String introduction) {
        try {
            if (introduction.isEmpty() || introduction.equals("null")) return;
            List<MovieDetail> movieDetails = getAllMovieDetails();
            movieDetails.add(new MovieDetail(name, introduction));
            saveMovieDetailsList(movieDetails);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "saveMovieDetails: 存储失败");
            // 可以在这里处理存储异常的情况，比如记录日志或者回滚操作
        }
    }

    // 从 Hawk 中获取所有影片详情
    public static List<MovieDetail> getAllMovieDetails() {
        List<MovieDetail> movieDetails = new ArrayList<>();
        try {
            String detailsJson = Hawk.get(MOVIE_DETAILS_KEY, "[]");
            JSONArray jsonArray = new JSONArray(detailsJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("name");
                String introduction = jsonObject.getString("introduction");
                movieDetails.add(new MovieDetail(name, introduction));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("TAG", "saveMovieDetails: 获取失败");
            // 可以在这里处理解析异常的情况，比如记录日志或者返回空列表
        }
        return movieDetails;
    }

    // 根据影片名称从 Hawk 中获取影片详情
    public static String getMovieDetailByName(String name) {
        List<MovieDetail> movieDetails = getAllMovieDetails();
        for (MovieDetail detail : movieDetails) {
            if (detail.getName().equals(name)) {
                if (!Objects.equals(detail.getIntroduction(), "null") && !detail.getIntroduction().isEmpty()){
                    return detail.getIntroduction();
                }
            }
        }
        return null; // 如果没有找到对应的影片详情，返回null
    }

    // 保存影片详情列表到 Hawk 中
    private static void saveMovieDetailsList(List<MovieDetail> movieDetails) {
        JSONArray jsonArray = new JSONArray();
        for (MovieDetail detail : movieDetails) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", detail.getName());
                jsonObject.put("introduction", detail.getIntroduction());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "saveMovieDetails: 存储失败");
            }
        }
        Log.d("TAG", "saveMovieDetailsList: " + jsonArray.toString());
        Hawk.put(MOVIE_DETAILS_KEY, jsonArray.toString());
    }

    // 内部类，表示影片详情
    public static class MovieDetail {
        private final String name;
        private final String introduction;

        public MovieDetail(String name, String introduction) {
            this.name = name;
            this.introduction = introduction;
        }

        public String getName() {
            return name;
        }

        public String getIntroduction() {
            return introduction;
        }
    }
}