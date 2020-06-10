//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor.videoslimmer.muxer;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import java.nio.ByteBuffer;

import io.rong.imkit.utilities.videocompressor.videoslimmer.render.TextureRenderer;

@RequiresApi(
        api = 17
)
public class CodecInputSurface implements OnFrameAvailableListener {
    private static final int EGL_RECORDABLE_ANDROID = 12610;
    private final Object mFrameSyncObject = new Object();
    private EGLDisplay mEGLDisplay;
    private EGLContext mEGLContext;
    private EGLSurface mEGLSurface;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private Surface mDrawSurface;
    private boolean mFrameAvailable;
    private ByteBuffer mPixelBuf;
    private TextureRenderer mTextureRender;

    public CodecInputSurface(Surface surface) {
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        if (surface == null) {
            throw new NullPointerException();
        } else {
            this.mSurface = surface;
            this.eglSetup();
        }
    }

    public void createRender() {
        this.mTextureRender = new TextureRenderer();
        this.mTextureRender.surfaceCreated();
        this.mSurfaceTexture = new SurfaceTexture(this.mTextureRender.getTextureId());
        this.mSurfaceTexture.setOnFrameAvailableListener(this);
        this.mDrawSurface = new Surface(this.mSurfaceTexture);
    }

    private void eglSetup() {
        this.mEGLDisplay = EGL14.eglGetDisplay(0);
        if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL14 display");
        } else {
            int[] version = new int[2];
            if (!EGL14.eglInitialize(this.mEGLDisplay, version, 0, version, 1)) {
                throw new RuntimeException("unable to initialize EGL14");
            } else {
                int[] attribList = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12610, 1, 12344};
                EGLConfig[] configs = new EGLConfig[1];
                int[] numConfigs = new int[1];
                EGL14.eglChooseConfig(this.mEGLDisplay, attribList, 0, configs, 0, configs.length, numConfigs, 0);
                this.checkEglError("eglCreateContext RGB888+recordable ES2");
                int[] attrib_list = new int[]{12440, 2, 12344};
                this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, configs[0], EGL14.EGL_NO_CONTEXT, attrib_list, 0);
                this.checkEglError("eglCreateContext");
                int[] surfaceAttribs = new int[]{12344};
                this.mEGLSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, configs[0], this.mSurface, surfaceAttribs, 0);
                this.checkEglError("eglCreateWindowSurface");
            }
        }
    }

    public SurfaceTexture getSurfaceTexture() {
        return this.mSurfaceTexture;
    }

    public void changeFragmentShader(String fragmentShader) {
        this.mTextureRender.changeFragmentShader(fragmentShader);
    }

    public void awaitNewImage() {
        synchronized (this.mFrameSyncObject) {
            while (!this.mFrameAvailable) {
                try {
                    this.mFrameSyncObject.wait(5000L);
                    if (!this.mFrameAvailable) {
                        throw new RuntimeException("Surface frame wait timed out");
                    }
                } catch (InterruptedException var5) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(var5);
                }
            }

            this.mFrameAvailable = false;
        }

        this.mTextureRender.checkGlError("before updateTexImage");
        this.mSurfaceTexture.updateTexImage();
    }

    public void drawImage() {
        this.mTextureRender.drawFrame(this.mSurfaceTexture);
    }

    public void onFrameAvailable(SurfaceTexture st) {
        synchronized (this.mFrameSyncObject) {
            if (this.mFrameAvailable) {
                throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
            } else {
                this.mFrameAvailable = true;
                this.mFrameSyncObject.notifyAll();
            }
        }
    }

    public Surface getSurface() {
        return this.mDrawSurface;
    }

    public void release() {
        if (this.mEGLDisplay != EGL14.EGL_NO_DISPLAY) {
            EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
            EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
            EGL14.eglReleaseThread();
            EGL14.eglTerminate(this.mEGLDisplay);
        }

        this.mSurface.release();
        this.mEGLDisplay = EGL14.EGL_NO_DISPLAY;
        this.mEGLContext = EGL14.EGL_NO_CONTEXT;
        this.mEGLSurface = EGL14.EGL_NO_SURFACE;
        this.mSurface = null;
    }

    public void makeCurrent() {
        EGL14.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext);
        this.checkEglError("eglMakeCurrent");
    }

    public boolean swapBuffers() {
        boolean result = EGL14.eglSwapBuffers(this.mEGLDisplay, this.mEGLSurface);
        this.checkEglError("eglSwapBuffers");
        return result;
    }

    @RequiresApi(
            api = 18
    )
    public void setPresentationTime(long nsecs) {
        EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, this.mEGLSurface, nsecs);
        this.checkEglError("eglPresentationTimeANDROID");
    }

    private void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != 12288) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}