//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.manager;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioAttributes.Builder;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.Build.VERSION;
import android.os.PowerManager.WakeLock;
import io.rong.common.RLog;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayManager implements SensorEventListener {
  private static final String TAG = "AudioPlayManager";
  private MediaPlayer mMediaPlayer;
  private IAudioPlayListener _playListener;
  private Uri mUriPlaying;
  private Sensor _sensor;
  private SensorManager _sensorManager;
  private AudioManager mAudioManager;
  private PowerManager _powerManager;
  private WakeLock _wakeLock;
  private OnAudioFocusChangeListener afChangeListener;
  private Context mContext;
  private Handler handler;
  private boolean isVOIPMode;

  private AudioPlayManager() {
    this.isVOIPMode = false;
    this.handler = new Handler(Looper.getMainLooper());
  }

  public static AudioPlayManager getInstance() {
    return AudioPlayManager.SingletonHolder.sInstance;
  }

  @TargetApi(11)
  public void onSensorChanged(SensorEvent event) {
    float range = event.values[0];
    RLog.d("AudioPlayManager", "onSensorChanged. range:" + range + "; max range:" + event.sensor.getMaximumRange());
    double rangeJudgeValue = 0.0D;
    if (this._sensor != null && this.mMediaPlayer != null) {
      boolean judge = this.judgeCondition(event, range, rangeJudgeValue);
      if (this.mMediaPlayer.isPlaying()) {
        FileInputStream fis = null;
        if (judge) {
          if (this.mAudioManager.getMode() == 0) {
            return;
          }

          this.mAudioManager.setMode(0);
          this.mAudioManager.setSpeakerphoneOn(true);
          final int positions = this.mMediaPlayer.getCurrentPosition();

          try {
            this.mMediaPlayer.reset();
            if (VERSION.SDK_INT >= 21) {
              AudioAttributes attributes = (new Builder()).setUsage(1).build();
              this.mMediaPlayer.setAudioAttributes(attributes);
            } else {
              this.mMediaPlayer.setAudioStreamType(3);
            }

            this.mMediaPlayer.setVolume(1.0F, 1.0F);
            fis = new FileInputStream(this.mUriPlaying.getPath());
            this.mMediaPlayer.setDataSource(fis.getFD());
            this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
              public void onPrepared(MediaPlayer mp) {
                mp.seekTo(positions);
              }
            });
            this.mMediaPlayer.setOnSeekCompleteListener(new OnSeekCompleteListener() {
              public void onSeekComplete(MediaPlayer mp) {
                mp.start();
              }
            });
            this.mMediaPlayer.prepareAsync();
          } catch (IOException var17) {
            RLog.e("AudioPlayManager", "onSensorChanged", var17);
          } finally {
            if (fis != null) {
              try {
                fis.close();
              } catch (IOException var16) {
                RLog.e("AudioPlayManager", "startPlay", var16);
              }
            }

          }

          this.setScreenOn();
        } else {
          if (!Build.BRAND.equals("samsung") || !Build.MODEL.equals("SM-N9200")) {
            this.setScreenOff();
          }

          if (VERSION.SDK_INT >= 11) {
            if (this.mAudioManager.getMode() == 3) {
              return;
            }

            this.mAudioManager.setMode(3);
          } else {
            if (this.mAudioManager.getMode() == 2) {
              return;
            }

            this.mAudioManager.setMode(2);
          }

          this.mAudioManager.setSpeakerphoneOn(false);
          this.replay();
        }
      } else if ((double)range > 0.0D) {
        if (this.mAudioManager.getMode() == 0) {
          return;
        }

        this.mAudioManager.setMode(0);
        this.mAudioManager.setSpeakerphoneOn(true);
        this.setScreenOn();
      }

    }
  }

  private boolean judgeCondition(SensorEvent event, float range, double rangeJudgeValue) {
    boolean judge;
    if (Build.BRAND.equalsIgnoreCase("HUAWEI")) {
      judge = range >= event.sensor.getMaximumRange();
    } else {
      if (Build.BRAND.equalsIgnoreCase("ZTE")) {
        rangeJudgeValue = 1.0D;
      } else if (Build.BRAND.equalsIgnoreCase("nubia")) {
        rangeJudgeValue = 3.0D;
      }

      judge = (double)range > rangeJudgeValue;
    }

    return judge;
  }

  @TargetApi(21)
  private void setScreenOff() {
    if (this._wakeLock == null) {
      this._wakeLock = this._powerManager.newWakeLock(32, "AudioPlayManager");
    }

    if (this._wakeLock != null && !this._wakeLock.isHeld()) {
      this._wakeLock.acquire();
    }

  }

  private void setScreenOn() {
    if (this._wakeLock != null && this._wakeLock.isHeld()) {
      this._wakeLock.setReferenceCounted(false);
      this._wakeLock.release();
      this._wakeLock = null;
    }

  }

  public void onAccuracyChanged(Sensor sensor, int accuracy) {
  }

  private void replay() {
    FileInputStream fis = null;

    try {
      this.mMediaPlayer.reset();
      AudioAttributes attributes;
      if (Build.BRAND.equals("samsung") && Build.MODEL.equals("SM-N9200")) {
        if (VERSION.SDK_INT >= 21) {
          attributes = (new Builder()).setUsage(2).build();
          this.mMediaPlayer.setAudioAttributes(attributes);
        } else {
          this.mMediaPlayer.setAudioStreamType(0);
        }
      } else if (VERSION.SDK_INT >= 21) {
        attributes = (new Builder()).setUsage(1).build();
        this.mMediaPlayer.setAudioAttributes(attributes);
      } else {
        this.mMediaPlayer.setAudioStreamType(3);
      }

      this.mMediaPlayer.setVolume(1.0F, 1.0F);
      fis = new FileInputStream(this.mUriPlaying.getPath());
      this.mMediaPlayer.setDataSource(fis.getFD());
      this.mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
          try {
            Thread.sleep(1000L);
          } catch (InterruptedException var3) {
            RLog.e("AudioPlayManager", "replay", var3);
            Thread.currentThread().interrupt();
          }

          mp.start();
        }
      });
      this.mMediaPlayer.prepareAsync();
    } catch (IOException var11) {
      RLog.e("AudioPlayManager", "replay", var11);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException var10) {
          RLog.e("AudioPlayManager", "replay", var10);
        }
      }

    }

  }

  public void startPlay(final Context context, Uri audioUri, IAudioPlayListener playListener) {
    if (context != null && audioUri != null) {
      this.mContext = context;
      if (this._playListener != null && this.mUriPlaying != null) {
        this._playListener.onStop(this.mUriPlaying);
      }

      this.resetMediaPlayer();
      this.afChangeListener = new OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
          RLog.d("AudioPlayManager", "OnAudioFocusChangeListener " + focusChange);
          if (AudioPlayManager.this.mAudioManager != null && focusChange == -1) {
            AudioPlayManager.this.mAudioManager.abandonAudioFocus(AudioPlayManager.this.afChangeListener);
            AudioPlayManager.this.afChangeListener = null;
            AudioPlayManager.this.handler.post(new Runnable() {
              public void run() {
                if (AudioPlayManager.this._playListener != null) {
                  AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
                  AudioPlayManager.this._playListener = null;
                }

              }
            });
            AudioPlayManager.this.reset();
          }

        }
      };
      FileInputStream fis = null;
      ((Activity)context).getWindow().addFlags(128);

      try {
        this._powerManager = (PowerManager)context.getApplicationContext().getSystemService("power");
        this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService("audio");
        if (!this.mAudioManager.isWiredHeadsetOn()) {
          this._sensorManager = (SensorManager)context.getApplicationContext().getSystemService("sensor");
          this._sensor = this._sensorManager.getDefaultSensor(8);
          this._sensorManager.registerListener(this, this._sensor, 3);
        }

        this.muteAudioFocus(this.mAudioManager, true);
        this._playListener = playListener;
        this.mUriPlaying = audioUri;
        this.mMediaPlayer = new MediaPlayer();
        this.mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
          public void onCompletion(MediaPlayer mp) {
            if (AudioPlayManager.this._playListener != null) {
              AudioPlayManager.this._playListener.onComplete(AudioPlayManager.this.mUriPlaying);
              AudioPlayManager.this._playListener = null;
            }

            AudioPlayManager.this.reset();
            ((Activity)context).getWindow().clearFlags(128);
          }
        });
        this.mMediaPlayer.setOnErrorListener(new OnErrorListener() {
          public boolean onError(MediaPlayer mp, int what, int extra) {
            AudioPlayManager.this.reset();
            return true;
          }
        });
        fis = new FileInputStream(audioUri.getPath());
        this.mMediaPlayer.setDataSource(fis.getFD());
        if (VERSION.SDK_INT >= 21) {
          AudioAttributes attributes = (new Builder()).setUsage(1).build();
          this.mMediaPlayer.setAudioAttributes(attributes);
        } else {
          this.mMediaPlayer.setAudioStreamType(3);
        }

        this.mMediaPlayer.prepare();
        this.mMediaPlayer.start();
        if (this._playListener != null) {
          this._playListener.onStart(this.mUriPlaying);
        }
      } catch (Exception var14) {
        RLog.e("AudioPlayManager", "startPlay", var14);
        if (this._playListener != null) {
          this._playListener.onStop(audioUri);
          this._playListener = null;
        }

        this.reset();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException var13) {
            RLog.e("AudioPlayManager", "startPlay", var13);
          }
        }

      }

    } else {
      RLog.e("AudioPlayManager", "startPlay context or audioUri is null.");
    }
  }

  public void setPlayListener(IAudioPlayListener listener) {
    this._playListener = listener;
  }

  public void stopPlay() {
    if (this.mContext != null) {
      ((Activity)this.mContext).getWindow().clearFlags(128);
    }

    if (this._playListener != null && this.mUriPlaying != null) {
      this._playListener.onStop(this.mUriPlaying);
    }

    this.reset();
  }

  private void reset() {
    this.resetMediaPlayer();
    this.resetAudioPlayManager();
  }

  private void resetAudioPlayManager() {
    if (this.mAudioManager != null) {
      this.mAudioManager.setMode(0);
      this.muteAudioFocus(this.mAudioManager, false);
    }

    if (this._sensorManager != null) {
      this.setScreenOn();
      this._sensorManager.unregisterListener(this);
    }

    this._sensorManager = null;
    this._sensor = null;
    this._powerManager = null;
    this.mAudioManager = null;
    this._wakeLock = null;
    this.mUriPlaying = null;
    this._playListener = null;
  }

  private void resetMediaPlayer() {
    if (this.mMediaPlayer != null) {
      try {
        this.mMediaPlayer.stop();
        this.mMediaPlayer.reset();
        this.mMediaPlayer.release();
        this.mMediaPlayer = null;
      } catch (IllegalStateException var2) {
        RLog.e("AudioPlayManager", "resetMediaPlayer", var2);
      }
    }

  }

  public Uri getPlayingUri() {
    return this.mUriPlaying != null ? this.mUriPlaying : Uri.EMPTY;
  }

  @TargetApi(8)
  private void muteAudioFocus(AudioManager audioManager, boolean bMute) {
    if (bMute) {
      audioManager.requestAudioFocus(this.afChangeListener, 3, 2);
    } else {
      audioManager.abandonAudioFocus(this.afChangeListener);
      this.afChangeListener = null;
    }

  }

  public boolean isInNormalMode(Context context) {
    if (this.mAudioManager == null) {
      this.mAudioManager = (AudioManager)context.getApplicationContext().getSystemService("audio");
    }

    return this.mAudioManager != null && this.mAudioManager.getMode() == 0;
  }

  public boolean isInVOIPMode(Context context) {
    return this.isVOIPMode;
  }

  public void setInVoipMode(boolean isVOIPMode) {
    this.isVOIPMode = isVOIPMode;
  }

  public boolean isPlaying() {
    return this.mMediaPlayer != null && this.mMediaPlayer.isPlaying();
  }

  static class SingletonHolder {
    static AudioPlayManager sInstance = new AudioPlayManager();

    SingletonHolder() {
    }
  }
}
