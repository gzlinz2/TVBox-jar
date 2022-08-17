package com.github.catvod.spider;


import android.content.Context;
import android.text.TextUtils;

import com.github.catvod.crawler.Spider;
import com.github.catvod.crawler.SpiderDebug;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Dy555 extends Spider {

    private static final String siteUrl = "https://www.555dianying.cc";

    protected JSONObject playerConfig;
    protected JSONObject filterConfig;

    protected Pattern regexCategory = Pattern.compile("/vodtype/(\\d+).html");
    protected Pattern regexVoddetail = Pattern.compile("/voddetail/(\\d+).html");
    protected Pattern regexPlay = Pattern.compile("/vodplay/(\\S+).html");
    protected Pattern regexPage = Pattern.compile("\\d+/(\\d+)");


    private final String filterString = "{\"1\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"大陆\",\"v\":\"大陆\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"法国\",\"v\":\"法国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"德国\",\"v\":\"德国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"印度\",\"v\":\"印度\"},{\"n\":\"意大利\",\"v\":\"意大利\"},{\"n\":\"西班牙\",\"v\":\"西班牙\"},{\"n\":\"加拿大\",\"v\":\"加拿大\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"爱情\",\"v\":\"爱情\"},{\"n\":\"科幻\",\"v\":\"科幻\"},{\"n\":\"恐怖\",\"v\":\"恐怖\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"悬疑\",\"v\":\"悬疑\"},{\"n\":\"冒险\",\"v\":\"冒险\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"惊悚\",\"v\":\"惊悚\"},{\"n\":\"青春\",\"v\":\"青春\"},{\"n\":\"动画\",\"v\":\"动画\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"2\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"韩国\",\"v\":\"韩国\"},{\"n\":\"香港\",\"v\":\"香港\"},{\"n\":\"台湾\",\"v\":\"台湾\"},{\"n\":\"日本\",\"v\":\"日本\"},{\"n\":\"美国\",\"v\":\"美国\"},{\"n\":\"泰国\",\"v\":\"泰国\"},{\"n\":\"英国\",\"v\":\"英国\"},{\"n\":\"新加坡\",\"v\":\"新加坡\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"古装\",\"v\":\"古装\"},{\"n\":\"战争\",\"v\":\"战争\"},{\"n\":\"青春偶像\",\"v\":\"青春偶像\"},{\"n\":\"喜剧\",\"v\":\"喜剧\"},{\"n\":\"家庭\",\"v\":\"家庭\"},{\"n\":\"犯罪\",\"v\":\"犯罪\"},{\"n\":\"动作\",\"v\":\"动作\"},{\"n\":\"奇幻\",\"v\":\"奇幻\"},{\"n\":\"剧情\",\"v\":\"剧情\"},{\"n\":\"历史\",\"v\":\"历史\"},{\"n\":\"经典\",\"v\":\"经典\"},{\"n\":\"乡村\",\"v\":\"乡村\"},{\"n\":\"情景\",\"v\":\"情景\"},{\"n\":\"商战\",\"v\":\"商战\"},{\"n\":\"网剧\",\"v\":\"网剧\"},{\"n\":\"其他\",\"v\":\"其他\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"3\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"地区\",\"key\":\"area\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"内地\",\"v\":\"内地\"},{\"n\":\"港台\",\"v\":\"港台\"},{\"n\":\"日韩\",\"v\":\"日韩\"},{\"n\":\"欧美\",\"v\":\"欧美\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}],\"5\":[{\"name\":\"年份\",\"key\":\"year\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"2022\",\"v\":\"2022\"},{\"n\":\"2021\",\"v\":\"2021\"},{\"n\":\"2020\",\"v\":\"2020\"},{\"n\":\"2019\",\"v\":\"2019\"},{\"n\":\"2018\",\"v\":\"2018\"},{\"n\":\"2017\",\"v\":\"2017\"},{\"n\":\"2016\",\"v\":\"2016\"},{\"n\":\"2015\",\"v\":\"2015\"},{\"n\":\"2014\",\"v\":\"2014\"},{\"n\":\"2013\",\"v\":\"2013\"},{\"n\":\"2012\",\"v\":\"2012\"},{\"n\":\"2011\",\"v\":\"2011\"},{\"n\":\"2010\",\"v\":\"2010\"}]},{\"name\":\"类型\",\"key\":\"class\",\"value\":[{\"n\":\"全部\",\"v\":\"\"},{\"n\":\"番剧\",\"v\":\"番剧\"},{\"n\":\"国创\",\"v\":\"国创\"},{\"n\":\"动画片\",\"v\":\"动画片\"}]},{\"name\":\"排序\",\"key\":\"by\",\"value\":[{\"n\":\"最新\",\"v\":\"time\"},{\"n\":\"人气\",\"v\":\"hits\"},{\"n\":\"评分\",\"v\":\"score\"}]}]}";

    @Override
    public void init(Context context) {
        super.init(context);
        try {
            filterConfig = new JSONObject(filterString);
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param refererUrl
     * @return
     */
    protected HashMap<String, String> getHeaders(String refererUrl) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(refererUrl)) {
            headers.put("Referer", refererUrl);
        }
        headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        return headers;
    }

    /**
     * 获取分类数据 + 首页最近更新视频列表数据
     *
     * @param filter 是否开启筛选 关联的是 软件设置中 首页数据源里的筛选开关
     * @return
     */
    @Override
    public String homeContent(boolean filter) {
        try {
            String url = siteUrl + '/';
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            Elements elements = doc.select("ul.navbar-items > li > a[href*='vodtype']");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                String href = ele.attr("href");
                Matcher mather = regexCategory.matcher(href);
                if (!mather.find() || name.contains("福利"))
                    continue;
                // 把分类的id和名称取出来加到列表里
                String id = mather.group(1).trim();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type_id", id);
                jsonObject.put("type_name", name);
                classes.put(jsonObject);
            }

            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
                Elements list = doc.select("div.module-items> a[href*='voddetail']");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.attr("title");
                    String cover = vod.selectFirst("img").attr("data-original");
                    String remark = vod.selectFirst("div.module-item-note").text();
                    Matcher matcher = regexVoddetail.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String id = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
                result.put("list", videos);
            } catch (Exception e) {
                SpiderDebug.log(e);
            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    /**
     * 获取分类信息数据
     *
     * @param tid    分类id
     * @param pg     页数
     * @param filter 同homeContent方法中的filter
     * @param extend 筛选参数{k:v, k1:v1}
     * @return
     */
    @Override
    public String categoryContent(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            int page = 1;
            if (!TextUtils.isEmpty(pg) && Integer.parseInt(pg.trim()) > 1) {
                page = Integer.parseInt(pg.trim());
            }
            Map<String, String> ext = new HashMap<>();
            ext.put("id", tid);
            ext.put("pg", "" + page);
            if (extend != null && extend.size() > 0) {
                ext.putAll(extend);
            }
//            13-内地-hits-古装-国语-A------2022.html
            String id = ext.get("id") == null ? "" : ext.get("id");
            String area = ext.get("area") == null ? "" : ext.get("area");
            String by = ext.get("by") == null ? "" : ext.get("by");
            String clazz = ext.get("class") == null ? "" : ext.get("class");
            String lang = ext.get("lang") == null ? "" : ext.get("lang");
            String spg = ext.get("pg") == null ? "" : ext.get("pg");
            String year = ext.get("year") == null ? "" : ext.get("year");
            String url = siteUrl + String.format("/vodshow/%s-%s-%s-%s-%s----%s---%s.html", id, area, by, clazz, lang, spg, year);
            SpiderDebug.log(url);

            String html = OkHttpUtil.string(url, getHeaders(siteUrl));
            Document doc = Jsoup.parse(html);
            JSONObject result = new JSONObject();
            int pageCount = 1;

            // 取页码相关信息
            Elements pageInfo = doc.select("div[id='page'] > a:last-child");
            if (pageInfo.size() > 0) {
                String href = pageInfo.attr("href");
                href = href.substring(9, href.length() - 4);
                String pageCountNum = href.split("-")[9];
                if (!TextUtils.isEmpty(pageCountNum)) {
                    pageCount = Integer.parseInt(pageCountNum);
                }
            }

            JSONArray videos = new JSONArray();
            // 取当前分类页的视频列表
            Elements list = doc.select("div.module-items > a[href*='voddetail']");
            for (int i = 0; i < list.size(); i++) {
                Element vod = list.get(i);
                String title = vod.attr("title");
                String cover = vod.selectFirst("img").attr("data-original");
                String remark = vod.selectFirst("div.module-item-note").text();
                Matcher matcher = regexVoddetail.matcher(vod.attr("href"));
                if (matcher.find()) {
                    String vodId = matcher.group(1);
                    JSONObject v = new JSONObject();
                    v.put("vod_id", vodId);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", remark);
                    videos.put(v);
                }
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 40);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 40);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 视频详情信息
     *
     * @param ids 视频id
     * @return
     */
    @Override
    public String detailContent(List<String> ids) {
        try {
            // 视频详情url
            String url = siteUrl + "/voddetail/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(OkHttpUtil.string(url, getHeaders(siteUrl)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("div.module-main img").attr("data-original");
            String title = doc.selectFirst("div.module-main h1").text();
            String desc = doc.selectFirst("div.module-main div.module-info-introduction-content >p").text();

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("vod_content", desc);

            Map<String, String> vod_play = new LinkedHashMap<>();

            // 取播放列表数据
            Elements sources = doc.select("div[id='y-playList'] span");
            Elements sourceList = doc.select("div.module-play-list");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.text();
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
//                        sourceName = flag;
                        found = true;
                        break;
                    }
                }
                if (found) {
                    String playList = "";
                    Elements playListA = sourceList.get(i).select("a.module-play-list-link");
                    List<String> vodItems = new ArrayList<>();

                    for (int j = 0; j < playListA.size(); j++) {
                        Element vod = playListA.get(j);
                        Matcher matcher = regexPlay.matcher(vod.attr("href"));
                        if (matcher.find()) {
                            String playURL = matcher.group(1);
                            String playUrlName = vod.text();
                            vodItems.add(playUrlName + "$" + playURL);
                        }
                    }
                    if (vodItems.size() > 0) {
                        playList = TextUtils.join("#", vodItems);
                        vod_play.put(sourceName, playList);
                    }
                }
            }

            if (vod_play.size() > 0) {
                String vod_play_from = TextUtils.join("$$$", vod_play.keySet());
                String vod_play_url = TextUtils.join("$$$", vod_play.values());
                vodList.put("vod_play_from", vod_play_from);
                vodList.put("vod_play_url", vod_play_url);
            }
            JSONArray list = new JSONArray();
            list.put(vodList);
            result.put("list", list);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }






    /**
     * 获取视频播放信息
     *
     * @param flag     播放源
     * @param id       视频id
     * @param vipFlags 所有可能需要vip解析的源
     * @return
     */
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            // 播放页 url
            String url = siteUrl + "/vodplay/" + id + ".html";
            SpiderDebug.log(url );
            JSONObject result = new JSONObject();
            result.put("parse", 1);
            result.put("playUrl", "");
            result.put("url", url);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    @Override
    public boolean manualVideoCheck() {
        return true;
    }

    private String[] videoFormatList = new String[]{".m3u8", ".mp4", ".mpeg", ".flv"};

    @Override
    public boolean isVideoFormat(String url) {
        url = url.toLowerCase();
        if (url.contains("=http") || url.contains("=https") || url.contains("=https%3a%2f") || url.contains("=http%3a%2f")) {
            return false;
        }
        for (String format : videoFormatList) {
            if (url.contains(format)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String searchContent(String key, boolean quick) {
        try {
            long currentTime = System.currentTimeMillis();
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=10&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(siteUrl)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("total") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("id");
                    String title = vod.getString("name");
                    String cover = vod.getString("pic");
                    JSONObject v = new JSONObject();
                    v.put("vod_id", id);
                    v.put("vod_name", title);
                    v.put("vod_pic", cover);
                    v.put("vod_remarks", "");
                    videos.put(v);
                }
            }
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }
}