package com.example.customschedule;

import android.support.annotation.WorkerThread;
import android.support.test.espresso.base.MainThread;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * @author wangyu
 * @date 18-4-8
 * @describe TODO
 */

public class TestGetInfo {

    private static final String HOST = "www.haodou.com";
    private static final String ACCEPT_ENCODING = "gzip, deflate";
    private static final String MAIN_URL = "http://www.haodou.com/recipe/all/p-2/";
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0";

    private OkHttpClient mOkHttpClient = null;

    @Test
    @MainThread
    public void Test() {
        init();
        spider();
    }

    private void init() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        OkHttpUtils.initClient(mOkHttpClient);
    }

    /**
     * 获取课表
     */
    private void spider() {
        System.out.println("Spider");
        OkHttpUtils.get().url(MAIN_URL)
                .addHeader("Host", HOST)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept-Encoding", ACCEPT_ENCODING)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        System.out.println("onResponse:" + response);
                        parseStringHtml(response);
                    }
                });
        System.out.println("执行完毕");
    }

    /**
     * 解析HTML
     */
    private void parseStringHtml(String html) {
        String tdStr, tdStrOfHtml;

        Document document = Jsoup.parse(html);
        // 拿到列表
        Element elementTable1 = document.getElementById("recipe_all_list clearfix mgt20");
        Elements divs = elementTable1.select("div");

        for (int start = 0; start < divs.size(); ++start) {
            System.out.println(divs.html());
//            int nbsp = 0;
//            Element tr = divs.get(start);
//            Elements tds = tr.select("td[align]");
//            for (int j = 0; j < tds.size(); j++) {
//
//                Element td = tds.get(j);
//                tdStr = td.text();
//
//                // &nbsp占一个英文字符，别的课不止一个字符
//                nbsp++;
//                if (tdStr.length() != 1) {
//                    if (nbsp == 4) {
//                        nbsp = 4;
//                    }
//                    int lessonCount = 0;
//                    //检测同一个时间点存在多少节课
//                    tdStrOfHtml = td.html();
//                    String findSameTimeClass = "<\\w+><\\w+>";
//                    Pattern checkLesson = Pattern.compile(findSameTimeClass);
//                    Matcher matcherCheckLesson = checkLesson.matcher(tdStrOfHtml);
//                    while (matcherCheckLesson.find()) {
//                        lessonCount++;
//                    }
//                    // 一个位置有多堂课的情况
//                    if (lessonCount != 0) {
//                        String[] tdStrs = tdStrOfHtml.split("<br><br>");
//                        for (int n = 0; n <= lessonCount; n++) {
//                            String tdStr1 = tdStrs[n];
//                            //替换<br>为" ";
//                            String replaceBr = "<br>";
//                            Pattern replaseBr = Pattern.compile(replaceBr);
//                            Matcher matcherReplaseBr = replaseBr.matcher(tdStr1);
//                            tdStr1 = matcherReplaseBr.replaceAll(" ");
//
//                        }
//                    } else {
//                    }
//                }
//            }
        }
    }

    /**
     * 找出单双周的字符串
     *
     * @param string 周四第3,4节{第2-14周|双周}
     * @return 单周返回“单周”，双周返回“双周”，不是就返回null
     */
    private String findOddOrEven(String string) {
        // 周四第3,4节{第2-14周|双周}
        String findOddOrEvenString = "\\|(.*?)\\}";
        Pattern patternSaveWeek = Pattern.compile(findOddOrEvenString);
        Matcher matcherSaveWeek = patternSaveWeek.matcher(string);
        String result = null;
        while (matcherSaveWeek.find()) {
            result = matcherSaveWeek.group()
                    .replace("|", "")
                    .replace("}", " ");
        }
        return result;
    }

}
