//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.eventbus;

import java.lang.reflect.Method;

final class SubscriberMethod {
    final Method method;
    final ThreadMode threadMode;
    final Class<?> eventType;
    String methodString;

    SubscriberMethod(Method method, ThreadMode threadMode, Class<?> eventType) {
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public boolean equals(Object other) {
        if (other instanceof SubscriberMethod) {
            this.checkMethodString();
            SubscriberMethod otherSubscriberMethod = (SubscriberMethod)other;
            otherSubscriberMethod.checkMethodString();
            return this.methodString.equals(otherSubscriberMethod.methodString);
        } else {
            return false;
        }
    }

    private synchronized void checkMethodString() {
        if (this.methodString == null) {
            this.methodString = this.method.getDeclaringClass().getName() + '#' + this.method.getName() + '(' + this.eventType.getName();
        }

    }

    public int hashCode() {
        return this.method.hashCode();
    }
}
