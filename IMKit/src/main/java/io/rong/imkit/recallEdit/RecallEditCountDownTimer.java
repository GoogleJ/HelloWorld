//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.recallEdit;

import io.rong.common.CountDownTimer;

public class RecallEditCountDownTimer {
  private static final int COUNTDOWN_INTERVAL = 1000;
  private CountDownTimer mCountDownTimer;
  private String mMessageId;
  private RecallEditCountDownTimerListener mListener;

  public RecallEditCountDownTimer(String messageId, RecallEditCountDownTimerListener listener, long millisInFuture) {
    this.mMessageId = messageId;
    this.mListener = listener;
    this.mCountDownTimer = new CountDownTimer(millisInFuture, 1000L) {
      public void onTick(long millisUntilFinished) {
        if (RecallEditCountDownTimer.this.mListener != null) {
          RecallEditCountDownTimer.this.mListener.onTick((long)Math.round((float)millisUntilFinished / 1000.0F), RecallEditCountDownTimer.this.mMessageId);
        }

      }

      public void onFinish() {
        if (RecallEditCountDownTimer.this.mListener != null) {
          RecallEditCountDownTimer.this.mListener.onFinish(RecallEditCountDownTimer.this.mMessageId);
        }

      }
    };
  }

  public void start() {
    if (this.mCountDownTimer != null && !this.mCountDownTimer.isStart()) {
      this.mCountDownTimer.start();
    }

  }

  public void cancel() {
    if (this.mCountDownTimer != null) {
      this.mCountDownTimer.cancel();
    }

  }

  public void setListener(RecallEditCountDownTimerListener listener) {
    this.mListener = listener;
  }
}
