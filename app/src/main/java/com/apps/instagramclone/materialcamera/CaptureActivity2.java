package com.apps.instagramclone.materialcamera;

import android.app.Fragment;
import androidx.annotation.NonNull;

import com.apps.instagramclone.materialcamera.internal.BaseCaptureActivity;
import com.apps.instagramclone.materialcamera.internal.Camera2Fragment;


public class CaptureActivity2 extends BaseCaptureActivity {

  @Override
  @NonNull
  public Fragment getFragment() {
    return Camera2Fragment.newInstance();
  }
}
