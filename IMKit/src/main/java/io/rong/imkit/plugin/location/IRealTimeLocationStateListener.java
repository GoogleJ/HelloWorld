//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.plugin.location;

import java.util.List;

public interface IRealTimeLocationStateListener {
  void onParticipantChanged(List<String> var1);

  void onErrorException();
}
