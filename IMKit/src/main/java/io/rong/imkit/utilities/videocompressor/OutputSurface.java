//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.utilities.videocompressor;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES20;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

@TargetApi(16)
public class OutputSurface implements OnFrameAvailableListener {
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private EGL10 mEGL;
    private EGLDisplay mEGLDisplay = null;
    private EGLContext mEGLContext = null;
    private EGLSurface mEGLSurface = null;
    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private final Object mFrameSyncObject = new Object();
    private boolean mFrameAvailable;
    private TextureRenderer mTextureRender;
    private int mWidth;
    private int mHeight;
    private int rotateRender = 0;
    private ByteBuffer mPixelBuf;

    public OutputSurface(int width, int height, int rotate) {
        if (width > 0 && height > 0) {
            this.mWidth = width;
            this.mHeight = height;
            this.rotateRender = rotate;
            this.mPixelBuf = ByteBuffer.allocateDirect(this.mWidth * this.mHeight * 4);
            this.mPixelBuf.order(ByteOrder.LITTLE_ENDIAN);
            this.eglSetup(width, height);
            this.makeCurrent();
            this.setup();
        } else {
            throw new IllegalArgumentException();
        }
    }

    public OutputSurface() {
        this.setup();
    }

    private void setup() {
        this.mTextureRender = new TextureRenderer(this.rotateRender);
        this.mTextureRender.surfaceCreated();
        this.mSurfaceTexture = new SurfaceTexture(this.mTextureRender.getTextureId());
        this.mSurfaceTexture.setOnFrameAvailableListener(this);
        this.mSurface = new Surface(this.mSurfaceTexture);
    }

    private void eglSetup(int width, int height) {
        this.mEGL = (EGL10) EGLContext.getEGL();
        this.mEGLDisplay = this.mEGL.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        if (this.mEGLDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("unable to get EGL10 display");
        } else if (!this.mEGL.eglInitialize(this.mEGLDisplay, (int[]) null)) {
            this.mEGLDisplay = null;
            throw new RuntimeException("unable to initialize EGL10");
        } else {
            int[] attribList = new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12339, 1, 12352, 4, 12344};
            EGLConfig[] configs = new EGLConfig[1];
            int[] numConfigs = new int[1];
            if (!this.mEGL.eglChooseConfig(this.mEGLDisplay, attribList, configs, configs.length, numConfigs)) {
                throw new RuntimeException("unable to find RGB888+pbuffer EGL config");
            } else {
                int[] attrib_list = new int[]{12440, 2, 12344};
                this.mEGLContext = this.mEGL.eglCreateContext(this.mEGLDisplay, configs[0], EGL10.EGL_NO_CONTEXT, attrib_list);
                this.checkEglError("eglCreateContext");
                if (this.mEGLContext == null) {
                    throw new RuntimeException("null context");
                } else {
                    int[] surfaceAttribs = new int[]{12375, width, 12374, height, 12344};
                    this.mEGLSurface = this.mEGL.eglCreatePbufferSurface(this.mEGLDisplay, configs[0], surfaceAttribs);
                    this.checkEglError("eglCreatePbufferSurface");
                    if (this.mEGLSurface == null) {
                        throw new RuntimeException("surface was null");
                    }
                }
            }
        }
    }

    public void release() {
        if (this.mEGL != null) {
            if (this.mEGL.eglGetCurrentContext().equals(this.mEGLContext)) {
                this.mEGL.eglMakeCurrent(this.mEGLDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            }

            this.mEGL.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
            this.mEGL.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
        }

        this.mSurface.release();
        this.mEGLDisplay = null;
        this.mEGLContext = null;
        this.mEGLSurface = null;
        this.mEGL = null;
        this.mTextureRender = null;
        this.mSurface = null;
        this.mSurfaceTexture = null;
    }

    public void makeCurrent() {
        if (this.mEGL == null) {
            throw new RuntimeException("not configured for makeCurrent");
        } else {
            this.checkEglError("before makeCurrent");
            if (!this.mEGL.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext)) {
                throw new RuntimeException("eglMakeCurrent failed");
            }
        }
    }

    public Surface getSurface() {
        return this.mSurface;
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

    public void drawImage(boolean invert) {
        this.mTextureRender.drawFrame(this.mSurfaceTexture, invert);
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

    public ByteBuffer getFrame() {
        this.mPixelBuf.rewind();
        GLES20.glReadPixels(0, 0, this.mWidth, this.mHeight, 6408, 5121, this.mPixelBuf);
        return this.mPixelBuf;
    }

    private void checkEglError(String msg) {
        if (this.mEGL.eglGetError() != 12288) {
            throw new RuntimeException("EGL error encountered (see log)");
        }
    }
}