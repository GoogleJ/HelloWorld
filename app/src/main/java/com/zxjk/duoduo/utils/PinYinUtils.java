package com.zxjk.duoduo.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtils {

    public static String converterToFirstSpell(String chines) {
        StringBuilder pinyinName = new StringBuilder();
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if ((nameChar[0] >= 65 && nameChar[0] <= 90) || (nameChar[0] >= 97 && nameChar[0] <= 122)) {
            pinyinName.append(nameChar[0]);
        } else {
            try {
                String[] strings = PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat);
                if (strings == null) return "#";
                pinyinName.append(strings[0].charAt(0));
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }

        return pinyinName.toString().toUpperCase();
    }
}
