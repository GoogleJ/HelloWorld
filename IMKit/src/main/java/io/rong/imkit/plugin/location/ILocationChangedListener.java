//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.location;

import io.rong.imlib.location.RealTimeLocationConstant.RealTimeLocationErrorCode;

public interface ILocationChangedListener {
  void onLocationChanged(double var1, double var3, String var5);

  void onParticipantJoinSharing(String var1);

  void onParticipantQuitSharing(String var1);

  void onSharingTerminated();

  void onError(RealTimeLocationErrorCode var1);
}
