package com.davidmedenjak.sample;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import com.davidmedenjak.magikarp.Magikarp;
import com.davidmedenjak.magikarp.RevealCallback;

import java.util.concurrent.TimeUnit;

import androidx.fragment.app.FragmentActivity;

/** Demo Activity showcasing the different modes usable with Magikarp. */
public class SplashActivity extends FragmentActivity {

  public static final String EXTRA_MAGIKARP = "magikarp";
  public static final String EXTRA_ANIMATED = "animated";

  /**
   * Simulate the time it takes for the app to "load" until the first frame will be drawn. This is
   * the time where we would usually see a plain {@code windowBackground} during app start.
   */
  private static final long APP_LOAD_TIME = TimeUnit.SECONDS.toMillis(1);

  /**
   * Simulate the time it takes to load content. This would usually be the time until we finished
   * reading the cache or querying the API
   */
  private static final long CONTENT_LOAD_TIME = TimeUnit.MILLISECONDS.toMillis(500);

  public static Intent newIntent(Context context, boolean magikarp, boolean animated) {
    return new Intent(context, SplashActivity.class)
        .putExtra(EXTRA_MAGIKARP, magikarp)
        .putExtra(EXTRA_ANIMATED, animated);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      // simulate slow app start
      Thread.sleep(APP_LOAD_TIME);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    setContentView(R.layout.activity_lorem_loading);

    // initialize demo stuff
    final boolean magikarp = getIntent().getBooleanExtra(EXTRA_MAGIKARP, false);
    final Runnable splash = loadSplashScreen(magikarp);

    // simulate content loading
    new Handler().postDelayed(() -> showContent(splash), CONTENT_LOAD_TIME);
  }

  /** We finished "loading", now display the "content" ;) */
  private void showContent(Runnable splash) {
    findViewById(android.R.id.progress).setVisibility(View.GONE);
    findViewById(R.id.content).setVisibility(View.VISIBLE);
    splash.run();
  }

  // region << Demo helper methods >>
  /**
   * Return the splash screen or a dummy Runnable if we don't use one. This is just to make the demo
   * work.
   */
  private Runnable loadSplashScreen(boolean magikarp) {
    if (magikarp) {
      boolean animated = getIntent().getBooleanExtra(EXTRA_ANIMATED, false);
      final RevealCallback callback = createSplashAnimation(animated);
      return callback::reveal;
    } else {
      return () -> {};
    }
  }

  private RevealCallback createSplashAnimation(boolean animate) {
    final RevealCallback callback = Magikarp.splash(this, R.drawable.splash_screen);
    if (animate) {
      addAnimation(callback);
    }
    return callback;
  }

  private void addAnimation(RevealCallback callback) {
    Drawable drawable = ((LayerDrawable) callback.getDrawable()).findDrawableByLayerId(R.id.icon);
    float dp100 =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());

    ValueAnimator splashAnimator = ValueAnimator.ofInt(0, (int) dp100, 0);
    splashAnimator.setDuration(450);
    splashAnimator.setInterpolator(new AnticipateOvershootInterpolator());
    splashAnimator.setRepeatMode(ValueAnimator.RESTART);
    splashAnimator.setStartDelay(100);
    splashAnimator.setRepeatCount(1);

    int top = drawable.copyBounds().top;
    int left = drawable.copyBounds().left;
    splashAnimator.addUpdateListener(
        animation -> {
          Rect bounds = drawable.copyBounds();
          int value = (int) animation.getAnimatedValue();
          bounds.offsetTo(left, top - value);
          drawable.setBounds(bounds);
        });
    callback.withAnimator(splashAnimator);
  }
  // endregion
}
