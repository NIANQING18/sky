package com.sky.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.properties.LocationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LocationUtil {

    private static final String url = "https://api.map.baidu.com/geocoding/v3";

    private static final Double R = 6371D;

    @Autowired
    private LocationProperties locationProperties;


    private JSONObject getLocation(String address) {
        Map<String,String> params = new HashMap<>();
        params.put("address",address);
        params.put("output","json");
        params.put("ak", locationProperties.getAK());

        String text = HttpClientUtil.doGet(url, params);
        JSONObject jsonObject = JSON.parseObject(text);
        return jsonObject.getJSONObject("result").getJSONObject("location");
    }

    private JSONObject getShopLocation(){
        return getLocation(locationProperties.getShopAddress());
    }

    public  double getDistance(String address) {
        JSONObject shopLocation = getShopLocation();
        JSONObject userLocation = getLocation(address);

        double lng1 = Double.parseDouble(shopLocation.getString("lng"));
        double lat1 = Double.parseDouble(shopLocation.getString("lat"));

        double lng2 = Double.parseDouble(userLocation.getString("lng"));
        double lat2 = Double.parseDouble(userLocation.getString("lat"));

        double dLng = (lng1 - lng2) * Math.PI / 180;
        double dLat = (lat1 - lat2) * Math.PI / 180;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double dis = 2 * R * Math.atan2(Math.sqrt(a),Math.sqrt(1 - a));
        return dis;
    }
}
