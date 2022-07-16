package com.apps.instagramclone.materialcamera;

import android.app.Fragment;
import androidx.annotation.NonNull;

import com.apps.instagramclone.materialcamera.internal.BaseCaptureActivity;
import com.apps.instagramclone.materialcamera.internal.CameraFragment;

public class CaptureActivity extends BaseCaptureActivity {

  @Override
  @NonNull
  public Fragment getFragment() {
    return CameraFragment.newInstance();
  }
}
