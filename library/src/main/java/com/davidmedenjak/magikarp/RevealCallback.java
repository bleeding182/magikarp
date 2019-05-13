package com.davidmedenjak.magikarp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

@MainThread
public final class RevealCallback {

  private final SplashView view;
  private boolean revealed = false;

  RevealCallback(@NonNull SplashView view) {
    this.view = view;
  }

  /** Reveal the content animating away the splash screen overlay. */
  public void reveal() {
    if (revealed) {
      return;
    }

    final Animator revealAnimator = createRevealAnimator();

    final int animTime = readDefaultAnimationTime();
    revealAnimator.setDuration(animTime);

    reveal(revealAnimator);
  }

  /**
   * Reveal the content animating away the splash screen overlay using the {@code revealAnimation}.
   */
  public void reveal(@NonNull final Animator revealAnimation) {
    if (revealed) {
      return;
    }
    revealed = true;

    revealAnimation.addListener(new RemoveViewListener(view));
    revealAnimation.start();
  }

  @NonNull
  public Drawable getDrawable() {
    return view.getSplashDrawable();
  }

  private int readDefaultAnimationTime() {
    final Resources resources = view.getResources();
    return resources.getInteger(android.R.integer.config_mediumAnimTime);
  }

  /**
   * Create the default reveal animation to use with {@link #reveal(Animator)}. This is a helper
   * method in case you want to chain multiple animations, for the default animation you can use
   * {@link #reveal()}.
   */
  public Animator createRevealAnimator() {
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

  /** Removes the view from parent when the animation ends or gets cancelled. */
  private static class RemoveViewListener implements Animator.AnimatorListener {

    private final View view;

    private RemoveViewListener(View view) {
      this.view = view;
    }

    private void removeView() {
      ViewGroup parent = (ViewGroup) view.getParent();
      if (parent != null) {
        parent.removeView(view);
      }
    }

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
