package com.example.customschedule.http.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.example.customschedule.http.bean.DIYCourses;
import com.example.customschedule.util.Constants;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.MemoryCookieStore;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Setter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author wangyu
 * @date 10/9/18
 * @describe 导入课程表
 */

public class ImportSchedule {

    public static final String ERROR = "账号或密码或者验证码错误";
    public static final String TITLE = "欢迎使用正方教务管理系统！请登录";

    private static final String HOST = "210.38.137.126:8016";
    private static final String ACCEPT_ENCODING = "gzip, deflate";
    private static final String MAIN_URL = "http://210.38.137.126:8016/default2.aspx";
    private static final String CHECK_CODE = "http://210.38.137.126:8016/CheckCode.aspx";
    private static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:59.0) Gecko/20100101 Firefox/59.0";

    /**
     * 地点
     */
    private String txtClsSite;
    private String stuName = "";
    private String cookie = null;
    /**
     * 一个版本位，在一个标签里面
     */
    private String viewState;
    @Setter
    private String strId = "";
    @Setter
    private String strPassword = "";
    /**
     * 验证码
     */
    @Setter
    private String txtSecretCode = "";

    private ImportInterface importInterface;
    private OkHttpClient mOkHttpClient = null;
    private MemoryCookieStore memoryCookieStore = new MemoryCookieStore();

    public ImportSchedule(Context context, ImportInterface importInterface) {
        this.importInterface = importInterface;
        this.searchID();
        // 初始化验证码
        this.initImage();
    }

    /**
     * 初始化验证码并显示出来
     */
    public void initImage() {
        if (mOkHttpClient == null) {
            // 设置日志拦截器
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    //设定日志级别
                    .addInterceptor(logInterceptor)
                    .cookieJar(new CookieJarImpl(memoryCookieStore))
                    .build();
        }
        OkHttpUtils.initClient(mOkHttpClient);
        // 获取ViewState
        OkHttpUtils.get().url(MAIN_URL)
                .addHeader("Host", HOST)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept-Encoding", ACCEPT_ENCODING)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 获取ViewState
                        String viewStatePattern = "name=\"__VIEWSTATE\" value=\".*?\"";
                        Pattern pattern = Pattern.compile(viewStatePattern);
                        Matcher matcher = pattern.matcher(response);
                        while (matcher.find()) {
                            viewState = matcher.group()
                                    .replace("name=\"__VIEWSTATE\" value=\"", "")
                                    .replace("\"", "");
                        }
                    }
                });
        OkHttpUtils.get().url(CHECK_CODE)
                .addHeader("Host", HOST)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept-Encoding", ACCEPT_ENCODING)
                .build()
                .execute(new BitmapCallback() {
                    @Override
                    public void cookie(String cookie) {
                        ImportSchedule.this.cookie = cookie;
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        importInterface.loadVerificationCodeFailure(e);
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        importInterface.loadVerificationCodeSuccess(response);
                    }
                });
    }

    /**
     * 从HTML中获取课表
     */
    public void getScheduleFromWeb() {
        // 利用OkHttp封装获取课表
        OkHttpUtils.post().url(MAIN_URL)
                // 这个参数是固定的
                .addParams("__VIEWSTATE", viewState)
                .addParams("Button1", "")
                .addParams("hidPdrs", "")
                .addParams("hidsc", "")
                .addParams("lbLanguage", "")
                // 身份
                .addParams("RadioButtonList1", "学生")
                // 学号
                .addParams("TextBox1", strId)
                // 密码
                .addParams("TextBox2", strPassword)
                // 验证码
                .addParams("txtSecretCode", txtSecretCode)
                // 用户名
                .addParams("txtUserName", strId)
                .addHeader("Host", HOST)
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept-Encoding", ACCEPT_ENCODING)
                // 添加重定向
                .addHeader("Referer", MAIN_URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        boolean loadState = getLoadState(response);
                        if (loadState) {
                            spiderSchedule();
                        } else {
                            importInterface.loadScheduleFailure();
                        }
                    }
                });
    }

    /**
     * 获取课表
     */
    private void spiderSchedule() {
        String urlStuName = getUrlByGb2312(stuName);
        String url = "http://210.38.137.126:8016/xskbcx.aspx?xh=" + strId + "&xm=" + urlStuName + "&gnmkdm=N121601";
        String referer = "http://210.38.137.126:8016/xs_main.aspx?xh=" + strId;
        OkHttpUtils.get().url(url)
                .addHeader("Referer", referer)
                .addHeader("Host", HOST)
                .addHeader("User-Agent", USER_AGENT)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        importInterface.analyzeScheduleFailure();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        parseStringHtml(response);
                        importInterface.analyzeScheduleSuccess();
                    }
                });
    }

    /**
     * 判断是否成功登入
     *
     * @param html 通过解析页面，分析是否登录了
     */
    private boolean getLoadState(String html) {
        Document document = Jsoup.parse(html);

        Element elementName = document.getElementById("xhxm");
        Elements elementTitle = document.getElementsByTag("title");
        Element title = elementTitle.first();
        String strTitle = title.html();
        if (TITLE.equals(strTitle)) {
            return false;
        }
        stuName = elementName.html();

        return true;
    }

    /**
     * url中文编码
     *
     * @param str 待转换的编码
     * @return 返回转码后的字符串
     */
    private String getUrlByGb2312(String str) {
        String encodeStr = "";
        try {
            encodeStr = URLEncoder.encode(str, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    /**
     * 解析总的课表
     */
    private void parseStringHtml(String html) {
        String tdStr, tdStrOfHtml;

        Document document = Jsoup.parse(html);
        Element elementTable1 = document.getElementById("Table1");
        Elements trs = elementTable1.select("tr");
        //去除前两组tr，前两组tr是星期几和早晨行
        trs.remove(0);
        trs.remove(0);
        // 遍历tr，tr是节数的位置，从0开始，0表示第一大节（1，2小节）
        for (int start = 0; start < trs.size(); ++start) {
            // nbsp用于计算当前的td是星期几,根据正方系统html表格的留空来计算
            int nbsp = 0;
            Element tr = trs.get(start);
            Elements tds = tr.select("td[align]");
            for (int j = 0; j < tds.size(); j++) {

                Element td = tds.get(j);
                tdStr = td.text();

                // &nbsp占一个英文字符，别的课不止一个字符
                nbsp++;
                if (tdStr.length() != 1) {
                    if (nbsp == 4) {
                        nbsp = 4;
                    }
                    int lessonCount = 0;
                    //检测同一个时间点存在多少节课
                    tdStrOfHtml = td.html();
                    String findSameTimeClass = "<\\w+><\\w+>";
                    Pattern checkLesson = Pattern.compile(findSameTimeClass);
                    Matcher matcherCheckLesson = checkLesson.matcher(tdStrOfHtml);
                    while (matcherCheckLesson.find()) {
                        lessonCount++;
                    }
                    // 一个位置有多堂课的情况
                    if (lessonCount != 0) {
                        String[] tdStrs = tdStrOfHtml.split("<br><br>");
                        for (int n = 0; n <= lessonCount; n++) {
                            String tdStr1 = tdStrs[n];
                            //替换<br>为" ";
                            String replaceBr = "<br>";
                            Pattern replaseBr = Pattern.compile(replaceBr);
                            Matcher matcherReplaseBr = replaseBr.matcher(tdStr1);
                            tdStr1 = matcherReplaseBr.replaceAll(" ");

                            //名称地点解析
                            regularSchedule(tdStr1, nbsp, td, start);
                        }
                    } else {
                        //名称地点解析
                        regularSchedule(tdStr, nbsp, td, start);
                    }
                }
            }
        }
    }

    /**
     * 正则表达式解析单个的列表项
     *
     * @param str   待解析的列表项，可能有多个
     * @param day   周几
     * @param td    当前行的HTML源码
     * @param start 第几节
     */
    private void regularSchedule(String str, int day, Element td, int start) {
        String[] strings = str.split(" ");
        if (str.length() < 3) {
            return;
        }
        // 名称
        String txtClsName = strings[0];
        String regularWeek = strings[1];
        regularAndSaveWeek(regularWeek);
        if (strings.length == 4) {
            txtClsSite = strings[3];
        }

        // 几小节
        int step = Integer.parseInt(td.attr("rowspan"));

        // 格式为“周一第1,2节{第1-6周}”，将数字提取出来，1,2,1,6
        String findAllNumber = "\\d+(\\.\\d+)?";
        Pattern pattern = Pattern.compile(findAllNumber);
        Matcher matcher = pattern.matcher(regularWeek);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group().trim()));
        }
        // 开始周
        int weekStart = numbers.get(2);
        // 结束周
        int weekEnd = numbers.get(3);
        // 周四第3,4节{第2-14周|双周}，判断是否是单双周
        String oddOrEvenOfEven = findOddOrEven(str);
        int isOdd = 0;
        if (oddOrEvenOfEven != null) {
            switch (oddOrEvenOfEven.trim()) {
                case "单周":
                    isOdd = 1;
                    break;
                case "双周":
                    isOdd = 2;
                    break;
                default:
                    break;
            }
        }

        importInterface.save2DB(start, step, day, txtClsName, txtClsSite);
    }

    /**
     * 周数解析
     *
     * @param str 格式为“周一第1,2节{第1-6周}”
     */
    private void regularAndSaveWeek(String str) {
        if (str == null) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Import2Database.IID, importInterface.getIID());

        // 格式为“周一第1,2节{第1-6周}”，将数字提取出来，1,2,1,6
        String findAllNumber = "\\d+(\\.\\d+)?";
        Pattern pattern = Pattern.compile(findAllNumber);
        Matcher matcher = pattern.matcher(str);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group().trim()));
        }
        // 开始周
        int weekStart = numbers.get(2);
        // 结束周
        int weekEnd = numbers.get(3);
        // 周四第3,4节{第2-14周|双周}，判断是否是单双周
        String oddOrEvenOfEven = findOddOrEven(str);

        // 循环1-25，没有课的周数插入0
        for (int i = 1; i < 26; i++) {
            if (i > weekEnd || i < weekStart) {
                values.put(Import2Database.WEEK + String.valueOf(i), 0);
            } else {
                if (oddOrEvenOfEven == null) {
                    values.put(Import2Database.WEEK + String.valueOf(i), i);
                    continue;
                }
                switch (oddOrEvenOfEven.trim()) {
                    case "单周":
                        values.put(Import2Database.WEEK + String.valueOf(i), i % 2 == 1 ? i : 0);
                        break;
                    case "双周":
                        values.put(Import2Database.WEEK + String.valueOf(i), i % 2 == 0 ? i : 0);
                        break;
                    default:
                        break;
                }
            }

        }
        importInterface.insertData(Import2Database.DIY_WEEK, values);
        values.clear();
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

    /**
     * 查询iID的值
     */
    private void searchID() {
        //查询表中最后一条数据的iID
        DIYCourses forID = DataSupport.findLast(DIYCourses.class);
        importInterface.setIID(forID == null ? 1 : forID.getIId() + 1);
    }

    /**
     * 判断EditText是否为空
     *
     * @param textView 待判断的View
     * @return 为空返回true，不为空返回false
     */
    public boolean isNull(TextView textView) {
        return "".equals(textView.getText().toString());
    }
}
