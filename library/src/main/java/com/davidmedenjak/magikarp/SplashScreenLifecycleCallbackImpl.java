package com.davidmedenjak.magikarp;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.StyleRes;

public class SplashScreenLifecycleCallbackImpl extends ThemeLifecycleCallback {

  private static final String META_THEME = "theme";
  private static final int FLAG_META_DATA = PackageManager.GET_META_DATA;

  private final PackageManager packageManager;
  private final int defaultAppTheme;

  public SplashScreenLifecycleCallbackImpl(Application application, @StyleRes int defaultAppTheme) {
    this.defaultAppTheme = defaultAppTheme;
    packageManager = application.getPackageManager();
  }

  @Override
  public int getTheme(Activity activity) {
    try {
      final ComponentName name = activity.getComponentName();
      final ActivityInfo activityInfo = packageManager.getActivityInfo(name, FLAG_META_DATA);

      final Bundle metaData = activityInfo.metaData;
      if (metaData != null) {
        return metaData.getInt(META_THEME, defaultAppTheme);
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return defaultAppTheme;
  }
}
