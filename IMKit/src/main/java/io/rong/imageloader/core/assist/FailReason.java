//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imageloader.core.assist;

public class FailReason {
    private final FailReason.FailType type;
    private final Throwable cause;

    public FailReason(FailReason.FailType type, Throwable cause) {
        this.type = type;
        this.cause = cause;
    }

    public FailReason.FailType getType() {
        return this.type;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public static enum FailType {
        IO_ERROR,
        DECODING_ERROR,
        NETWORK_DENIED,
        OUT_OF_MEMORY,
        UNKNOWN;

        private FailType() {
        }
    }
}
