package com.junX2.kotlintest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.widget.Toast
import kotlinx.android.synthetic.main.content_main.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener


class MainActivity : AppCompatActivity(), RewardedVideoAdListener {
    override fun onRewarded(reward: com.google.android.gms.ads.reward.RewardItem?) {
        Toast.makeText(this, "onRewarded! currency: ${reward?.type} amount: ${reward?.amount}",
            Toast.LENGTH_SHORT).show()
        // Reward the user.
    }


    private lateinit var mRewardedVideoAd: RewardedVideoAd

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        MobileAds.initialize(this, "ca-app-pub-9256036974908996~9819080620");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()
        btnCreate.isClickable=false
        btnCreate.setBackgroundColor(Color.RED)
        btnCreate.text="Waiting for ad loading."
        if (!Settings.canDrawOverlays(this@MainActivity)) {
            getOverlayPermission()
        }
    }
    private fun loadRewardedVideoAd() {
        //ca-app-pub-9256036974908996/8065524560
        mRewardedVideoAd.loadAd("ca-app-pub-9256036974908996/8065524560",
            AdRequest.Builder().build())
    }


    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {


        //Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
        var intent=Intent(this@MainActivity, ShowDice::class.java)
        intent.putExtra("txt",this@MainActivity.editText.text.toString())
        intent.putExtra("txt1",this@MainActivity.editText1.text.toString())
        intent.putExtra("txt2",this@MainActivity.editText2.text.toString())
        intent.putExtra("txt3",this@MainActivity.editText3.text.toString())

        startService(intent)
        topScreen=intent
        moveTaskToBack(true)
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        btnCreate.isClickable=true
        btnCreate.setBackgroundColor(Color.GREEN)
        btnCreate.text="Create random"
        //Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        //Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoStarted() {
        //Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        //Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    var topScreen:Intent?=null
    private val MY_PERMISSIONS_SYSTEM_ALERT: Int=1
    fun getOverlayPermission() {
        var intent: Intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }

    var earned:Boolean=false
    @RequiresApi(Build.VERSION_CODES.M)
    fun View.onCreateBtnClick()
    {
        if (mRewardedVideoAd.isLoaded() as Boolean) {
            mRewardedVideoAd.show()
        }

    }

    fun removeExsited() {
        if (topScreen != null) {
            stopService(topScreen)
        }
    }
    fun View.onDeleteBtnClick() {
        removeExsited()
    }
}
