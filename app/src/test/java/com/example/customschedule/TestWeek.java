package com.example.customschedule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TestWeek {
    @Test
    public void addition_isCorrect() throws Exception {
        regularAndSaveWeek("周四第3,4节{第2-14周|双周}");
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
        System.out.println(oddOrEvenOfEven);
        String tempWeek;

        // 循环1-25，没有课的周数插入0
        for (int i = 1; i < 26; i++) {
            if (i > weekEnd || i < weekStart) {
                tempWeek = "week" + String.valueOf(i);
                values.put(tempWeek, 0);
            } else {
                if (oddOrEvenOfEven == null) {
                    tempWeek = "week" + String.valueOf(i);
                    values.put(tempWeek, i);
                    continue;
                }
                switch (oddOrEvenOfEven.trim()) {
                    case "单周":
                        if (i % 2 == 1) {
                            tempWeek = "week" + String.valueOf(i);
                            values.put(tempWeek, i);
                        } else {
                            tempWeek = "week" + String.valueOf(i);
                            values.put(tempWeek, 0);
                        }
                        break;
                    case "双周":
                        if (i % 2 == 0) {
                            tempWeek = "week" + String.valueOf(i);
                            values.put(tempWeek, i);
                        } else {
                            tempWeek = "week" + String.valueOf(i);
                            values.put(tempWeek, 0);
                        }
                        break;
                    default:
                        break;
                }
            }

        }
        // values.clear();

        for (String key : values.keySet()) {
            System.out.println(key + "," + values.get(key));
        }
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