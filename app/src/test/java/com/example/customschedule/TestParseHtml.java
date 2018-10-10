package com.example.customschedule;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyu
 * @date 18-3-26
 * @describe TODO
 */

public class TestParseHtml {

    private Map<Integer, String> stringMap = new TreeMap<>();
    private int iID;
    /**
     * 地点
     */
    private String txtClsSite;

    /**
     * 读取文件的内容
     */
    private String getString(String fileName) {
        File file = new File("./app/src/test/java/com/example/customschedule/" + fileName);
        BufferedReader bufread = null;
        String read;
        StringBuilder sb = new StringBuilder();
        try {
            bufread = new BufferedReader(new FileReader(file));
            while ((read = bufread.readLine()) != null) {
                sb.append(read);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufread != null) {
                try {
                    bufread.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }


    @Test
    public void testParse() {
        parseStringHtml(getString("hello.txt"));
        for (int key : stringMap.keySet()) {
            System.out.println(key + ", " + stringMap.get(key));
        }
    }

    private void parseStringHtml(String html) {
        String tdStr, tdStrOfHtml;

        Document document = Jsoup.parse(html);
        Element elementTable1 = document.getElementById("Table1");
        Elements trs = elementTable1.select("tr");
        //去除前两组tr，前两组tr是星期几和早晨行
        trs.remove(0);
        trs.remove(0);
        // 遍历tr
        for (int i = 0; i < trs.size(); i++) {
            // nbsp用于计算当前的td是星期几,根据正方系统html表格的留空来计算
            int nbsp = 0;
            Element tr = trs.get(i);
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
                    System.out.println("########### " + tdStrOfHtml);

                    String findSameTimeClass = "<\\w+><\\w+>";
                    Pattern checkLesson = Pattern.compile(findSameTimeClass);
                    Matcher matcherCheckLesson = checkLesson.matcher(tdStrOfHtml);
                    while (matcherCheckLesson.find()) {
                        lessonCount++;
                    }
                    // 同一个时间段多堂课的情况
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
                            regularSchedule(tdStr1, nbsp, td, i);
                        }
                    } else {
                        //名称地点解析
                        regularSchedule(tdStr, nbsp, td, i);
                    }
                }
            }
        }
    }

    /**
     * 正则表达式
     */
    private void regularSchedule(String str, int nbsp, Element td, int index) {
        System.out.println(str);
        String[] strings = str.split(" ");
        System.out.println("当前的长度 -> " + strings.length);
        if (strings.length <= 1) {
            return;
        }
        // 名称
        String txtClsName = strings[0];
        String regularWeek = strings[1];
        regularAndSaveWeek(regularWeek);
        if (strings.length == 4) {
            txtClsSite = strings[3];
        }
        System.out.println("name -> " + txtClsName + ", regularWeek -> " + regularWeek + ", room -> " + txtClsSite);
        // 总共几节
        System.out.println(td.toString());
        int txtCountNumber = Integer.parseInt(td.attr("rowspan"));
        System.out.println("当前课程数目 -> " + txtCountNumber);

        HashMap<String, String> values = new HashMap<>();
        values.put("name", txtClsName);
        values.put("room", txtClsSite);
        values.put("day", String.valueOf(nbsp));
        values.put("start", String.valueOf(index));
        values.put("step", String.valueOf(txtCountNumber));
        values.put("iId", String.valueOf(iID));

        stringMap.put(iID, txtClsName);
        values.clear();
        iID++;

        System.out.println("\n");
    }

    private void regularAndSaveWeek(String str) {
        TreeMap<String, Integer> values = new TreeMap<>();

        // 格式为“周一第1,2节{第1-6周}”
        // 将数字提取出来，1,2,1,6
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(str);
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            numbers.add(Integer.valueOf(matcher.group().trim()));
        }
        System.out.println(Arrays.toString(numbers.toArray()));

        int weekStart = numbers.get(2);
        int weekEnd = numbers.get(3);
        // 周四第3,4节{第2-14周|双周}
        // 判断是否是单双周
        String oddOrEvenOfEven = findOddOrEven(str);
        String tempWeek;

        // 循环1-25，没有课的周数插入0
        for (int i = 1; i < 26; i++) {
            if (i > weekEnd || i < weekStart) {
                tempWeek = "week" + String.valueOf(i);
                values.put(tempWeek, 0);
            } else {
                if (oddOrEvenOfEven == null) {
                    values.put("week" + String.valueOf(i), i);
                    continue;
                }
                switch (oddOrEvenOfEven.trim()) {
                    case "单周":
                        values.put("week" + String.valueOf(i), i % 2 == 1 ? i : 0);
                        break;
                    case "双周":
                        values.put("week" + String.valueOf(i), i % 2 == 0 ? i : 0);
                        break;
                    default:
                        break;
                }
            }
        }
        values.clear();

//        for (String key : values.keySet()) {
//            System.out.println(key + "," + values.get(key));
//        }
    }

    private String findOddOrEven(String string) {
        // 周四第3,4节{第2-14周|双周}
        Pattern patternSaveWeek = Pattern.compile("\\|(.*?)}");
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
