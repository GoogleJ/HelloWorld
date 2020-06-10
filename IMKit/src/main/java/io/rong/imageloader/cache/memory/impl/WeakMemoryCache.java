//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imageloader.cache.memory.impl;

import android.graphics.Bitmap;
import io.rong.imageloader.cache.memory.BaseMemoryCache;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WeakMemoryCache extends BaseMemoryCache {
    public WeakMemoryCache() {
    }

    protected Reference<Bitmap> createReference(Bitmap value) {
        return new WeakReference(value);
    }
}
