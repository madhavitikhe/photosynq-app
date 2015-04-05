package com.photosynq.app;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.ViewFlipper;

import com.google.android.gms.games.quest.Quest;
import com.photosynq.app.db.DatabaseHelper;
import com.photosynq.app.model.Question;
import com.photosynq.app.model.RememberAnswers;
import com.photosynq.app.utils.Constants;
import com.photosynq.app.utils.PrefUtils;

/**
 * Created by shekhar on 4/2/15.
 */
public class QuestionViewFlipper extends ViewFlipper {

    private DatabaseHelper dbHelper = DatabaseHelper.getHelper(getContext());
    private ProjectMeasurmentActivity projectMeasurmentActivity;
    final String userId = PrefUtils.getFromPrefs(getContext(), PrefUtils.PREFS_LOGIN_USERNAME_KEY, PrefUtils.PREFS_DEFAULT_VAL);

    public QuestionViewFlipper(Context context) {
        super(context);
    }

    public QuestionViewFlipper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        projectMeasurmentActivity = ((ProjectMeasurmentActivity)((RelativeLayout)(getParent().getParent())).getContext());
        if(null != getTag() && null != getCurrentView().getTag()) {
            showNextIfRemembered();
        }
        projectMeasurmentActivity.userDefinedOptions();
    }

    @Override
    public void setDisplayedChild(int whichChild) {
        super.setDisplayedChild(whichChild);
        projectMeasurmentActivity = ((ProjectMeasurmentActivity)((RelativeLayout)(getParent().getParent())).getContext());
        if(null != getTag() && null != getCurrentView().getTag()) {
            projectMeasurmentActivity.initReviewPage();
            showNextIfRemembered();
        }
        projectMeasurmentActivity.userDefinedOptions();
    }

    private void showNextIfRemembered()
    {

        RememberAnswers rememberedAnswers = dbHelper.getRememberAnswers(userId, getTag().toString(), getCurrentView().getTag().toString());
        if(rememberedAnswers.getIs_remember() != null && rememberedAnswers.getIs_remember().equals(Constants.IS_REMEMBER))
        {
            if(!projectMeasurmentActivity.reviewFlag) {
                showNext();
            }
        }
    }

    @Override
    public void showNext() {
        super.showNext();
        projectMeasurmentActivity.initReviewPage();
        projectMeasurmentActivity.userDefinedOptions();

    }
}
