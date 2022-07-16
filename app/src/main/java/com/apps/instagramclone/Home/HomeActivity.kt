package com.apps.instagramclone.Home

import android.content.Intent
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.nostra13.universalimageloader.core.ImageLoader

import com.apps.instagramclone.Login.LoginActivity
import com.apps.instagramclone.R
import com.apps.instagramclone.Utils.BottomNavigationViewHelper
import com.apps.instagramclone.Utils.FirebaseMethods
import com.apps.instagramclone.Utils.MainFeedListAdapter
import com.apps.instagramclone.Utils.SectionsPagerAdapter
import com.apps.instagramclone.Utils.UniversalImageLoader
import com.apps.instagramclone.Utils.ViewCommentsFragment
import com.apps.instagramclone.models.Photo
import com.apps.instagramclone.opengl.AddToStoryDialog
import com.apps.instagramclone.opengl.NewStoryActivity

class HomeActivity : AppCompatActivity(), MainFeedListAdapter.OnLoadMoreItemsListener {

    private val mContext = this@HomeActivity

    //firebase
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    //widgets
    private var mViewPager: ViewPager? = null
    private var mFrameLayout: FrameLayout? = null
    private var mRelativeLayout: RelativeLayout? = null

    override fun onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos")
        val fragment = supportFragmentManager
                .findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + mViewPager!!.currentItem) as HomeFragment?
        fragment?.displayMorePhotos()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        Log.d(TAG, "onCreate: starting.")
        mViewPager = findViewById<View>(R.id.viewpager_container) as ViewPager
        mFrameLayout = findViewById<View>(R.id.container) as FrameLayout
        mRelativeLayout = findViewById<View>(R.id.relLayoutParent) as RelativeLayout

        setupFirebaseAuth()

        initImageLoader()
        setupBottomNavigationView()
        setupViewPager()

    }

    fun openNewStoryActivity() {
        val intent = Intent(this, NewStoryActivity::class.java)
        startActivityForResult(intent, REQUEST_ADD_NEW_STORY)
    }

    fun showAddToStoryDialog() {
        Log.d(TAG, "showAddToStoryDialog: showing add to story dialog.")
        val dialog = AddToStoryDialog()
        dialog.show(fragmentManager, getString(R.string.dialog_add_to_story))
    }


    fun onCommentThreadSelected(photo: Photo, callingActivity: String) {
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread")

        val fragment = ViewCommentsFragment()
        val args = Bundle()
        args.putParcelable(getString(R.string.photo), photo)
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity))
        fragment.arguments = args

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(getString(R.string.view_comments_fragment))
        transaction.commit()

    }

    fun hideLayout() {
        Log.d(TAG, "hideLayout: hiding layout")
        mRelativeLayout!!.visibility = View.GONE
        mFrameLayout!!.visibility = View.VISIBLE
    }


    fun showLayout() {
        Log.d(TAG, "hideLayout: showing layout")
        mRelativeLayout!!.visibility = View.VISIBLE
        mFrameLayout!!.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mFrameLayout!!.visibility == View.VISIBLE) {
            showLayout()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: incoming result.")
        // Received recording or error from MaterialCamera

        if (requestCode == REQUEST_ADD_NEW_STORY) {
            Log.d(TAG, "onActivityResult: incoming new story.")
            if (resultCode == RESULT_ADD_NEW_STORY) {
                Log.d(TAG, "onActivityResult: got the new story.")
                Log.d(TAG, "onActivityResult: data type: " + data!!.type!!)

                val fragment = supportFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewpager_container + ":" + 1) as HomeFragment?
                if (fragment != null) {

                    val firebaseMethods = FirebaseMethods(this)
                    firebaseMethods.uploadNewStory(data, fragment)

                } else {
                    Log.d(TAG, "onActivityResult: could not communicate with home fragment.")
                }


            }
        }
    }


    private fun initImageLoader() {
        val universalImageLoader = UniversalImageLoader(mContext)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }

    /**
     * Responsible for adding the 3 tabs: Camera, Home, Messages
     */
    private fun setupViewPager() {
        val adapter = SectionsPagerAdapter(supportFragmentManager)
        adapter.addFragment(CameraFragment()) //index 0
        adapter.addFragment(HomeFragment()) //index 1
        adapter.addFragment(MessagesFragment()) //index 2
        mViewPager!!.adapter = adapter

        val tabLayout = findViewById<View>(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)

        tabLayout.getTabAt(0)!!.setIcon(R.drawable.ic_camera)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.ic_instagram_black)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.ic_arrow)
    }

    /**
     * BottomNavigationView setup
     */
    private fun setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView")
        val bottomNavigationViewEx = findViewById<BottomNavigationViewEx>(R.id.bottomNavViewBar)
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx)
        BottomNavigationViewHelper.enableNavigation(mContext, this, bottomNavigationViewEx)
        val menu = bottomNavigationViewEx.menu
        val menuItem = menu.getItem(ACTIVITY_NUM)
        menuItem.isChecked = true
    }


    /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private fun checkCurrentUser(user: FirebaseUser?) {
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.")

        if (user == null) {
            val intent = Intent(mContext, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Setup the firebase auth object
     */
    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.")

        mAuth = FirebaseAuth.getInstance()

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser

            //check if the user is logged in
            checkCurrentUser(user)

            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out")
            }
            // ...
        }
    }

    public override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)
        mViewPager!!.currentItem = HOME_FRAGMENT
        checkCurrentUser(mAuth!!.currentUser)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            mAuth!!.removeAuthStateListener(mAuthListener!!)
        }
    }

    companion object {

        private val TAG = "HomeActivity"
        private val ACTIVITY_NUM = 0
        private val HOME_FRAGMENT = 1
        private val RESULT_ADD_NEW_STORY = 7891
        private val CAMERA_RQ = 6969
        private val REQUEST_ADD_NEW_STORY = 8719
    }


}
