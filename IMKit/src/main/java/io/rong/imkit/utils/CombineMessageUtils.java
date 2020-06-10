//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import io.rong.common.FileUtils;
import io.rong.common.RLog;
import io.rong.imkit.RongContext;
import io.rong.imkit.R.string;
import io.rong.imkit.message.CombineMessage;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.FileMessage;
import io.rong.message.GIFMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.SightMessage;
import io.rong.message.TextMessage;
import io.rong.message.utils.BitmapUtil;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class CombineMessageUtils {
    private static final String TAG = CombineMessageUtils.class.getSimpleName();
    public static final String COMBINE_FILE_PATH = "combine";
    public static final String COMBINE_FILE_NAME = ".html";
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    private static final String JSON_FILE_NAME = "combine.json";
    private static final String BASE64_PRE = "data:image/png;base64,";
    private static final String NO_USER = "rong-none-user";
    private static final String TAG_BASE_HEAD = "baseHead";
    private static final String TAG_TIME = "time";
    private static final String TAG_TXT = "RC:TxtMsg";
    private static final String TAG_GIF = "RC:GIFMsg";
    private static final String TAG_VC = "RC:VcMsg";
    private static final String TAG_HQVC = "RC:HQVCMsg";
    private static final String TAG_CARD = "RC:CardMsg";
    private static final String TAG_STK = "RC:StkMsg";
    private static final String TAG_IMG_TEXT = "RC:ImgTextMsg";
    private static final String TAG_SIGHT = "RC:SightMsg";
    private static final String TAG_IMG = "RC:ImgMsg";
    private static final String TAG_COMBINE = "RC:CombineMsg";
    private static final String TAG_MSG_COMBINE_BODY = "CombineMsgBody";
    private static final String TAG_FILE = "RC:FileMsg";
    private static final String TAG_LBS = "RC:LBSMsg";
    private static final String TAG_VCSUMMARY = "RC:VCSummary";
    private static final String TAG_VST = "RC:VSTMsg";
    private static final String TAG_RP = "RCJrmf:RpMsg";
    private static final String TAG_BASE_BOTTOM = "baseBottom";
    private static final String MSG_BASE_HEAD_STYLE = "{%style%}";
    private static final String MSG_TIME = "{%time%}";
    private static final String MSG_SHOW_USER = "{%showUser%}";
    private static final String MSG_PORTRAIT = "{%portrait%}";
    private static final String MSG_USER_NAMEM = "{%userName%}";
    private static final String MSG_SEND_TIME = "{%sendTime%}";
    private static final String MSG_TEXT = "{%text%}";
    private static final String MSG_IMAG_URL = "{%imgUrl%}";
    private static final String MSG_FILE_NAME = "{%fileName%}";
    private static final String MSG_SIZE = "{%size%}";
    private static final String MSG_FILE_SIZE = "{%fileSize%}";
    private static final String MSG_FILE_URL = "{%fileUrl%}";
    private static final String MSG_FILE_TYPE = "{%fileType%}";
    private static final String MSG_FILE_ICON = "{%fileIcon%}";
    private static final String MSG_TITLE = "{%title%}";
    private static final String MSG_COMBINE_BODY = "{%combineBody%}";
    private static final String MSG_FOOT = "{%foot%}";
    private static final String MSG_LOCATION_NAME = "{%locationName%}";
    private static final String MSG_LATITUDE = "{%latitude%}";
    private static final String MSG_LONGITTUDE = "{%longitude%}";
    private Map<String, String> DATA;
    private Uri URI;
    private Boolean isSameDay;
    private Boolean isSameYear;
    private String style;

    private CombineMessageUtils() {
        this.DATA = new HashMap();
        this.URI = null;
        this.style = "";
    }

    public static CombineMessageUtils getInstance() {
        return CombineMessageUtils.Holder.Utils;
    }

    Uri getUrlFromMessageList(List<Message> messagesList) {
        this.style = "";
        this.URI = null;
        this.isSameDay = this.isSameYear = false;
        String filePath = FileUtils.getCachePath(RongContext.getInstance()) + File.separator + "combine" + File.separator + System.currentTimeMillis() + ".html";
        String filStr = this.getHtmlFromMessageList(messagesList);
        FileUtils.saveFile(filStr, filePath);
        return Uri.parse("file://" + filePath);
    }

    private String getHtmlFromMessageList(List<Message> messagesList) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getHtmlBaseHead());
        stringBuilder.append(this.getHtmlTime(messagesList));
        Iterator var3 = messagesList.iterator();

        while(var3.hasNext()) {
            Message msg = (Message)var3.next();
            stringBuilder.append(this.getHtmlFromMessageContent(msg, msg.getContent()));
        }

        stringBuilder.append(this.getHtmlBaseBottom());
        return stringBuilder.toString();
    }

    private String getHtmlBaseHead() {
        return this.getHtmlFromType("baseHead").replace("{%style%}", this.style);
    }

    private String getHtmlTime(List<Message> messagesList) {
        long first = ((Message)messagesList.get(0)).getSentTime();
        Calendar firstCalendar = Calendar.getInstance();
        firstCalendar.setTimeInMillis(first);
        long last = ((Message)messagesList.get(messagesList.size() - 1)).getSentTime();
        Calendar lastCalendar = Calendar.getInstance();
        lastCalendar.setTimeInMillis(last);
        this.isSameYear = firstCalendar.get(1) == lastCalendar.get(1);
        this.isSameDay = this.isSameYear && firstCalendar.get(2) == lastCalendar.get(2) && firstCalendar.get(5) == lastCalendar.get(5);
        String format = "yyyy-M-d";
        String time;
        if (this.isSameDay) {
            time = (new SimpleDateFormat(format, Locale.CANADA)).format(first);
        } else {
            time = (new SimpleDateFormat(format, Locale.CANADA)).format(first) + " - " + (new SimpleDateFormat(format, Locale.CANADA)).format(last);
        }

        return this.getHtmlFromType("time").replace("{%time%}", time);
    }

    private String getHtmlFromMessageContent(Message message, MessageContent content) {
        MessageTag tag = (MessageTag)content.getClass().getAnnotation(MessageTag.class);
        if (tag != null && tag.value().startsWith("RC:")) {
            String type = tag.value();
            String html = this.setUserInfo(this.getHtmlFromType(type), message);
            byte var7 = -1;
            switch(type.hashCode()) {
                case -2042295573:
                    if (type.equals("RC:VcMsg")) {
                        var7 = 2;
                    }
                    break;
                case -1835503925:
                    if (type.equals("RC:CombineMsg")) {
                        var7 = 14;
                    }
                    break;
                case -1160730064:
                    if (type.equals("RC:VCSummary")) {
                        var7 = 7;
                    }
                    break;
                case -961182724:
                    if (type.equals("RC:FileMsg")) {
                        var7 = 12;
                    }
                    break;
                case -926790277:
                    if (type.equals("RCJrmf:RpMsg")) {
                        var7 = 8;
                    }
                    break;
                case -911587622:
                    if (type.equals("RC:ImgTextMsg")) {
                        var7 = 1;
                    }
                    break;
                case 105394658:
                    if (type.equals("RC:HQVCMsg")) {
                        var7 = 3;
                    }
                    break;
                case 447751656:
                    if (type.equals("RC:CardMsg")) {
                        var7 = 5;
                    }
                    break;
                case 659653286:
                    if (type.equals("RC:GIFMsg")) {
                        var7 = 11;
                    }
                    break;
                case 751141447:
                    if (type.equals("RC:ImgMsg")) {
                        var7 = 10;
                    }
                    break;
                case 796721677:
                    if (type.equals("RC:LBSMsg")) {
                        var7 = 13;
                    }
                    break;
                case 1044016768:
                    if (type.equals("RC:StkMsg")) {
                        var7 = 4;
                    }
                    break;
                case 1076608122:
                    if (type.equals("RC:TxtMsg")) {
                        var7 = 0;
                    }
                    break;
                case 1098742835:
                    if (type.equals("RC:VSTMsg")) {
                        var7 = 6;
                    }
                    break;
                case 1310555117:
                    if (type.equals("RC:SightMsg")) {
                        var7 = 9;
                    }
            }

            switch(var7) {
                case 0:
                    TextMessage text = (TextMessage)content;
                    html = html.replace("{%text%}", text.getContent());
                    break;
                case 1:
                case 2:
                case 3:
                    html = html.replace("{%text%}", this.getSpannable(content));
                    break;
                case 4:
                    html = html.replace("{%text%}", RongContext.getInstance().getString(string.rc_message_content_sticker));
                    break;
                case 5:
                    html = html.replace("{%text%}", RongContext.getInstance().getString(string.rc_message_content_card));
                    break;
                case 6:
                case 7:
                    html = html.replace("{%text%}", RongContext.getInstance().getString(string.rc_message_content_vst));
                    break;
                case 8:
                    html = html.replace("{%text%}", RongContext.getInstance().getString(string.rc_message_content_rp));
                    break;
                case 9:
                    SightMessage sight = (SightMessage)content;
                    html = html.replace("{%fileName%}", sight.getName()).replace("{%size%}", FileTypeUtils.formatFileSize(sight.getSize())).replace("{%fileUrl%}", sight.getMediaUrl() == null ? "" : sight.getMediaUrl().toString());
                    break;
                case 10:
                    ImageMessage image = (ImageMessage)content;
                    String base64 = this.getBase64FromUrl(image.getThumUri());
                    html = html.replace("{%fileUrl%}", image.getMediaUrl() == null ? "" : image.getMediaUrl().toString()).replace("{%imgUrl%}", base64);
                    break;
                case 11:
                    GIFMessage gif = (GIFMessage)content;
                    String gifBase64 = this.getBase64FromUrl(gif.getRemoteUri());
                    html = html.replace("{%fileUrl%}", gif.getRemoteUri() == null ? "" : gif.getRemoteUri().toString()).replace("{%imgUrl%}", gifBase64);
                    break;
                case 12:
                    FileMessage file = (FileMessage)content;
                    html = html.replace("{%fileName%}", file.getName()).replace("{%size%}", FileTypeUtils.formatFileSize(file.getSize())).replace("{%fileSize%}", String.valueOf(file.getSize())).replace("{%fileUrl%}", file.getFileUrl() == null ? "" : file.getFileUrl().toString()).replace("{%fileType%}", file.getType()).replace("{%fileIcon%}", this.getBase64FromImageId(FileTypeUtils.fileTypeImageId(file.getName())));
                    break;
                case 13:
                    LocationMessage location = (LocationMessage)content;
                    html = html.replace("{%locationName%}", location.getPoi()).replace("{%latitude%}", String.valueOf(location.getLat())).replace("{%longitude%}", String.valueOf(location.getLng()));
                    break;
                case 14:
                    CombineMessage combine = (CombineMessage)content;
                    StringBuilder summary = new StringBuilder();
                    String combineBody = this.getHtmlFromType("CombineMsgBody");
                    List<String> summarys = combine.getSummaryList();
                    Iterator var20 = summarys.iterator();

                    while(var20.hasNext()) {
                        String sum = (String)var20.next();
                        summary.append(combineBody.replace("{%text%}", sum));
                    }

                    html = html.replace("{%fileUrl%}", combine.getMediaUrl() == null ? "" : combine.getMediaUrl().toString()).replace("{%title%}", combine.getTitle()).replace("{%combineBody%}", summary.toString()).replace("{%foot%}", RongContext.getInstance().getString(string.rc_combine_chat_history));
                    break;
                default:
                    RLog.e(TAG, "getHtmlFromMessageContent UnKnown type:" + type);
            }

            return html;
        } else {
            RLog.e(TAG, "getHtmlFromMessageContent tag is UnKnown, content:" + content);
            return "";
        }
    }

    private String getHtmlBaseBottom() {
        return this.getHtmlFromType("baseBottom");
    }

    private String getHtmlFromType(String type) {
        if (this.DATA == null || this.DATA.size() == 0) {
            this.DATA = this.getDATA();
        }

        if (this.DATA != null && this.DATA.size() != 0) {
            if ("RC:HQVCMsg".equals(type)) {
                type = "RC:VcMsg";
            }

            if ("RC:VSTMsg".equals(type)) {
                type = "RC:VCSummary";
            }

            String html = (String)this.DATA.get(type);
            if (TextUtils.isEmpty(html)) {
                RLog.e(TAG, "getHtmlFromType html is null, type:" + type);
                return "";
            } else {
                return html;
            }
        } else {
            RLog.e(TAG, "getHtmlFromType data is null");
            return "";
        }
    }

    private Map<String, String> getDATA() {
        this.DATA = this.setData(this.getJson());
        return this.DATA;
    }

    private String getJson() {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bf = null;

        try {
            bf = new BufferedReader(new InputStreamReader(RongContext.getInstance().getAssets().open("combine.json")));

            String line;
            while((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException var12) {
            RLog.e(TAG, "getJson", var12);
        } finally {
            try {
                if (bf != null) {
                    bf.close();
                }
            } catch (IOException var11) {
                RLog.e(TAG, "getJson", var11);
            }

        }

        return stringBuilder.toString();
    }

    private Map<String, String> setData(String str) {
        try {
            JSONObject jsonObj = new JSONObject(str);
            this.DATA.put("baseHead", jsonObj.optString("baseHead"));
            this.DATA.put("time", jsonObj.optString("time"));
            this.DATA.put("RC:TxtMsg", jsonObj.optString("RC:TxtMsg"));
            this.DATA.put("RC:SightMsg", jsonObj.optString("RC:SightMsg"));
            this.DATA.put("RC:ImgMsg", jsonObj.optString("RC:ImgMsg"));
            this.DATA.put("RC:GIFMsg", jsonObj.optString("RC:GIFMsg"));
            this.DATA.put("RC:CombineMsg", jsonObj.optString("RC:CombineMsg"));
            this.DATA.put("CombineMsgBody", jsonObj.optString("CombineMsgBody"));
            this.DATA.put("RC:FileMsg", jsonObj.optString("RC:FileMsg"));
            this.DATA.put("RC:VcMsg", jsonObj.optString("RC:VcMsg"));
            this.DATA.put("RC:CardMsg", jsonObj.optString("RC:CardMsg"));
            this.DATA.put("RC:StkMsg", jsonObj.optString("RC:StkMsg"));
            this.DATA.put("RC:ImgTextMsg", jsonObj.optString("RC:ImgTextMsg"));
            this.DATA.put("RC:LBSMsg", jsonObj.optString("RC:LBSMsg"));
            this.DATA.put("RC:VCSummary", jsonObj.optString("RC:VCSummary"));
            this.DATA.put("RCJrmf:RpMsg", jsonObj.optString("RCJrmf:RpMsg"));
            this.DATA.put("baseBottom", jsonObj.optString("baseBottom"));
            return this.DATA;
        } catch (JSONException var3) {
            var3.printStackTrace();
            return this.DATA;
        }
    }

    private String setUserInfo(String str, Message msg) {
        String portrait = this.getUserPortrait(msg);
        String showUser = TextUtils.isEmpty(portrait) ? "rong-none-user" : "";
        return str.replace("{%portrait%}", portrait).replace("{%showUser%}", showUser).replace("{%userName%}", this.getUserName(msg)).replace("{%sendTime%}", this.getSendTime(msg));
    }

    private String getSendTime(Message msg) {
        long dateMillis = msg.getSentTime();
        Context context = RongContext.getInstance();
        if (dateMillis <= 0L) {
            return "";
        } else {
            String hourTime;
            if (RongDateUtils.isTime24Hour(context)) {
                hourTime = (new SimpleDateFormat("H:mm", Locale.CANADA)).format(dateMillis);
            } else {
                Calendar calendarTime = Calendar.getInstance();
                calendarTime.setTimeInMillis(dateMillis);
                int hour = calendarTime.get(10);
                int minute = calendarTime.get(12);
                if (calendarTime.get(9) == 0) {
                    if (hour < 6) {
                        if (hour == 0) {
                            hour = 12;
                        }

                        hourTime = context.getResources().getString(string.rc_daybreak_format);
                    } else {
                        hourTime = context.getResources().getString(string.rc_morning_format);
                    }
                } else if (hour == 0) {
                    hour = 12;
                    hourTime = context.getResources().getString(string.rc_noon_format);
                } else if (hour <= 5) {
                    hourTime = context.getResources().getString(string.rc_afternoon_format);
                } else {
                    hourTime = context.getResources().getString(string.rc_night_format);
                }

                if (minute < 10) {
                    hourTime = hourTime + " " + hour + ":0" + minute;
                } else {
                    hourTime = hourTime + " " + hour + ":" + minute;
                }
            }

            String format;
            if (this.isSameDay) {
                format = "";
            } else if (this.isSameYear) {
                format = "M-d ";
            } else {
                format = "yyyy-M-d ";
            }

            return (new SimpleDateFormat(format, Locale.CANADA)).format(dateMillis) + hourTime;
        }
    }

    private String getUserName(Message msg) {
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(msg.getSenderUserId());
        return info == null ? "" : info.getName();
    }

    private String getUserPortrait(Message msg) {
        UserInfo info = RongUserInfoManager.getInstance().getUserInfo(msg.getSenderUserId());
        if (info == null) {
            RLog.d(TAG, "getUserPortrait userInfo is null, msg:" + msg);
            return "";
        } else {
            Uri uri = info.getPortraitUri();
            if (uri != null && !uri.equals(this.URI)) {
                this.URI = uri;
                return this.getBase64FromUrl(uri);
            } else {
                Log.d(TAG, "getUserPortrait is same uri:" + uri);
                return "";
            }
        }
    }

    private String getBase64FromUrl(Uri uri) {
        if (uri == null) {
            return "";
        } else {
            String scheme = uri.getScheme();
            if (scheme != null && scheme.equals("file")) {
                Bitmap bitmap = null;

                try {
                    bitmap = BitmapUtil.getResizedBitmap(RongContext.getInstance(), uri, 100, 100);
                } catch (IOException var6) {
                    RLog.e(TAG, "getBase64FromUrl", var6);
                }

                if (bitmap == null) {
                    RLog.e(TAG, "getBase64FromUrl bitmap is null, uri:" + uri.toString());
                    return "";
                } else {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();
                    return "data:image/png;base64," + Base64.encodeToString(data, 2);
                }
            } else {
                RLog.d(TAG, "getBase64FromUrl uri is not file, uri:" + uri.toString());
                return uri.toString();
            }
        }
    }

    private String getBase64FromImageId(int id) {
        Bitmap bitmap = BitmapFactory.decodeResource(RongContext.getInstance().getResources(), id);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(data, 2);
    }

    private String getSpannable(MessageContent content) {
        Spannable spannable = RongContext.getInstance().getMessageTemplate(content.getClass()).getContentSummary(RongContext.getInstance(), content);
        return spannable == null ? "" : spannable.toString();
    }

    public void setStyle(String STYLE) {
        this.style = STYLE;
    }

    private static class Holder {
        private static volatile CombineMessageUtils Utils = new CombineMessageUtils();

        private Holder() {
        }
    }
}