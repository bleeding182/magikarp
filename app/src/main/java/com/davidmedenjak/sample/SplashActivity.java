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

import androidx.fragment.app.FragmentActivity;

/** Demo Activity showcasing the different modes usable with Magikarp. */
public class SplashActivity extends FragmentActivity {

  public static final String EXTRA_MAGIKARP = "magikarp";
  public static final String EXTRA_ANIMATED = "animated";

  public static Intent newIntent(Context context, boolean magikarp, boolean animated) {
    return new Intent(context, SplashActivity.class)
        .putExtra(EXTRA_MAGIKARP, magikarp)
        .putExtra(EXTRA_ANIMATED, animated);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    boolean magikarp = getIntent().getBooleanExtra(EXTRA_MAGIKARP, false);

    setContentView(R.layout.activity_lorem_loading);

    final Runnable splash;
    if (magikarp) {
      boolean animated = getIntent().getBooleanExtra(EXTRA_ANIMATED, false);
      final RevealCallback callback = createSplashAnimation(animated);
      splash = callback::reveal;
    } else {
      splash = () -> {};
    }
    new Handler()
        .postDelayed(
            () -> {
              findViewById(android.R.id.progress).setVisibility(View.GONE);
              findViewById(R.id.content).setVisibility(View.VISIBLE);
              splash.run();
            },
            1000);
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
}
