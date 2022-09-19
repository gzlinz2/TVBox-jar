package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Live1211 extends Spider {

    private static final String host = "http://live.yj1211.work";

    private static String platform = "douyu";

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void init(Context context, String extend) {
        platform = extend;
        super.init(context, extend);
    }

    @Override
    public String homeContent(boolean filter) {
        JSONObject result = new JSONObject();
        try {
            String resp = OkHttpUtil.string(host + "/api/live/getAreas?platform=" + platform, null);
            JSONObject res = new JSONObject(resp);
            JSONArray list = res.getJSONArray("data");
            JSONArray classes = new JSONArray();
            JSONObject filters = new JSONObject();
            for (int i = 0; i < list.length(); i++) {
                JSONArray typeItems = list.getJSONArray(i);
                JSONArray values = new JSONArray();
                for (int j = 0; j < typeItems.length(); j++) {
                    JSONObject typeItem = typeItems.optJSONObject(j);
                    if (j == 0) {
                        String typeName = typeItem.getString("typeName");
                        String typeId = typeItem.getString("areaName");
                        JSONObject newCls = new JSONObject();
                        newCls.put("type_id", typeId);
                        newCls.put("type_name", typeName);
                        classes.put(newCls);

                        JSONArray filterList = new JSONArray();
                        filters.put(typeId, filterList);

                        JSONObject filterItem = new JSONObject();
                        filterList.put(filterItem);

                        filterItem.put("key", "area");
                        filterItem.put("name", "分类");
                        filterItem.put("value", values);

                    }
                    JSONObject kv = new JSONObject();
                    kv.put("n", typeItem.optString("areaName"));
                    kv.put("v", typeItem.optString("areaName"));
                    values.put(kv);
                }
            }
            result.put("class", classes);
            result.put("filters", filters);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    /**
     * 平台推荐 http://live.yj1211.work/api/live/getRecommendByPlatform?platform=douyu&page=1&size=20
     *
     * @return
     */
    @Override
    public String homeVideoContent() {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/api/live/getRecommendByPlatform?page=1&size=20&platform=" + platform;
            JSONArray list = new JSONArray();
            JSONArray data = new JSONObject(OkHttpUtil.string(url, null)).getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject vr = data.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", vr.optString("roomId"));
                vod.put("vod_name", vr.getString("ownerName"));
                vod.put("vod_pic", vr.getString("roomPic"));
                vod.put("vod_remarks", vr.getString("categoryName") + "|" + vr.getLong("online") / 10000 + "w");
                list.put(vod);
            }
            result.put("list", list);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/api/live/getRecommendByPlatformArea?platform=" + platform;
            url = url + "&area=";
            if (extend.containsKey("area")) {
                url = url + extend.get("area");
            } else {
                url = url + tid;
            }

            url = url + "&page=" + pg + "&size=20";
            JSONArray data = new JSONObject(OkHttpUtil.string(url, null)).getJSONArray("data");
            JSONArray vods = new JSONArray();
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", item.optString("roomId"));
                vod.put("vod_name", item.getString("ownerName"));
                vod.put("vod_pic", item.getString("roomPic"));
                vod.put("vod_remarks", item.getString("categoryName") + "|" + item.getLong("online") / 10000 + "w");
                vods.put(vod);
            }
            int parseInt = Integer.parseInt(pg);
            result.put("page", parseInt);
            if (vods.length() == 20) {
                parseInt++;
            }
            result.put("pagecount", parseInt);
            result.put("limit", 20);
            result.put("total", Integer.MAX_VALUE);
            result.put("list", vods);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String detailContent(List<String> ids) {
        JSONObject result = new JSONObject();
        try {
            String url = host + "/api/live/getRoomInfo?platform=" + platform + "&roomId=" + ids.get(0);
            JSONObject data = new JSONObject(OkHttpUtil.string(url, null)).optJSONObject("data");
            JSONObject vod = new JSONObject();
            vod.put("vod_id", data.getString("roomId"));
            vod.put("vod_name", data.getString("roomName"));
            vod.put("vod_pic", data.getString("roomPic"));
            vod.put("type_name", data.optString("categoryName"));
            vod.put("vod_area", data.optString("ownerName"));
            vod.put("vod_remarks", data.optLong("online") / 10000 + "w");
            List<String> reUrls = new LinkedList<>();

            if(platform.equals("huya")){
                String  playurl ="https://mp.huya.com/cache.php?m=Live&do=profileRoom" + "&roomid=" + ids.get(0);
                JSONObject flv = new JSONObject(OkHttpUtil.string(playurl, null)).getJSONObject("data").getJSONObject("stream").getJSONObject("flv");
                JSONArray playData =flv.optJSONArray("multiLine");
                JSONArray playData_Name =flv.optJSONArray("rateArray");
                if(playData.length()>0){
                    for(int i=0;i<playData.length();i++){
                        JSONObject src1 = playData.getJSONObject(i);
                        JSONObject src2 = playData_Name.getJSONObject(i);
                        reUrls.add(src2.getString("sDisplayName")+"$" + src1.optString("url"));
                    }
                }
            }else {
                String playurl = host + "/api/live/getRealUrl?platform=" + platform + "&roomId=" + ids.get(0);
                JSONObject playData = new JSONObject(OkHttpUtil.string(playurl, null)).optJSONObject("data");

                if (playData.has("OD")) {
                    reUrls.add("原画$" + playData.optString("OD"));
                }
                if (playData.has("HD")) {
                    reUrls.add("超清$" + playData.optString("HD"));
                }
                if (playData.has("SD")) {
                    reUrls.add("高清$" + playData.optString("SD"));
                }
                if (playData.has("LD")) {
                    reUrls.add("清晰$" + playData.optString("LD"));
                }
            }

            String urls = TextUtils.join("#", reUrls);
            vod.put("vod_play_from", platform);
            vod.put("vod_play_url", urls);
            JSONArray vods = new JSONArray();
            vods.put(vod);
            result.put("list", vods);
        } catch (Exception e) {

        }
        return result.toString();
    }

    @Override
    public String searchContent(String key, boolean quick) {
        JSONObject result = new JSONObject();
        try {
            String str2 = host + "/api/live/search?platform=" + platform + "&keyWords=" + key + "&isLive=1";
            JSONArray jSONArray = new JSONObject(OkHttpUtil.string(str2, null)).optJSONArray("data");
            JSONArray vods = new JSONArray();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                JSONObject vod = new JSONObject();
                vod.put("vod_id", jSONObject.getString("roomId"));
                vod.put("vod_name", jSONObject.getString("nickName"));
                vod.put("vod_pic", jSONObject.getString("headPic"));
                vod.put("vod_remarks", jSONObject.getString("cateName"));
                vods.put(vod);
            }
            result.put("list", vods);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        JSONObject result = new JSONObject();
        try {
            result.put("parse", "0");
            result.put("url", id);
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return result.toString();
    }

    @Override
    public boolean isVideoFormat(String url) {
        return super.isVideoFormat(url);
    }

    @Override
    public boolean manualVideoCheck() {
        return super.manualVideoCheck();
    }
}
