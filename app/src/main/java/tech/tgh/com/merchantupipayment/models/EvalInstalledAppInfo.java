package tech.tgh.com.merchantupipayment.models;

import android.graphics.drawable.Drawable;

public class EvalInstalledAppInfo {

    private String ClassName;
    private String SimpleName;
    private String PackageName;
    private Drawable appIcon;

    public EvalInstalledAppInfo(Drawable appIcon,String className, String packageName, String simpleName) {
        ClassName = className;
        SimpleName = simpleName;
        PackageName = packageName;
        this.appIcon = appIcon;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getClassName() {
        return ClassName;
    }

    public void setClassName(String className) {
        ClassName = className;
    }

    public String getSimpleName() {
        return SimpleName;
    }

    public void setSimpleName(String simpleName) {
        SimpleName = simpleName;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }
}
