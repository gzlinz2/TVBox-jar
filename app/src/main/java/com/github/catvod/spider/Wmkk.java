package com.github.catvod.spider;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

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

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 蓝光影院
 * <p>
 * Author: 匿名20220729
 */
public class Wmkk extends Spider {
    private static final String siteUrl = "https://www.wanmeikk.film";

    /**
     * 播放源配置
     */
    private JSONObject playerConfig;
    /**
     * 筛选配置
     */
    private JSONObject filterConfig;

    private Pattern regexCategory = Pattern.compile("/category/(\\d+).html"); //分类
    private Pattern regexVid = Pattern.compile("/project/(\\d+).html");  //详情页
    private Pattern regexPlay = Pattern.compile("/play/(\\d+)-(\\d+)-(\\d+).html"); //播放页
    private Pattern regexPage = Pattern.compile("/category/(\\S+).html"); //筛选页

    @Override
    public void init(Context context) {
        super.init(context);
        try {
              playerConfig = new JSONObject("{\"wanmeikk\":{\"show\":\"一路向北\",\"pu\":\"https://www.wanmeikk.film/dplayer.php?url=\",\"sn\":0,\"or\":999},\"fantasy\":{\"show\":\"七里香\",\"pu\":\"https://www.wanmeikk.film/dplayer.php?url=\",\"sn\":0,\"or\":999},\"1080P\":{\"show\":\"告白气球\",\"pu\":\"https://www.wanmeikk.film/dplayer.php?url=\",\"sn\":0,\"or\":999},\"wanmei\":{\"show\":\"兰亭序\",\"pu\":\"https://www.wanmeikk.film/dplayer.php?url=\",\"sn\":0,\"or\":999},\"fuckplayer\":{\"show\":\"晴天\",\"pu\":\"https://www.wanmeikk.film/dplayer.php?url=\",\"sn\":0,\"or\":999}}");
            //playerConfig = new JSONObject("{\"duoduozy\":{\"show\":\"蓝光专线\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"sohu\":{\"show\":\"搜狐\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"qq\":{\"show\":\"腾讯\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"bilibili\":{\"show\":\"哔哩\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"youku\":{\"show\":\"优酷\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"qiyi\":{\"show\":\"爱奇艺\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"mgtv\":{\"show\":\"芒果\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"xigua\":{\"show\":\"西瓜\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"},\"pptv\":{\"show\":\"PPTV\",\"des\":\"\",\"ps\":\"0\",\"parse\":\"https://player.tjomet.com/lgyy/?url=\"}}");
            filterConfig = new JSONObject("{}");
        } catch (JSONException e) {
            SpiderDebug.log(e);
        }
    }

    /**
     * 爬虫headers
     *
     * @param url
     * @return
     */
    protected HashMap<String, String> getHeaders(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("method", "GET");
        if (!TextUtils.isEmpty(url)) {
            headers.put("Referer", url);
        }
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("upgrade-insecure-requests", "1");
        headers.put("DNT", "1");
        headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
        headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headers.put("accept-language", "zh-CN,zh;q=0.9");
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
            Document doc = Jsoup.parse(new URL(siteUrl).openStream(), "utf-8",OkHttpUtil.string(siteUrl, getHeaders(siteUrl)));
            // 分类节点
            Elements elements = doc.select("ul.dropdown li a");
            JSONArray classes = new JSONArray();
            for (Element ele : elements) {
                String name = ele.text();
                boolean show = true;
                if (filter) {
                    show = name.equals("电影") ||
                            name.equals("欧美剧") ||
                            name.equals("综艺") ||
                            name.equals("动漫") ||
                            name.equals("韩剧");
                }
                if (show) {
                    Matcher mather = regexCategory.matcher(ele.attr("href"));
                    if (!mather.find())
                        continue;
                    String id = mather.group(1).trim();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type_id", id);
                    jsonObject.put("type_name", name);
                    classes.put(jsonObject);
                }
            }
            JSONObject result = new JSONObject();
            if (filter) {
                result.put("filters", filterConfig);
            }
            result.put("class", classes);
            try {
                // 取首页推荐视频列表
              //  Elements list = doc.select("div.stui-vodlist__box");
                Element homeList = doc.select("ul.stui-vodlist").get(0);
                Elements list = homeList.select("div.stui-vodlist__box");
                JSONArray videos = new JSONArray();
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a").attr("href"));
                    if (!matcher.find())
                        continue;
                    String title = vod.selectFirst("a").attr("title");
                    String cover = vod.selectFirst("a ").attr("data-original");
                    String remark = vod.selectFirst("a span.pic-text").text();
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                       cover = siteUrl + cover;

                    }
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
            String[] urlParams = new String[]{"", "", "", "", "", "", "", "", "", "", "", ""};
            urlParams[0] = tid;
            urlParams[1] = pg;
            if (extend != null && extend.size() > 0) {
                for (Iterator<String> it = extend.keySet().iterator(); it.hasNext(); ) {
                    String key = it.next();
                    String value = extend.get(key);
                    if (value.trim().length() == 0)
                        continue;
                    urlParams[Integer.parseInt(key)] = URLEncoder.encode(value);
                }
            }
            // 获取分类数据的url
            String url = siteUrl + "/category/"+tid+"-"+pg+".html";
            String html = OkHttpUtil.string(url, getHeaders(url));
            Document doc = Jsoup.parse(new URL(url).openStream(), "utf-8",OkHttpUtil.string(url, getHeaders(url)));

            JSONObject result = new JSONObject();
            int pageCount = 0;
            int page = -1;

            Elements pageInfo = doc.select("ul.stui-page li");
            if (pageInfo.size() == 0) {
                page = Integer.parseInt(pg);
                pageCount = page;
            } else {
                for (int i = 0; i < pageInfo.size(); i++) {
                    Element li = pageInfo.get(i);
                    Element a = li.selectFirst("a");
                    if (a == null)
                        continue;
                    String name = a.text();
                    if (page == -1 && li.hasClass("active")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            page = Integer.parseInt(matcher.group(1).split("-")[1]);
                        } else {
                            page = 0;
                        }
                    }
                    if (name.equals("尾页")) {
                        Matcher matcher = regexPage.matcher(a.attr("href"));
                        if (matcher.find()) {
                            pageCount = Integer.parseInt(matcher.group(1).split("-")[1]);
                        } else {
                            pageCount = 0;
                        }
                        break;
                    }
                }
            }

            JSONArray videos = new JSONArray();
            if (!html.contains("没有找到您想要的结果哦")) {
                Elements list = doc.select("div.stui-vodlist__box ");
                for (int i = 0; i < list.size(); i++) {
                    Element vod = list.get(i);
                    String title = vod.selectFirst("a").attr("title");
                    String cover = vod.selectFirst("a").attr("data-original");
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
                    String remark = vod.selectFirst("a span.pic-text").text();
                    Matcher matcher = regexVid.matcher(vod.selectFirst("a").attr("href"));
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
            }
            result.put("page", page);
            result.put("pagecount", pageCount);
            result.put("limit", 58);
            result.put("total", pageCount <= 1 ? videos.length() : pageCount * 58);

            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }


    private static String Regex(Pattern pattern, String content) {
        if (pattern == null) {
            return content;
        }
        try {
            Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1).trim();
            }
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return content;
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
            String url = siteUrl + "/project/" + ids.get(0) + ".html";
            Document doc = Jsoup.parse(new URL(url).openStream(), "utf-8",OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONObject vodList = new JSONObject();

            // 取基本数据
            String cover = doc.selectFirst("a.picture img").attr("data-original");
            if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                cover = siteUrl + cover;
            }
            String title = doc.selectFirst("div.stui-content__detail h1.title").text();
            String category = "", area = "", year = "", remark = "", director = "", actor = "", desc = "";
           Elements data = doc.select("p.data");
            desc = doc.selectFirst("span.detail-content").text().trim();
            category =doc.select("a").get(0).text().trim();
            year=doc.select("a").get(0).text().trim();
            area =doc.select("a").get(0).text().trim();
            //year = Regex(Pattern.compile("上映：(\\S+)"), data.get(0).text());
            actor = Regex(Pattern.compile("主演：(\\S+)"), data.nextAll().text());
            director = Regex(Pattern.compile("导演：(\\S+)"), data.nextAll().text());
            // remark=data.select("div.module-info-item-content").text().trim();

            vodList.put("vod_id", ids.get(0));
            vodList.put("vod_name", title);
            vodList.put("vod_pic", cover);
            vodList.put("type_name", category);
            vodList.put("vod_year", year);
            vodList.put("vod_area", area);
            vodList.put("vod_remarks", remark);
            vodList.put("vod_actor", actor);
            vodList.put("vod_director", director);
            vodList.put("vod_content", desc);


           Map<String, String> vod_play = new TreeMap<>(new Comparator<String>() {
               @Override
                public int compare(String o1, String o2) {
                    try {
                        int sort1 = playerConfig.getJSONObject(o1).getInt("or");
                        int sort2 = playerConfig.getJSONObject(o2).getInt("or");

                        if (sort1 == sort2) {
                            return 1;
                        }
                        return sort1 - sort2 > 0 ? 1 : -1;
                    } catch (JSONException e) {
                        SpiderDebug.log(e);
                    }
                    return 1;
                }
            });



            // 取播放列表数据
            Elements sources = doc.select(" div.stui-pannel__head.bottom-line>h3");
            //System.out.println("sources:" + sources);
            Elements sourceList = doc.select("ul.stui-content__playlist");

            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
              // String sourceName = source.attr("h3");
               // String sourceName = sources.select("h3").text().trim();
                String sourceName = source.text();
                boolean found = false;
                for (Iterator<String> it = playerConfig.keys(); it.hasNext(); ) {
                    String flag = it.next();
                    if (playerConfig.getJSONObject(flag).getString("show").equals(sourceName)) {
                        //sourceName = flag;
                        sourceName = playerConfig.getJSONObject(flag).getString("show");
                        found = true;
                        break;
                    }
                }
                if (!found)
                    continue;
                String playList = "";
                Elements playListA = sourceList.get(i).select("a");
                List<String> vodItems = new ArrayList<>();

                for (int j = 0; j < playListA.size(); j++) {
                    Element vod = playListA.get(j);
                    Matcher matcher = regexPlay.matcher(vod.attr("href"));
                    if (!matcher.find())
                        continue;
                    String playURL = matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3);
                    vodItems.add(vod.text() + "$" + playURL);
                }
                if (vodItems.size() > 0)
                    playList = TextUtils.join("#", vodItems);

                if (playList.length() == 0)
                    continue;

                vod_play.put(sourceName, playList);
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

    private final Pattern urlt = Pattern.compile("\"url\": *\"([^\"]*)\",");
    private final Pattern token = Pattern.compile("\"token\": *\"([^\"]*)\"");
    private final Pattern vkey = Pattern.compile("\"vkey\": *\"([^\"]*)\",");
    private final Pattern urls = Pattern.compile("urls = *\'([^\']*)\';");
    @Override
    public String playerContent(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject headers = new JSONObject();
           // headers.put("Referer", "https://www.wanmeikk.film");
            headers.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
          //  headers.put("Accept", " text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
          //  headers.put("Accept-Language", " zh-CN,zh;q=0.9,en-GB;q=0.8,en-US;q=0.7,en;q=0.6");
         //   headers.put("Accept-Encoding", " gzip, deflate");
            String url = siteUrl + "/play/" + id + ".html";
            Elements allScript = Jsoup.parse(new URL(url).openStream(), "utf-8",OkHttpUtil.string(url, getHeaders(url))).select("script");
            JSONObject result = new JSONObject();
            for (int i = 0; i < allScript.size(); i++)
            {
                String scContent = allScript.get(i).html().trim();
                if (scContent.startsWith("var player_"))
                {
                    JSONObject player = new JSONObject(scContent.substring(scContent.indexOf('{'), scContent.lastIndexOf('}') + 1));
                    if (playerConfig.has(player.getString("from")))
                    {
                        JSONObject pCfg = playerConfig.getJSONObject(player.getString("from"));
                        String jxurl = "https://www.wanmeikk.film/dplayer.php?url=" + player.getString("url");
                        Document doc = Jsoup.parse(new URL(jxurl).openStream(), "utf-8", OkHttpUtil.string(jxurl, getHeaders(jxurl)));
                        Elements script = doc.select("body > script[type=text/javascript]");
                        for (int j = 0; j < script.size(); j++)
                        {
                            String Content = script.get(j).html().trim();
                            Matcher matcher = urls.matcher(Content);
                            if (matcher.find()) {
                                String urls = matcher.group(1);
                               // String zlurl = new String(Base64.decode(urls.substring(8).getBytes(), 0));
                                result.put("url", urls);
                                result.put("header", headers.toString());
                                result.put("parse", 0);
                                result.put("playUrl", "");
                                break;
                            }


                        }

                    }
                    break;
                }

            }
            return result.toString();
        } catch (Exception e) {
            SpiderDebug.log(e);
        }
        return "";
    }

    /**
     * 搜索
     *
     * @param key
     * @param quick 是否播放页的快捷搜索
     * @return
     */
    @Override
    public String searchContent(String key, boolean quick) {
        try {
            if (quick)
                return "";
            long currentTime = System.currentTimeMillis();
            String url = siteUrl + "/index.php/ajax/suggest?mid=1&wd=" + URLEncoder.encode(key) + "&limit=10&timestamp=" + currentTime;
            JSONObject searchResult = new JSONObject(OkHttpUtil.string(url, getHeaders(url)));
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            if (searchResult.getInt("total") > 0) {
                JSONArray lists = new JSONArray(searchResult.getString("list"));
                for (int i = 0; i < lists.length(); i++) {
                    JSONObject vod = lists.getJSONObject(i);
                    String id = vod.getString("id");
                    String title = vod.getString("name");
                    String cover = vod.getString("pic");
                    if (!TextUtils.isEmpty(cover) && !cover.startsWith("http")) {
                        cover = siteUrl + cover;
                    }
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
