//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.eventbus.util;

import android.annotation.TargetApi;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

public abstract class ErrorDialogFragmentFactory<T> {
    protected final ErrorDialogConfig config;

    protected ErrorDialogFragmentFactory(ErrorDialogConfig config) {
        this.config = config;
    }

    protected T prepareErrorFragment(ThrowableFailureEvent event, boolean finishAfterDialog, Bundle argumentsForErrorDialog) {
        if (event.isSuppressErrorUi()) {
            return null;
        } else {
            Bundle bundle;
            if (argumentsForErrorDialog != null) {
                bundle = (Bundle)argumentsForErrorDialog.clone();
            } else {
                bundle = new Bundle();
            }

            String message;
            if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.title")) {
                message = this.getTitleFor(event, bundle);
                bundle.putString("de.greenrobot.eventbus.errordialog.title", message);
            }

            if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.message")) {
                message = this.getMessageFor(event, bundle);
                bundle.putString("de.greenrobot.eventbus.errordialog.message", message);
            }

            if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.finish_after_dialog")) {
                bundle.putBoolean("de.greenrobot.eventbus.errordialog.finish_after_dialog", finishAfterDialog);
            }

            if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.event_type_on_close") && this.config.defaultEventTypeOnDialogClosed != null) {
                bundle.putSerializable("de.greenrobot.eventbus.errordialog.event_type_on_close", this.config.defaultEventTypeOnDialogClosed);
            }

            if (!bundle.containsKey("de.greenrobot.eventbus.errordialog.icon_id") && this.config.defaultDialogIconId != 0) {
                bundle.putInt("de.greenrobot.eventbus.errordialog.icon_id", this.config.defaultDialogIconId);
            }

            return this.createErrorFragment(event, bundle);
        }
    }

    protected abstract T createErrorFragment(ThrowableFailureEvent var1, Bundle var2);

    protected String getTitleFor(ThrowableFailureEvent event, Bundle arguments) {
        return this.config.resources.getString(this.config.defaultTitleId);
    }

    protected String getMessageFor(ThrowableFailureEvent event, Bundle arguments) {
        int msgResId = this.config.getMessageIdForThrowable(event.throwable);
        return this.config.resources.getString(msgResId);
    }

    @TargetApi(11)
    public static class Honeycomb extends ErrorDialogFragmentFactory<Fragment> {
        public Honeycomb(ErrorDialogConfig config) {
            super(config);
        }

        protected Fragment createErrorFragment(ThrowableFailureEvent event, Bundle arguments) {
            ErrorDialogFragments.Honeycomb errorFragment = new ErrorDialogFragments.Honeycomb();
            errorFragment.setArguments(arguments);
            return errorFragment;
        }
    }

    public static class Support extends ErrorDialogFragmentFactory<Fragment> {
        public Support(ErrorDialogConfig config) {
            super(config);
        }

        protected Fragment createErrorFragment(ThrowableFailureEvent event, Bundle arguments) {
            ErrorDialogFragments.Support errorFragment = new ErrorDialogFragments.Support();
            errorFragment.setArguments(arguments);
            return errorFragment;
        }
    }
}
