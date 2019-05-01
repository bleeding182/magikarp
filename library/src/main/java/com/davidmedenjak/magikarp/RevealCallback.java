package com.davidmedenjak.magikarp;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class RevealCallback {

  private final SplashView view;
  private boolean revealed = false;

  @Nullable private Animator splashAnimator;

  RevealCallback(SplashView view) {
    this.view = view;
  }

  private void removeView() {
    ViewGroup parent = (ViewGroup) view.getParent();
    if (parent != null) {
      parent.removeView(view);
    }
  }

  /** Reveal the content animating away the splash screen overlay. */
  public void reveal() {
    if (revealed) {
      return;
    }
    revealed = true;

    final Animator revealAnimator = createRevealAnimator();

    final int animTime = readDefaultAnimationTime();
    revealAnimator.setDuration(animTime);

    revealAnimator.addListener(new RemoveViewListener());

    final Animator animation;
    if (splashAnimator != null) {
      animation = new AnimatorSet();
      ((AnimatorSet) animation).playSequentially(splashAnimator, revealAnimator);
      animation.start();
    } else {
      animation = revealAnimator;
    }

    animation.start();
  }

  /**
   * Add an animation that plays <i>before</i> the reveal animation happens.
   *
   * @param splashAnimator the animator to play
   * @return this RevealCallback
   */
  public RevealCallback withAnimator(Animator splashAnimator) {
    this.splashAnimator = splashAnimator;
    return this;
  }

  @NonNull
  public Drawable getDrawable() {
    return view.getSplashDrawable();
  }

  private int readDefaultAnimationTime() {
    final Resources resources = view.getResources();
    return resources.getInteger(android.R.integer.config_mediumAnimTime);
  }

  private Animator createRevealAnimator() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      final int width = view.getWidth();
      final int height = view.getHeight();
      final int centerX = width / 2;
      final int centerY = height / 2;
      final int radius = Math.max(width, height);
      return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, radius, 0F);
    } else {
      final Animator animator = ValueAnimator.ofFloat(1F, 0F);
      ((ValueAnimator) animator)
          .addUpdateListener(it -> view.setAlpha((Float) it.getAnimatedValue()));
      return animator;
    }
  }

  private class RemoveViewListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationEnd(Animator animation) {
      removeView();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
      removeView();
    }

    @Override
    public void onAnimationStart(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}
  }
}
