package com.davidmedenjak.sample;

import android.app.Application;

import com.davidmedenjak.magikarp.Magikarp;

public class App extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    Magikarp.addSplashScreen(this, R.style.AppTheme);
  }
}
