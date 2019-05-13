package com.davidmedenjak.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

/** Activity to launch and showcase the different modes of splash screens available. */
public class DemoActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_demo);

    findViewById(R.id.action_no_splash)
        .setOnClickListener(v -> startActivity(new Intent(this, NoSplashActivity.class)));
    findViewById(R.id.action_background_splash)
        .setOnClickListener(v -> startActivity(SplashActivity.newIntent(this, false, false)));
    findViewById(R.id.action_magikarp)
        .setOnClickListener(v -> startActivity(SplashActivity.newIntent(this, true, false)));
    findViewById(R.id.action_magikarp_animated)
        .setOnClickListener(v -> startActivity(SplashActivity.newIntent(this, true, true)));
  }
}
