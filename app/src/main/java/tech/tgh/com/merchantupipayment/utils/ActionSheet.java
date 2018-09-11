package tech.tgh.com.merchantupipayment.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.util.Arrays;

import java.util.List;
import tech.tgh.com.merchantupipayment.R;

public class ActionSheet extends Dialog implements View.OnClickListener{
    private static final String TAG = "ActionSheet";
    private static final int CANCEL_BUTTON_ID = 100;
    private static final int BG_VIEW_ID = 10;
    private static final int TRANSLATE_DURATION = 300;
    private static final int ALPHA_DURATION = 300;

    private Context mContext;
    private Attributes mAttrs;
    private MenuItemClickListener mListener;
    private View mView;
    private LinearLayout mPanel;
    private View mbg;
    private List<String> itemsView;
    private String cancelTitle = "";
    private boolean mCancelableOnTouchOutside;
    private boolean mDismissed = true;
    private boolean isCancel = true;

    private boolean isListView = false;
    private View customView;

    public ActionSheet(@NonNull Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar); // Full Screen
        this.mContext = context;
        initViews();
        getWindow().setGravity(Gravity.BOTTOM);
        Drawable drawable = new ColorDrawable();
        drawable.setAlpha(0); // set transparent background
        getWindow().setBackgroundDrawable(drawable);
    }

    public ActionSheet(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ActionSheet(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void initViews() {
        /* Hide soft keyboard*/
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) mContext).getCurrentFocus();
            if (focusView != null)
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
        mAttrs = readAttribute(); // Get themes properties
        mView = createView();
        mbg.startAnimation(createAlphaInAnimation());
        mPanel.startAnimation(createTranslationInAnimation());
    }

    private Animation createTranslationInAnimation(){
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation  translateAnimation =  new  TranslateAnimation (type,0,type,0,type,1,type,0);
        translateAnimation.setDuration (TRANSLATE_DURATION);
        return  translateAnimation;
    }

    private Animation createAlphaInAnimation(){
        AlphaAnimation alphaAnimation = new AlphaAnimation (0,1);
        alphaAnimation.setDuration (ALPHA_DURATION);
        return  alphaAnimation;
    }

    private Animation createTranslationOutAnimation(){
        int type = TranslateAnimation.RELATIVE_TO_SELF;
        TranslateAnimation translateAnimation = new TranslateAnimation(type,0,type,0,type,0,type,1);
        translateAnimation.setDuration(TRANSLATE_DURATION);
        translateAnimation.setFillAfter(true);
        return  translateAnimation;
    }

    private Animation createAlphaOutAnimation(){
        AlphaAnimation  alphaAnimation = new AlphaAnimation(1,0);
        alphaAnimation.setDuration(ALPHA_DURATION);
        alphaAnimation.setFillAfter(true);
        return  alphaAnimation;
    }

    /*Create a basic background view*/
    private View createView(){
        FrameLayout parent = new FrameLayout(mContext);
        FrameLayout.LayoutParams  parentParams = new FrameLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentParams.gravity = Gravity.BOTTOM;
        parent.setLayoutParams (parentParams);

        mbg = new View(mContext);
        mbg.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mbg.setBackgroundColor (Color.argb (136,0,0,0));
        mbg.setId(ActionSheet.BG_VIEW_ID);
        mbg.setOnClickListener(this);

        mPanel = new LinearLayout (mContext);//container
        FrameLayout.LayoutParams  mPanelParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mPanelParams.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams (mPanelParams);//set param on container
        mPanel.setOrientation (LinearLayout.VERTICAL);
        parent.addView (mbg);
        parent.addView (mPanel);
        return  parent;
    }

    /*Create a MenuItem*/
    private void createItems() {
        if (isListView) {
            if (itemsView != null && itemsView.size() > 0) {
                for (int i = 0; i < itemsView.size(); i++) {
                    Button btn = new Button(mContext);
                    btn.setId(CANCEL_BUTTON_ID + i + 1);
                    btn.setOnClickListener(this);
                    btn.setBackgroundDrawable(getOtherButtonBg(itemsView.toArray(new String[itemsView.size()]), i));
                    btn.setText(itemsView.get(i));
                    btn.setTextColor(mAttrs.otherButtonTextColor);
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
                    if (i > 0) {
                        LinearLayout.LayoutParams params = createButtonLayoutParams();
                        params.topMargin = mAttrs.otherButtonSpacing;
                        mPanel.addView(btn, params);
                    } else {
                        mPanel.addView(btn);
                    }
                }
            }
        } else {
            mPanel.addView(customView);
        }
        Button btn1 = new Button(mContext);
        btn1.getPaint().setFakeBoldText(true);
        btn1.setTextSize(TypedValue.COMPLEX_UNIT_PX, mAttrs.actionSheetTextSize);
        btn1.setId(ActionSheet.CANCEL_BUTTON_ID);
        //btn1.setBackgroundDrawable(mAttrs.cancelButtonBackground);
        btn1.setBackgroundDrawable(CommonUtil.setRoundedCorner(Color.WHITE,Color.WHITE,10, GradientDrawable.RECTANGLE));
        btn1.setText(cancelTitle);
        btn1.setTextColor(mAttrs.cancelButtonTextColor);
        btn1.setOnClickListener(this);
        LinearLayout.LayoutParams params = createButtonLayoutParams();
        params.topMargin = mAttrs.cancelButtonMarginTop;
        mPanel.addView(btn1, params);
        mPanel.setBackgroundDrawable(mAttrs.background);
        //mPanel.setBackgroundDrawable(CommonUtil.setRoundedCorner(mContext,Color.WHITE,Color.WHITE,10));
        mPanel.setPadding(mAttrs.padding, mAttrs.padding, mAttrs.padding, mAttrs.padding);
    }

    private LinearLayout.LayoutParams createButtonLayoutParams(){
        LinearLayout.LayoutParams  params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return  params;
    }
    /*
     The color of the item button
     @param titles
     @param i
     @return*/
    private Drawable getOtherButtonBg(String [] titles, int i){
        if(titles.length == 1) {
            return mAttrs.otherButtonSingleBackground;
        }else if(titles.length ==  2 ){
            switch(i){
                case 0:
                    return mAttrs.otherButtonTopBackground;
                case 1:
                    return mAttrs.otherButtonBottomBackground;
            }
        }else if(titles.length>  2 ){
            if(i == 0)
                return mAttrs.otherButtonTopBackground;
            else if(i == (titles.length - 1))
                return mAttrs.otherButtonBottomBackground;
            else
                return mAttrs.getOtherButtonMiddleBackground ();
        }
        return null ;
    }

    public void showMenu(){
        if(!mDismissed)
            return ;
        show();
        getWindow().setContentView(mView);
        mDismissed = false ;
    }

    /*
     * The dismiss menu
     */
    public void dismissMenu(){
        if(mDismissed)
            return ;
        dismiss();
        onDismiss();
        mDismissed = true ;
    }

    private void onDismiss(){
        mPanel.startAnimation (createTranslationOutAnimation ());
        mbg.startAnimation (createAlphaOutAnimation ());
    }

    /*
     * Cancel the caption text of the button
     *
     * @param title
     * @return
     */
    public ActionSheet setCancelButtonTitle (String title){
        this.cancelTitle = title;
        return this;
    }
    /*
     * whether the outer edge can be canceled
     *
     * @param cancelable
     * @return
     */
    public ActionSheet setCancelableOnTouchMenuOutside(Boolean cancelable){
        mCancelableOnTouchOutside = cancelable;
        return this;
    }
    public ActionSheet addItems(String...titles){
        this.isListView = true;
        if(titles == null || titles.length == 0)
            return this;
        itemsView = Arrays.asList(titles);
        createItems();
        return this;
    }
    public ActionSheet setItemClickListener(MenuItemClickListener listener){
        this.mListener = listener;
        return this;
    }

    public ActionSheet setBottomDialogAsCustomView(View customView){
        this.isListView = false;
        this.customView = customView;
        createItems();
        return this;
    }


    private Attributes readAttribute() {
        Attributes attrs = new Attributes(mContext);

        TypedArray a = mContext.getTheme().obtainStyledAttributes(null,
                R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);

        Drawable background = a
                .getDrawable(R.styleable.ActionSheet_actionSheetBackground);
        if (background != null) {
            attrs.background = background;
        }

        Drawable cancelButtonBackground = a
                .getDrawable(R.styleable.ActionSheet_cancelButtonBackground);
        if (cancelButtonBackground != null) {
            attrs.cancelButtonBackground = cancelButtonBackground;
        }

        Drawable otherButtonTopBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonTopBackground);
        if (otherButtonTopBackground != null) {
            attrs.otherButtonTopBackground = otherButtonTopBackground;
        }

        Drawable otherButtonMiddleBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
        if (otherButtonMiddleBackground != null) {
            attrs.otherButtonMiddleBackground = otherButtonMiddleBackground;
        }

        Drawable otherButtonBottomBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonBottomBackground);
        if (otherButtonBottomBackground != null) {
            attrs.otherButtonBottomBackground = otherButtonBottomBackground;
        }

        Drawable otherButtonSingleBackground = a
                .getDrawable(R.styleable.ActionSheet_otherButtonSingleBackground);
        if (otherButtonSingleBackground != null) {
            attrs.otherButtonSingleBackground = otherButtonSingleBackground;
        }

        attrs.cancelButtonTextColor = a.getColor(
                R.styleable.ActionSheet_cancelButtonTextColor,
                attrs.cancelButtonTextColor);
        attrs.otherButtonTextColor = a.getColor(
                R.styleable.ActionSheet_otherButtonTextColor,
                attrs.otherButtonTextColor);
        attrs.padding = (int) a.getDimension(
                R.styleable.ActionSheet_actionSheetPadding, attrs.padding);
        attrs.otherButtonSpacing = (int) a.getDimension(
                R.styleable.ActionSheet_otherButtonSpacing,
                attrs.otherButtonSpacing);
        attrs.cancelButtonMarginTop = (int) a.getDimension(
                R.styleable.ActionSheet_cancelButtonMarginTop,
                attrs.cancelButtonMarginTop);
        attrs.actionSheetTextSize = a.getDimensionPixelSize(R.styleable.ActionSheet_actionSheetTextSize, (int) attrs.actionSheetTextSize);
        a.recycle();
        return attrs;
    }

    @Override
    public void onClick(View v){
        if(v.getId () == ActionSheet.BG_VIEW_ID && !mCancelableOnTouchOutside)
            return ;
        dismissMenu ();
        if(v.getId() != ActionSheet.CANCEL_BUTTON_ID && v.getId() != ActionSheet.BG_VIEW_ID){
            if(mListener != null)
                mListener.onItemClick(v.getId () - CANCEL_BUTTON_ID - 1);
            isCancel = false;
        }
    }
    /*
     * Custom properties for control themes
     *
     */
    private class  Attributes {
        private Context mContext;
        private Drawable background;
        private Drawable cancelButtonBackground;
        private Drawable otherButtonTopBackground;
        private Drawable otherButtonMiddleBackground;
        private Drawable otherButtonBottomBackground;
        private Drawable otherButtonSingleBackground;
        private int cancelButtonTextColor;
        private int otherButtonTextColor;
        private int padding;
        private int otherButtonSpacing;
        private int cancelButtonMarginTop;
        private float actionSheetTextSize;

        public Attributes(Context context) {
            this.mContext = context;
            this.background = new ColorDrawable(Color.TRANSPARENT);
            this.cancelButtonBackground = new ColorDrawable(Color.BLACK);
            ColorDrawable gray = new ColorDrawable(Color.GRAY);
            this.otherButtonTopBackground = gray;
            this.otherButtonMiddleBackground = gray;
            this.otherButtonBottomBackground = gray;
            this.otherButtonSingleBackground = gray;
            this.cancelButtonTextColor = Color.WHITE;
            this.otherButtonTextColor = Color.BLACK;
            this.padding = dp2px(20);
            this.otherButtonSpacing = dp2px(2);
            this.cancelButtonMarginTop = dp2px(10);
            this.actionSheetTextSize = dp2px(16);
        }
        private int dp2px(int DP) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DP, mContext.getResources().getDisplayMetrics());
        }
        public Drawable getOtherButtonMiddleBackground() {
            if (otherButtonMiddleBackground instanceof StateListDrawable) {
                TypedArray a = mContext.getTheme().obtainStyledAttributes(null,
                        R.styleable.ActionSheet, R.attr.actionSheetStyle, 0);
                otherButtonMiddleBackground = a
                        .getDrawable(R.styleable.ActionSheet_otherButtonMiddleBackground);
                a.recycle();
            }
            return otherButtonMiddleBackground;
        }
    }

    public static interface  MenuItemClickListener{
        void  onItemClick(int itemPosition);
    }
}
