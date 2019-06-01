# Magikarp

_**Magikarp** used splash&mdash;and something happened!_

A small library that has your splash screen needs covered. Check out the
demo app which showcases the the available options! You can also read an
[introductory article](https://blog.davidmedenjak.com/android/2019/05/17/animated-splash-screens.html)
on my blog.

## Why?

As the name might suggest this library is intended to help you with your
_splash screens_. On Android we can differentiate between two kinds of
splash screen (not counting really bad ideas like splash Actvities):

1. The splash screen during app startup time (`windowBackground` of the
   activity's theme) which will be visible _until the first frame of the
   content view gets drawn_
2. View overlays while the content is loading

We can't fully avoid **1.** as our app startup time will get slower the
bigger the project becomes, so this is a great spot to show our splash
screen with _minimal_ impact for the user&mdash;otherwise they'd just be
looking at a white screen instead.

Approach **2.** can be used if we _know_ that we need to do some
unsightly (e.g. getting the user position, moving the map, and waiting
for the tiles to finish loading). We can also use this to have a smooth
animation between our splash screen and our content.

While we could still use the same approach to _show a splash screen for
5s_ this is **not recommended** since users usually want to _use_ your
app and not stare at a splash screen&mdash;even though some designers
might think of it backwards

## Features

This library offers help with both approaches mentioned:

* A basic lifecycle callback that can swap themes for easy integration
  of a splash theme in your app
* A view overlay to animate your splash screen (while your content is
  loading)

### Splash Theme

To use a splash theme start by creating the theme. It is a good idea to
use the same flags that your theme will use (`fullscreen`, etc) but the
important bit is to specify the splash drawable as
`android:windowBackground`

```xml
<style name="AppTheme.Splash">
    <item name="android:windowBackground">@drawable/splash_screen</item>
</style>
```

A simple splash screen will usually consist of your primary color with
the app icon centered in it:

```xml
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item>
        <color android:color="@color/colorPrimary"/>
    </item>

    <item android:id="@+id/icon">
        <bitmap
            android:gravity="center"
            android:src="@mipmap/ic_launcher_foreground"/>
    </item>
</layer-list>
```

In your Application initialize Magikarp with the default theme of your
app:

```java
public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    
    // ...

    Magikarp.addSplashScreen(this, R.style.AppTheme);
  }
}
```

Finally, register your Application and splash theme in the manifest, and
keep in mind that you should not set any theme on your Activities
directly, or it will use the window background from _that_ theme
instead.

```xml
<application
    android:name=".App"
    android:theme="@style/AppTheme.Splash"/>
```

That's it! If an Activity needs a different theme you can set declare it
by setting `"theme"` as `<meta-inf />` on the Activity.


```xml
<activity>
    <meta-data
        android:name="theme"
        android:resource="@style/ThemeOverlay.AppCompat.Dark"/>
</activity>
```

### Animated Splash Screen

Magikarp offers a very simple reveal animation to keep showing a splash
screen while your content loads. Pass in the same drawable your splash
screen uses and call `.reveal()` to animate the splash screen away once
you're ready.

```java
RevealCallback callback = Magikarp.splash(this, R.drawable.splash_screen);
// ... do some loading ...
callback.reveal();
```

You can also create more complex animations with the drawable by using
`callback.getDrawable()`.

```java
private Animator createSplashAnimation(RevealCallback callback) {
  final Drawable drawable = ((LayerDrawable) callback.getDrawable()).findDrawableByLayerId(R.id.icon);
  final float dp100 = 200 // 100 "dp"
  
  final ValueAnimator splashAnimator = ValueAnimator.ofInt(0, (int) dp100, 0);
  splashAnimator.setDuration(450);
  splashAnimator.setInterpolator(new AnticipateOvershootInterpolator());
  splashAnimator.setRepeatMode(ValueAnimator.RESTART);
  splashAnimator.setStartDelay(100);
  splashAnimator.setRepeatCount(1);

  final Rect drawableBounds = drawable.copyBounds();
  final int top = drawableBounds.top;
  final int left = drawableBounds.left;
  splashAnimator.addUpdateListener(
      animation -> {
        drawable.copyBounds(drawableBounds);
        int value = (int) animation.getAnimatedValue();
        drawableBounds.offsetTo(left, top - value);
        drawable.setBounds(drawableBounds);
      });

  final AnimatorSet animatorSet = new AnimatorSet();
  animatorSet.playSequentially(splashAnimator, callback.createRevealAnimator());
  return animatorSet;
}
```

Then use `callback.reveal(animator)` to play your custom animation
instead!

## Usage

You can try it out using JitPack by adding the following to your `build.gradle` file:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.bleeding182:magikarp:-SNAPSHOT'
}
```

## Feature Backlog

This library should remain simple, as such the library should handle the
two use cases mentioned and it should handle them well.

For now there is only support to animate the drawable as shown in the
example to prevent splash screens with more complexity than the rest of
the app, but view animation support could be added if the need arises.

## Contributing

This library will keep a `0.*` version until I can get some feedback
about how you are using it along with any issues. As such, please feel
free to add suggestions or feedback.

## License

This code is published under MIT, so please feel free to use what you
need.
