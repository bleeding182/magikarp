package com.davidmedenjak.magikarp;

import android.app.Activity;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

/**
 * Offers methods to add splash screens to your application.
 *
 * <p>There exist two kinds of splash screens:
 *
 * <p>1. The value of {@code windowBackground} from your activity's theme, which will get shown
 * while the activity is loading, before the first frame of your content draws. {@link
 * #addSplashScreen(Application)} allows you to add a lifecycle callback that switches the splash
 * theme to the actual theme used before your Activity starts.
 *
 * <p>2. Any overlay that you might add to your content to show the splash screen for a longer
 * duration, e.g. while loading or initializing your content. {@link #splash(Activity, int)} allows
 * you to add an overlay that you can animate and finally {@link RevealCallback#reveal()} once
 * you're ready to show your content.
 *
 * <p>Those two approaches can be used together, where the {@code windowBackground} will switch
 * seamlessly to the overlay used, as long as the drawables used line up.
 */
public final class Magikarp {

  /**
   * Register a LifecycleCallback that switches the window background for all activities. Reads the
   * meta-inf and switches the theme if {@code "theme"} is set.
   *
   * @param application the application
   */
  public static void addSplashScreen(@NonNull Application application) {
    addSplashScreen(application, 0);
  }

  /**
   * Register a LifecycleCallback that switches the window background for all activities. Reads the
   * meta-inf and switches the theme if {@code "theme"} is set, otherwise uses the {@code
   * defaultAppTheme}.
   *
   * @param application the application
   * @param defaultAppTheme the default theme to use if no meta-inf attribute is set
   */
  public static void addSplashScreen(
      @NonNull Application application, @StyleRes int defaultAppTheme) {
    application.registerActivityLifecycleCallbacks(
        new ThemeLifecycleCallback(application, defaultAppTheme));
  }

  /**
   * Add a splash screen overlay to the activity. This will be displayed on top of your content
   * until you call {@link RevealCallback#reveal()} on the callback returned.
   *
   * @param activity the activity that should get a splash screen
   * @param splashDrawable the drawable to use for the overlay&mdash;this should be the same
   *     drawable as the {@code windowBackground} of your splash theme
   * @return the callback to reveal the content once you are done with the initialization
   */
  public static RevealCallback splash(
      final Activity activity, final @DrawableRes int splashDrawable) {

    final SplashView view = new SplashView(activity);
    view.setSplashDrawable(splashDrawable);

    return createRevealCallback(activity, view);
  }

  /**
   * Add a splash screen overlay to the activity. This will be displayed on top of your content
   * until you call {@link RevealCallback#reveal()} on the callback returned.
   *
   * @param activity the activity that should get a splash screen
   * @param splashDrawable the drawable to use for the overlay&mdash;this should be the same
   *     drawable as the {@code windowBackground} of your splash theme
   * @return the callback to reveal the content once you are done with the initialization
   */
  public static RevealCallback splash(
      final Activity activity, final @NonNull Drawable splashDrawable) {

    final SplashView view = new SplashView(activity);
    view.setSplashDrawable(splashDrawable);

    return createRevealCallback(activity, view);
  }

  private static RevealCallback createRevealCallback(Activity activity, SplashView view) {
    int matchParent = ViewGroup.LayoutParams.MATCH_PARENT;
    final ViewGroup.LayoutParams layoutParams =
        new ViewGroup.LayoutParams(matchParent, matchParent);

    final Window window = activity.getWindow();
    window.addContentView(view, layoutParams);

    return new RevealCallback(view);
  }
}
