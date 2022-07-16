package com.apps.instagramclone.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

import com.apps.instagramclone.Home.HomeActivity
import com.apps.instagramclone.Likes.LikesActivity
import com.apps.instagramclone.Profile.ProfileActivity
import com.apps.instagramclone.R
import com.apps.instagramclone.Search.SearchActivity
import com.apps.instagramclone.Share.ShareActivity

/**
 * Created by User on 5/28/2017.
 */

object BottomNavigationViewHelper {

    private val TAG = "BottomNavigationViewHel"

    fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView")
        /*
        bottomNavigationViewEx.enableAnimation(false)
        bottomNavigationViewEx.enableItemShiftingMode(false)
        bottomNavigationViewEx.enableShiftingMode(false)
        bottomNavigationViewEx.setTextVisibility(false)
        */

    }

    fun enableNavigation(context: Context, callingActivity: Activity, view: BottomNavigationViewEx) {
        view.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.ic_house -> {
                    val intent1 = Intent(context, HomeActivity::class.java)//ACTIVITY_NUM = 0
                    context.startActivity(intent1)
                    callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.ic_search -> {
                    val intent2 = Intent(context, SearchActivity::class.java)//ACTIVITY_NUM = 1
                    context.startActivity(intent2)
                    callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.ic_circle -> {
                    val intent3 = Intent(context, ShareActivity::class.java)//ACTIVITY_NUM = 2
                    context.startActivity(intent3)
                    callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.ic_alert -> {
                    val intent4 = Intent(context, LikesActivity::class.java)//ACTIVITY_NUM = 3
                    context.startActivity(intent4)
                    callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }

                R.id.ic_android -> {
                    val intent5 = Intent(context, ProfileActivity::class.java)//ACTIVITY_NUM = 4
                    context.startActivity(intent5)
                    callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
            }


            false
        }
    }
}
