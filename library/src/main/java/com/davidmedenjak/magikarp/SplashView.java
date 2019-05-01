package com.davidmedenjak.magikarp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * SplashView that aligns {@code splashDrawable} with the window background to make fluid animations
 * possible.
 *
 * <p>This class should not be used directly. Use {@link Magikarp#splash(Activity, int)} to add a
 * splash screen to your activity.
 */
public class SplashView extends View {

  private Drawable splashDrawable;

  private WindowManager windowManager;

  public SplashView(@NonNull Context context) {
    super(context);
    init();
  }

  private static int getSize(Resources resources, String resId) {
    final int navigationBarSize;
    int resourceId = resources.getIdentifier(resId, "dimen", "android");
    if (resourceId > 0) {
      navigationBarSize = resources.getDimensionPixelSize(resourceId);
    } else {
      navigationBarSize = 0;
    }
    return navigationBarSize;
  }

  private void init() {
    windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
  }

  @NonNull
  public Drawable getSplashDrawable() {
    return splashDrawable;
  }

  void setSplashDrawable(@DrawableRes int drawable) {
    //noinspection ConstantConditions
    setSplashDrawable(ContextCompat.getDrawable(getContext(), drawable));
  }

  void setSplashDrawable(@NonNull Drawable drawable) {
    this.splashDrawable = drawable;
    splashDrawable.setCallback(this);
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    super.onLayout(changed, left, top, right, bottom);
    readWindowBounds();
  }

  /**
   * The window background draws beneath the statusbar and navigationbar. To align the splash
   * drawable with the background we need to overdraw as well.
   */
  private void readWindowBounds() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      final WindowInsets windowInsets = getRootWindowInsets();
      splashDrawable.setBounds(
          -windowInsets.getSystemWindowInsetLeft(),
          -windowInsets.getSystemWindowInsetTop(),
          getWidth() + windowInsets.getSystemWindowInsetRight(),
          getHeight() + windowInsets.getSystemWindowInsetBottom());
    } else {
      final Display defaultDisplay = windowManager.getDefaultDisplay();
      final int rotation = defaultDisplay.getRotation();
      int statusBarSize = getStatusBarSize();
      int navigationBarSize = getNavigationBarSize();
      switch (rotation) {
        case Surface.ROTATION_180:
          // todo Will this align?
        case Surface.ROTATION_0:
          splashDrawable.setBounds(0, -statusBarSize, getWidth(), getHeight() + navigationBarSize);
          break;
        case Surface.ROTATION_90:
          splashDrawable.setBounds(0, -statusBarSize, getWidth() + navigationBarSize, getHeight());
          break;
        case Surface.ROTATION_270:
          splashDrawable.setBounds(-navigationBarSize, -statusBarSize, getWidth(), getHeight());
          break;
      }
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    splashDrawable.draw(canvas);
  }

  @Override
  protected boolean verifyDrawable(@NonNull Drawable who) {
    return who == splashDrawable || super.verifyDrawable(who);
  }

  private int getNavigationBarSize() {
    return getSize(getResources(), "navigation_bar_height");
  }

  private int getStatusBarSize() {
    return getSize(getResources(), "status_bar_height");
  }
}
