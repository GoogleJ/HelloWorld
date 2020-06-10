//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.location;

import io.rong.common.RLog;
import org.json.JSONObject;

public class AMapLocationParser {
  private static final String TAG = "AMapLocationParser";

  public AMapLocationParser() {
  }

  public AMapLocationInfo parserApsJsonResp(String str) {
    if (str == null) {
      return null;
    } else {
      AMapLocationInfo amapLoc = new AMapLocationInfo();

      try {
        JSONObject respOBJ = new JSONObject(str);
        String status = respOBJ.optString("status");
        String retype;
        String rdesc;
        if ("0".equals(status)) {
          retype = respOBJ.optString("info");
          amapLoc.setErrorInfo(retype);
          rdesc = respOBJ.optString("infocode");
          int errorCode = Integer.parseInt(rdesc);
          amapLoc.setErrorCode(errorCode);
          return amapLoc;
        }

        amapLoc.setErrorCode(0);
        amapLoc.setErrorInfo("success");
        retype = respOBJ.optString("retype");
        amapLoc.setRetype(retype);
        rdesc = respOBJ.optString("rdesc");
        amapLoc.setRdesc(rdesc);
        String adcode = respOBJ.optString("adcode");
        amapLoc.setAdcode(adcode);
        String citycode = respOBJ.optString("citycode");
        amapLoc.setCitycode(citycode);
        String coord = respOBJ.optString("coord");
        amapLoc.setCoord(coord);
        String desc = respOBJ.optString("desc");
        amapLoc.setDesc(desc);
        long apiTime = respOBJ.optLong("apiTime");
        amapLoc.setTime(apiTime);
        JSONObject locationOBJ = respOBJ.optJSONObject("location");
        if (locationOBJ != null) {
          float radius = (float)locationOBJ.optDouble("radius");
          amapLoc.setAccuracy(radius);
          double lon = locationOBJ.optDouble("cenx");
          amapLoc.setLon(lon);
          double lat = locationOBJ.optDouble("ceny");
          amapLoc.setLat(lat);
        }

        JSONObject revergeoOBJ = respOBJ.optJSONObject("revergeo");
        String pid;
        String flr;
        if (revergeoOBJ != null) {
          String country = revergeoOBJ.optString("country");
          amapLoc.setCountry(country);
          pid = revergeoOBJ.optString("province");
          amapLoc.setProvince(pid);
          flr = revergeoOBJ.optString("city");
          amapLoc.setCity(flr);
          String district = revergeoOBJ.optString("district");
          amapLoc.setDistrict(district);
          String road = revergeoOBJ.optString("road");
          amapLoc.setRoad(road);
          String street = revergeoOBJ.optString("street");
          amapLoc.setStreet(street);
          String number = revergeoOBJ.optString("number");
          amapLoc.setNumber(number);
          String poiname = revergeoOBJ.optString("poiname");
          amapLoc.setPoiname(poiname);
          String aoiname = revergeoOBJ.optString("aoiname");
          amapLoc.setAoiname(aoiname);
        }

        JSONObject indoorOBJ = respOBJ.optJSONObject("indoor");
        if (indoorOBJ != null) {
          pid = indoorOBJ.optString("pid");
          amapLoc.setPoiid(pid);
          flr = indoorOBJ.optString("flr");
          amapLoc.setFloor(flr);
        }
      } catch (Exception var24) {
        RLog.e("AMapLocationParser", "parserApsJsonResp", var24);
      }

      return amapLoc;
    }
  }
}
