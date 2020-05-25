//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.location;

import android.text.TextUtils;
import org.json.JSONObject;

public class AMapLocationInfo {
  private double lon = 0.0D;
  private double lat = 0.0D;
  private float accuracy = 0.0F;
  private long time = 0L;
  private String retype = "";
  private String rdesc = "";
  private String citycode = "";
  private String desc = "";
  private String adcode = "";
  private String country = "";
  private String province = "";
  private String city = "";
  private String district = "";
  private String road = "";
  private String street = "";
  private String poiname = "";
  private String cens = null;
  private String poiid = "";
  private String floor = "";
  private int errorCode = 0;
  private int coord = -1;
  private String mcell = "";
  private String number = "";
  private String aoiname = "";
  private boolean isOffset = true;
  private boolean isReversegeo = true;
  private String errorInfo;
  private JSONObject extra = null;

  public AMapLocationInfo() {
  }

  public String getErrorInfo() {
    return this.errorInfo;
  }

  public void setErrorInfo(String errorInfo) {
    this.errorInfo = errorInfo;
  }

  public int getErrorCode() {
    return this.errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public boolean isOffset() {
    return this.isOffset;
  }

  public void setOffset(boolean isOffset) {
    this.isOffset = isOffset;
  }

  public boolean isReversegeo() {
    return this.isReversegeo;
  }

  public void setReversegeo(boolean isReversegeo) {
    this.isReversegeo = isReversegeo;
  }

  public double getLng() {
    return this.lon;
  }

  public void setLon(double lon) {
    this.lon = lon;
  }

  public void setLon(String strLon) {
    this.lon = Double.parseDouble(strLon);
  }

  public double getLat() {
    return this.lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public void setLat(String strLat) {
    this.lat = Double.parseDouble(strLat);
  }

  public float getAccuracy() {
    return this.accuracy;
  }

  public void setAccuracy(float accuracy) {
    this.setAccuracy(String.valueOf(Math.round(accuracy)));
  }

  private void setAccuracy(String strAccu) {
    this.accuracy = Float.parseFloat(strAccu);
  }

  public long getTime() {
    return this.time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public String getRetype() {
    return this.retype;
  }

  public void setRetype(String retype) {
    this.retype = retype;
  }

  public String getRdesc() {
    return this.rdesc;
  }

  public void setRdesc(String rdesc) {
    this.rdesc = rdesc;
  }

  public String getCitycode() {
    return this.citycode;
  }

  public void setCitycode(String citycode) {
    this.citycode = citycode;
  }

  public String getDesc() {
    return this.desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public String getAdcode() {
    return this.adcode;
  }

  public void setAdcode(String adcode) {
    this.adcode = adcode;
  }

  public String getCountry() {
    return this.country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getProvince() {
    return this.province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getDistrict() {
    return this.district;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public String getRoad() {
    return this.road;
  }

  public void setRoad(String road) {
    this.road = road;
  }

  public String getStreet() {
    return this.street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getNumber() {
    return this.number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getPoiname() {
    return this.poiname;
  }

  public void setPoiname(String poiname) {
    this.poiname = poiname;
  }

  public String getAoiname() {
    return this.aoiname;
  }

  public void setAoiname(String aoiname) {
    this.aoiname = aoiname;
  }

  public String getCens() {
    return this.cens;
  }

  public void setCens(String cens) {
    if (!TextUtils.isEmpty(cens)) {
      String[] saCens = cens.split("\\*");
      String[] saCen = null;
      String[] var4 = saCens;
      int var5 = saCens.length;

      for(int var6 = 0; var6 < var5; ++var6) {
        String str = var4[var6];
        if (!TextUtils.isEmpty(str)) {
          saCen = str.split(",");
          this.setLon(Double.parseDouble(saCen[0]));
          this.setLat(Double.parseDouble(saCen[1]));
          int iAccu = Integer.parseInt(saCen[2]);
          this.setAccuracy((float)iAccu);
          break;
        }
      }

      saCens = null;
      saCen = null;
      this.cens = cens;
    }
  }

  public String getPoiid() {
    return this.poiid;
  }

  public void setPoiid(String poiid) {
    this.poiid = poiid;
  }

  public String getFloor() {
    return this.floor;
  }

  public void setFloor(String floor) {
    if (!TextUtils.isEmpty(floor)) {
      floor = floor.replace("F", "");

      try {
        Integer.parseInt(floor);
      } catch (Throwable var3) {
        floor = null;
      }
    }

    this.floor = floor;
  }

  public String getMcell() {
    return this.mcell;
  }

  public void setMcell(String mcell) {
    this.mcell = mcell;
  }

  public JSONObject getExtra() {
    return this.extra;
  }

  public void setExtra(JSONObject extra) {
    this.extra = extra;
  }

  public boolean hasAccuracy() {
    return this.accuracy > 0.0F;
  }

  public int getCoord() {
    return this.coord;
  }

  public void setCoord(String strCoord) {
    if (TextUtils.isEmpty(strCoord)) {
      this.coord = -1;
    } else if (strCoord.equals("0")) {
      this.coord = 0;
    } else if (strCoord.equals("1")) {
      this.coord = 1;
    } else {
      this.coord = -1;
    }

  }

  public void setCoord(int iCoord) {
    this.setCoord(String.valueOf(iCoord));
  }
}
