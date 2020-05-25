//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imageloader.cache.disc.naming;

public class HashCodeFileNameGenerator implements FileNameGenerator {
    public HashCodeFileNameGenerator() {
    }

    public String generate(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }
}
