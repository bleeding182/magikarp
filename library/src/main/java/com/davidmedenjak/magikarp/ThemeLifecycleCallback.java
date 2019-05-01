package com.davidmedenjak.magikarp;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.StyleRes;

public abstract class ThemeLifecycleCallback implements Application.ActivityLifecycleCallbacks {

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    final int theme = getTheme(activity);

    if (theme != 0) {
      activity.setTheme(theme);
    }
  }

  @StyleRes
  public abstract int getTheme(Activity activity);

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
