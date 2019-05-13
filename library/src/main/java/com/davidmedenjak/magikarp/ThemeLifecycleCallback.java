package com.davidmedenjak.magikarp;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.StyleRes;

/**
 * LifecycleCallback that applies a new theme on activity's {@code onCreate()} to switch to the
 * actual theme after showing a splash screen.
 *
 * <p>This will apply the default theme to all activities which can be overridden by specifying the
 * {@code "theme" } meta-data in your AndroidManifest.xml
 */
public class ThemeLifecycleCallback implements Application.ActivityLifecycleCallbacks {

  private static final String META_THEME = "theme";
  private static final int FLAG_META_DATA = PackageManager.GET_META_DATA;

  private final PackageManager packageManager;
  private final int defaultAppTheme;

  /** @param defaultAppTheme the default theme to use, or {@code 0} for no default. */
  public ThemeLifecycleCallback(Context context, @StyleRes int defaultAppTheme) {
    this.defaultAppTheme = defaultAppTheme;
    packageManager = context.getPackageManager();
  }

  @Override
  public void onActivityCreated(final Activity activity, final Bundle savedInstanceState) {
    final int theme = getTheme(activity);

    if (theme != 0) {
      activity.setTheme(theme);
    }
  }

  @StyleRes
  protected int getTheme(final Activity activity) {
    final Bundle metaData = readActivityMetadata(activity);
    if (metaData != null) {
      return metaData.getInt(META_THEME, defaultAppTheme);
    }
    return defaultAppTheme;
  }

  private Bundle readActivityMetadata(final Activity activity) {
    try {
      final ComponentName name = activity.getComponentName();
      final ActivityInfo activityInfo = packageManager.getActivityInfo(name, FLAG_META_DATA);

      return activityInfo.metaData;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  // region << Unused lifecycle callbacks >>
  @Override
  public void onActivityStarted(Activity activity) {
    // do nothing
  }

  @Override
  public void onActivityResumed(Activity activity) {
    // do nothing
  }

  @Override
  public void onActivityPaused(Activity activity) {
    // do nothing
  }

  @Override
  public void onActivityStopped(Activity activity) {
    // do nothing
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    // do nothing
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    // do nothing
  }
  // endregion
}
