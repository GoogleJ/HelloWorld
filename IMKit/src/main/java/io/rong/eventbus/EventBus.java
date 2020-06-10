//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.eventbus;

import android.os.Looper;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public class EventBus {
    public static String TAG = "Event";
    static volatile EventBus defaultInstance;
    private static final EventBusBuilder DEFAULT_BUILDER = new EventBusBuilder();
    private static final Map<Class<?>, List<Class<?>>> eventTypesCache = new HashMap();
    private final Map<Class<?>, CopyOnWriteArrayList<Subscription>> subscriptionsByEventType;
    private final Map<Object, List<Class<?>>> typesBySubscriber;
    private final Map<Class<?>, Object> stickyEvents;
    private final ThreadLocal<PostingThreadState> currentPostingThreadState;
    private final HandlerPoster mainThreadPoster;
    private final BackgroundPoster backgroundPoster;
    private final AsyncPoster asyncPoster;
    private final SubscriberMethodFinder subscriberMethodFinder;
    private final ExecutorService executorService;
    private final boolean throwSubscriberException;
    private final boolean logSubscriberExceptions;
    private final boolean logNoSubscriberMessages;
    private final boolean sendSubscriberExceptionEvent;
    private final boolean sendNoSubscriberEvent;
    private final boolean eventInheritance;

    public static EventBus getDefault() {
        if (defaultInstance == null) {
            Class var0 = EventBus.class;
            synchronized(EventBus.class) {
                if (defaultInstance == null) {
                    defaultInstance = new EventBus();
                }
            }
        }

        return defaultInstance;
    }

    public static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    public static void clearCaches() {
        SubscriberMethodFinder.clearCaches();
        eventTypesCache.clear();
    }

    public EventBus() {
        this(DEFAULT_BUILDER);
    }

    EventBus(EventBusBuilder builder) {
        this.currentPostingThreadState = new ThreadLocal<PostingThreadState>() {
            protected PostingThreadState initialValue() {
                return new PostingThreadState();
            }
        };
        this.subscriptionsByEventType = new HashMap();
        this.typesBySubscriber = new HashMap();
        this.stickyEvents = new ConcurrentHashMap();
        this.mainThreadPoster = new HandlerPoster(this, Looper.getMainLooper(), 10);
        this.backgroundPoster = new BackgroundPoster(this);
        this.asyncPoster = new AsyncPoster(this);
        this.subscriberMethodFinder = new SubscriberMethodFinder(builder.skipMethodVerificationForClasses);
        this.logSubscriberExceptions = builder.logSubscriberExceptions;
        this.logNoSubscriberMessages = builder.logNoSubscriberMessages;
        this.sendSubscriberExceptionEvent = builder.sendSubscriberExceptionEvent;
        this.sendNoSubscriberEvent = builder.sendNoSubscriberEvent;
        this.throwSubscriberException = builder.throwSubscriberException;
        this.eventInheritance = builder.eventInheritance;
        this.executorService = builder.executorService;
    }

    public void register(Object subscriber) {
        this.register(subscriber, false, 0);
    }

    public void register(Object subscriber, int priority) {
        this.register(subscriber, false, priority);
    }

    public void registerSticky(Object subscriber) {
        this.register(subscriber, true, 0);
    }

    public void registerSticky(Object subscriber, int priority) {
        this.register(subscriber, true, priority);
    }

    private synchronized void register(Object subscriber, boolean sticky, int priority) {
        List<SubscriberMethod> subscriberMethods = this.subscriberMethodFinder.findSubscriberMethods(subscriber.getClass());
        Iterator var5 = subscriberMethods.iterator();

        while(var5.hasNext()) {
            SubscriberMethod subscriberMethod = (SubscriberMethod)var5.next();
            this.subscribe(subscriber, subscriberMethod, sticky, priority);
        }

    }

    private void subscribe(Object subscriber, SubscriberMethod subscriberMethod, boolean sticky, int priority) {
        Class<?> eventType = subscriberMethod.eventType;
        CopyOnWriteArrayList<Subscription> subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventType);
        Subscription newSubscription = new Subscription(subscriber, subscriberMethod, priority);
        if (subscriptions == null) {
            subscriptions = new CopyOnWriteArrayList();
            this.subscriptionsByEventType.put(eventType, subscriptions);
        } else if (subscriptions.contains(newSubscription)) {
            throw new EventBusException("Subscriber " + subscriber.getClass() + " already registered to event " + eventType);
        }

        int size = subscriptions.size();

        for(int i = 0; i <= size; ++i) {
            if (i == size || newSubscription.priority > ((Subscription)subscriptions.get(i)).priority) {
                subscriptions.add(i, newSubscription);
                break;
            }
        }

        List<Class<?>> subscribedEvents = (List)this.typesBySubscriber.get(subscriber);
        if (subscribedEvents == null) {
            subscribedEvents = new ArrayList();
            this.typesBySubscriber.put(subscriber, subscribedEvents);
        }

        ((List)subscribedEvents).add(eventType);
        if (sticky) {
            Object stickyEvent;
            synchronized(this.stickyEvents) {
                stickyEvent = this.stickyEvents.get(eventType);
            }

            if (stickyEvent != null) {
                this.postToSubscription(newSubscription, stickyEvent, Looper.getMainLooper() == Looper.myLooper());
            }
        }

    }

    public synchronized boolean isRegistered(Object subscriber) {
        return this.typesBySubscriber.containsKey(subscriber);
    }

    private void unubscribeByEventType(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptions = (List)this.subscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();

            for(int i = 0; i < size; ++i) {
                Subscription subscription = (Subscription)subscriptions.get(i);
                if (subscription.subscriber == subscriber) {
                    subscription.active = false;
                    subscriptions.remove(i);
                    --i;
                    --size;
                }
            }
        }

    }

    public synchronized void unregister(Object subscriber) {
        List<Class<?>> subscribedTypes = (List)this.typesBySubscriber.get(subscriber);
        if (subscribedTypes != null) {
            Iterator var3 = subscribedTypes.iterator();

            while(var3.hasNext()) {
                Class<?> eventType = (Class)var3.next();
                this.unubscribeByEventType(subscriber, eventType);
            }

            this.typesBySubscriber.remove(subscriber);
        } else {
            Log.w(TAG, "Subscriber to unregister was not registered before: " + subscriber.getClass());
        }

    }

    public void post(Object event) {
        PostingThreadState postingState = (PostingThreadState)this.currentPostingThreadState.get();
        List<Object> eventQueue = postingState.eventQueue;
        eventQueue.add(event);
        if (!postingState.isPosting) {
            postingState.isMainThread = Looper.getMainLooper() == Looper.myLooper();
            postingState.isPosting = true;
            if (postingState.canceled) {
                throw new EventBusException("Internal error. Abort state was not reset");
            }

            try {
                while(!eventQueue.isEmpty()) {
                    this.postSingleEvent(eventQueue.remove(0), postingState);
                }
            } finally {
                postingState.isPosting = false;
                postingState.isMainThread = false;
            }
        }

    }

    public void cancelEventDelivery(Object event) {
        PostingThreadState postingState = (PostingThreadState)this.currentPostingThreadState.get();
        if (!postingState.isPosting) {
            throw new EventBusException("This method may only be called from inside event handling methods on the posting thread");
        } else if (event == null) {
            throw new EventBusException("Event may not be null");
        } else if (postingState.event != event) {
            throw new EventBusException("Only the currently handled event may be aborted");
        } else if (postingState.subscription.subscriberMethod.threadMode != ThreadMode.PostThread) {
            throw new EventBusException(" event handlers may only abort the incoming event");
        } else {
            postingState.canceled = true;
        }
    }

    public void postSticky(Object event) {
        synchronized(this.stickyEvents) {
            this.stickyEvents.put(event.getClass(), event);
        }

        this.post(event);
    }

    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized(this.stickyEvents) {
            return eventType.cast(this.stickyEvents.get(eventType));
        }
    }

    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized(this.stickyEvents) {
            return eventType.cast(this.stickyEvents.remove(eventType));
        }
    }

    public boolean removeStickyEvent(Object event) {
        synchronized(this.stickyEvents) {
            Class<?> eventType = event.getClass();
            Object existingEvent = this.stickyEvents.get(eventType);
            if (event.equals(existingEvent)) {
                this.stickyEvents.remove(eventType);
                return true;
            } else {
                return false;
            }
        }
    }

    public void removeAllStickyEvents() {
        synchronized(this.stickyEvents) {
            this.stickyEvents.clear();
        }
    }

    public boolean hasSubscriberForEvent(Class<?> eventClass) {
        List<Class<?>> eventTypes = this.lookupAllEventTypes(eventClass);
        if (eventTypes != null) {
            int countTypes = eventTypes.size();

            for(int h = 0; h < countTypes; ++h) {
                Class<?> clazz = (Class)eventTypes.get(h);
                CopyOnWriteArrayList subscriptions;
                synchronized(this) {
                    subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(clazz);
                }

                if (subscriptions != null && !subscriptions.isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void postSingleEvent(Object event, PostingThreadState postingState) throws Error {
        Class<?> eventClass = event.getClass();
        boolean subscriptionFound = false;
        if (this.eventInheritance) {
            List<Class<?>> eventTypes = this.lookupAllEventTypes(eventClass);
            int countTypes = eventTypes.size();

            for(int h = 0; h < countTypes; ++h) {
                Class<?> clazz = (Class)eventTypes.get(h);
                subscriptionFound |= this.postSingleEventForEventType(event, postingState, clazz);
            }
        } else {
            subscriptionFound = this.postSingleEventForEventType(event, postingState, eventClass);
        }

        if (!subscriptionFound) {
            if (this.logNoSubscriberMessages) {
                Log.d(TAG, "No subscribers registered for event " + eventClass);
            }

            if (this.sendNoSubscriberEvent && eventClass != NoSubscriberEvent.class && eventClass != SubscriberExceptionEvent.class) {
                this.post(new NoSubscriberEvent(this, event));
            }
        }

    }

    private boolean postSingleEventForEventType(Object event, PostingThreadState postingState, Class<?> eventClass) {
        CopyOnWriteArrayList subscriptions;
        synchronized(this) {
            subscriptions = (CopyOnWriteArrayList)this.subscriptionsByEventType.get(eventClass);
        }

        if (subscriptions != null && !subscriptions.isEmpty()) {
            Iterator var5 = subscriptions.iterator();

            while(var5.hasNext()) {
                Subscription subscription = (Subscription)var5.next();
                postingState.event = event;
                postingState.subscription = subscription;

                boolean aborted;
                try {
                    this.postToSubscription(subscription, event, postingState.isMainThread);
                    aborted = postingState.canceled;
                } finally {
                    postingState.event = null;
                    postingState.subscription = null;
                    postingState.canceled = false;
                }

                if (aborted) {
                    break;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        switch(subscription.subscriberMethod.threadMode) {
            case PostThread:
                this.invokeSubscriber(subscription, event);
                break;
            case MainThread:
                if (isMainThread) {
                    this.invokeSubscriber(subscription, event);
                } else {
                    this.mainThreadPoster.enqueue(subscription, event);
                }
                break;
            case BackgroundThread:
                if (isMainThread) {
                    this.backgroundPoster.enqueue(subscription, event);
                } else {
                    this.invokeSubscriber(subscription, event);
                }
                break;
            case Async:
                this.asyncPoster.enqueue(subscription, event);
                break;
            default:
                throw new IllegalStateException("Unknown thread mode: " + subscription.subscriberMethod.threadMode);
        }

    }

    private List<Class<?>> lookupAllEventTypes(Class<?> eventClass) {
        synchronized(eventTypesCache) {
            List<Class<?>> eventTypes = (List)eventTypesCache.get(eventClass);
            if (eventTypes == null) {
                eventTypes = new ArrayList();

                for(Class clazz = eventClass; clazz != null; clazz = clazz.getSuperclass()) {
                    ((List)eventTypes).add(clazz);
                    addInterfaces((List)eventTypes, clazz.getInterfaces());
                }

                eventTypesCache.put(eventClass, eventTypes);
            }

            return (List)eventTypes;
        }
    }

    static void addInterfaces(List<Class<?>> eventTypes, Class<?>[] interfaces) {
        Class[] var2 = interfaces;
        int var3 = interfaces.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> interfaceClass = var2[var4];
            if (!eventTypes.contains(interfaceClass)) {
                eventTypes.add(interfaceClass);
                addInterfaces(eventTypes, interfaceClass.getInterfaces());
            }
        }

    }

    void invokeSubscriber(PendingPost pendingPost) {
        Object event = pendingPost.event;
        Subscription subscription = pendingPost.subscription;
        PendingPost.releasePendingPost(pendingPost);
        if (subscription.active) {
            this.invokeSubscriber(subscription, event);
        }

    }

    void invokeSubscriber(Subscription subscription, Object event) {
        try {
            subscription.subscriberMethod.method.invoke(subscription.subscriber, event);
        } catch (InvocationTargetException var4) {
            this.handleSubscriberException(subscription, event, var4.getCause());
        } catch (IllegalAccessException var5) {
            throw new IllegalStateException("Unexpected exception", var5);
        }

    }

    private void handleSubscriberException(Subscription subscription, Object event, Throwable cause) {
        SubscriberExceptionEvent exEvent;
        if (event instanceof SubscriberExceptionEvent) {
            if (this.logSubscriberExceptions) {
                Log.e(TAG, "SubscriberExceptionEvent subscriber " + subscription.subscriber.getClass() + " threw an exception", cause);
                exEvent = (SubscriberExceptionEvent)event;
                Log.e(TAG, "Initial event " + exEvent.causingEvent + " caused exception in " + exEvent.causingSubscriber, exEvent.throwable);
            }
        } else {
            if (this.throwSubscriberException) {
                throw new EventBusException("Invoking subscriber failed", cause);
            }

            if (this.logSubscriberExceptions) {
                Log.e(TAG, "Could not dispatch event: " + event.getClass() + " to subscribing class " + subscription.subscriber.getClass(), cause);
            }

            if (this.sendSubscriberExceptionEvent) {
                exEvent = new SubscriberExceptionEvent(this, cause, event, subscription.subscriber);
                this.post(exEvent);
            }
        }

    }

    ExecutorService getExecutorService() {
        return this.executorService;
    }

    interface PostCallback {
        void onPostCompleted(List<SubscriberExceptionEvent> var1);
    }

    static final class PostingThreadState {
        final List<Object> eventQueue = new ArrayList();
        boolean isPosting;
        boolean isMainThread;
        Subscription subscription;
        Object event;
        boolean canceled;

        PostingThreadState() {
        }
    }
}
