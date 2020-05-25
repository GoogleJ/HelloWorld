//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.rong.imkit.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import io.rong.common.RLog;
import io.rong.imkit.R.drawable;
import io.rong.imkit.R.id;
import io.rong.imkit.R.layout;
import io.rong.imkit.R.string;
import io.rong.imlib.CustomServiceConfig.CSEvaSolveStatus;
import io.rong.imlib.RongIMClient;

public class CSEvaluateDialog extends AlertDialog {
    private static final String TAG = "CSEvaluateDialog";
    private int mStars;
    private boolean mResolved;
    private String mTargetId;
    private CSEvaSolveStatus mSolveStatus;
    private CSEvaluateDialog.EvaluateClickListener mClickListener;

    public CSEvaluateDialog(Context context, String targetId) {
        super(context);
        this.setCanceledOnTouchOutside(false);
        this.mTargetId = targetId;
    }

    public void setClickListener(CSEvaluateDialog.EvaluateClickListener listener) {
        this.mClickListener = listener;
    }

    public void showRobot(boolean resolved) {
        this.show();
        this.setContentView(layout.rc_cs_alert_robot_evaluation);
        final LinearLayout linearLayout = (LinearLayout) this.findViewById(id.rc_cs_yes_no);
        if (resolved) {
            linearLayout.getChildAt(0).setSelected(true);
            linearLayout.getChildAt(1).setSelected(false);
        } else {
            linearLayout.getChildAt(0).setSelected(false);
            linearLayout.getChildAt(1).setSelected(true);
        }

        for (int i = 0; i < linearLayout.getChildCount(); ++i) {
            View child = linearLayout.getChildAt(i);
            child.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    v.setSelected(true);
                    int index = linearLayout.indexOfChild(v);
                    if (index == 0) {
                        linearLayout.getChildAt(1).setSelected(false);
                        CSEvaluateDialog.this.mResolved = true;
                    } else {
                        CSEvaluateDialog.this.mResolved = false;
                        linearLayout.getChildAt(0).setSelected(false);
                    }

                }
            });
        }

        this.findViewById(id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateCanceled();
                }

            }
        });
        this.findViewById(id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RongIMClient.getInstance().evaluateCustomService(CSEvaluateDialog.this.mTargetId, CSEvaluateDialog.this.mResolved, "");
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateSubmit();
                }

            }
        });
    }

    public void showStar(final String dialogId) {
        this.show();
        int stars = 0;
        this.setContentView(layout.rc_cs_alert_human_evaluation);
        final LinearLayout linearLayout = (LinearLayout) this.findViewById(id.rc_cs_stars);

        for (int i = 0; i < linearLayout.getChildCount(); ++i) {
            View child = linearLayout.getChildAt(i);
            if (i < stars) {
                child.setSelected(true);
            }

            child.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int index = linearLayout.indexOfChild(v);
                    int count = linearLayout.getChildCount();
                    CSEvaluateDialog.this.mStars = index + 1;
                    if (!v.isSelected()) {
                        while (index >= 0) {
                            linearLayout.getChildAt(index).setSelected(true);
                            --index;
                        }
                    } else {
                        ++index;

                        while (index < count) {
                            linearLayout.getChildAt(index).setSelected(false);
                            ++index;
                        }
                    }

                }
            });
        }

        this.findViewById(id.rc_btn_cancel).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateCanceled();
                }

            }
        });
        this.findViewById(id.rc_btn_ok).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RongIMClient.getInstance().evaluateCustomService(CSEvaluateDialog.this.mTargetId, CSEvaluateDialog.this.mStars, (String) null, dialogId);
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateSubmit();
                }

            }
        });
    }

    public void showStarMessage(boolean resolvedOption) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(this.getContext()).inflate(layout.rc_cs_evaluate, (ViewGroup) null);
        this.setView(view);
        this.show();
        if (this.getWindow() != null) {
            this.getWindow().setContentView(layout.rc_cs_evaluate);
        } else {
            RLog.e("CSEvaluateDialog", "getWindow is null.");
        }

        RatingBar ratingBar = (RatingBar) this.findViewById(id.rc_rating_bar);
        final TextView evaluateLevel = (TextView) this.findViewById(id.rc_evaluate_level);
        TextView isResolved = (TextView) this.findViewById(id.rc_cs_resolved_or_not);
        LinearLayout progressLayout = (LinearLayout) this.findViewById(id.rc_resolve_progress);
        final ImageView resolvedBtn = (ImageView) this.findViewById(id.rc_cs_resolved);
        final ImageView resolvingBtn = (ImageView) this.findViewById(id.rc_cs_resolving);
        final ImageView unresolvedBtn = (ImageView) this.findViewById(id.rc_cs_unresolved);
        final EditText edit = (EditText) this.findViewById(id.rc_cs_evaluate_content);
        ImageView closeBtn = (ImageView) this.findViewById(id.rc_close_button);
        TextView submitBtn = (TextView) this.findViewById(id.rc_submit_button);
        this.mStars = 5;
        if (resolvedOption) {
            isResolved.setVisibility(0);
            progressLayout.setVisibility(0);
            this.mSolveStatus = CSEvaSolveStatus.RESOLVED;
        } else {
            isResolved.setVisibility(8);
            progressLayout.setVisibility(8);
        }

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (ratingBar.getId() == id.rc_rating_bar) {
                    if (rating >= 5.0F) {
                        CSEvaluateDialog.this.mStars = 5;
                        evaluateLevel.setText(string.rc_cs_very_satisfactory);
                    } else if (rating >= 4.0F && rating < 5.0F) {
                        CSEvaluateDialog.this.mStars = 4;
                        evaluateLevel.setText(string.rc_cs_satisfactory);
                    } else if (rating >= 3.0F && rating < 4.0F) {
                        CSEvaluateDialog.this.mStars = 3;
                        evaluateLevel.setText(string.rc_cs_average);
                    } else if (rating >= 2.0F && rating < 3.0F) {
                        CSEvaluateDialog.this.mStars = 2;
                        evaluateLevel.setText(string.rc_cs_unsatisfactory);
                    } else {
                        CSEvaluateDialog.this.mStars = 1;
                        evaluateLevel.setText(string.rc_cs_very_unsatisfactory);
                    }
                }

            }
        });
        evaluateLevel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        resolvedBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CSEvaluateDialog.this.mSolveStatus = CSEvaSolveStatus.RESOLVED;
                resolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_resolved_hover));
                resolvingBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_follow));
                unresolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_unresolved));
            }
        });
        resolvingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CSEvaluateDialog.this.mSolveStatus = CSEvaSolveStatus.RESOLVING;
                resolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_resolved));
                resolvingBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_follow_hover));
                unresolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_unresolved));
            }
        });
        unresolvedBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CSEvaluateDialog.this.mSolveStatus = CSEvaSolveStatus.UNRESOLVED;
                resolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_resolved));
                resolvingBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_follow));
                unresolvedBtn.setImageDrawable(v.getResources().getDrawable(drawable.rc_cs_unresolved_hover));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService("input_method");
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateCanceled();
                }

            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) edit.getContext().getSystemService("input_method");
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                RongIMClient.getInstance().evaluateCustomService(CSEvaluateDialog.this.mTargetId, CSEvaluateDialog.this.mStars, CSEvaluateDialog.this.mSolveStatus, edit.getText().toString(), (String) null);
                if (CSEvaluateDialog.this.mClickListener != null) {
                    CSEvaluateDialog.this.mClickListener.onEvaluateSubmit();
                }

            }
        });
    }

    public void destroy() {
        this.dismiss();
    }

    public void setOnCancelListener(OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    public interface EvaluateClickListener {
        void onEvaluateSubmit();

        void onEvaluateCanceled();
    }
}