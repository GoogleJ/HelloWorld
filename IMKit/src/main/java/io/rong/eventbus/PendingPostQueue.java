//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.eventbus;

final class PendingPostQueue {
    private PendingPost head;
    private PendingPost tail;

    PendingPostQueue() {
    }

    synchronized void enqueue(PendingPost pendingPost) {
        if (pendingPost == null) {
            throw new NullPointerException("null cannot be enqueued");
        } else {
            if (this.tail != null) {
                this.tail.next = pendingPost;
                this.tail = pendingPost;
            } else {
                if (this.head != null) {
                    throw new IllegalStateException("Head present, but no tail");
                }

                this.head = this.tail = pendingPost;
            }

            this.notifyAll();
        }
    }

    synchronized PendingPost poll() {
        PendingPost pendingPost = this.head;
        if (this.head != null) {
            this.head = this.head.next;
            if (this.head == null) {
                this.tail = null;
            }
        }

        return pendingPost;
    }

    synchronized PendingPost poll(int maxMillisToWait) throws InterruptedException {
        if (this.head == null) {
            this.wait((long)maxMillisToWait);
        }

        return this.poll();
    }
}
