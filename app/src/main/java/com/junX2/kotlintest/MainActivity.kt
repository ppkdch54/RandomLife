package com.junX2.kotlintest

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.annotation.RequiresApi
import kotlinx.android.synthetic.main.content_main.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import android.content.pm.ApplicationInfo
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAdCallback

class MainActivity : AppCompatActivity() {
    private var btnType: BtnType = BtnType.Walker
    private lateinit var rewardedAd: RewardedAd

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        init()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun init(){
        MobileAds.initialize(this, "ca-app-pub-9256036974908996~9819080620");
        rewardedAd = createAndLoadRewardedAd()

        btnDice.isClickable = false
        btnWalk.isClickable=false
        if (!Settings.canDrawOverlays(this@MainActivity)) {
            getOverlayPermission()
        }
    }

    fun createAndLoadRewardedAd(): RewardedAd {
        val rewardedAd = RewardedAd(
            this,
            when(isApkInDebug(this)){
                true->{
                     "ca-app-pub-3940256099942544/5224354917"
                }
                false->{
                    "ca-app-pub-9256036974908996/8065524560"
                }
            }

        )
        val adLoadCallback = object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                // Ad successfully loaded.
                onAdLoad()
            }

            override fun onRewardedAdFailedToLoad(errorCode: Int) {
                // Ad failed to load.
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
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

    private fun getOverlayPermission() {
        var intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }

    enum class BtnType {
        Dice,
        Walker
    }
    fun onRandomDiceClick(view: View) {
        btnType = BtnType.Dice
        showAd()
    }
    fun onRandomWalkClick(view: View) {
        btnType = BtnType.Walker
        showAd()
    }
    fun onAdClose(){
        when (btnType) {
            BtnType.Dice -> {
                startActivity(Intent(this@MainActivity,RandomDice::class.java))
            }
            BtnType.Walker -> {
                startActivity(Intent(this@MainActivity, RandomWalker::class.java))
            }
        }
    }
    fun onAdLoad(){
        btnDice.isClickable = true
        btnWalk.isClickable=true
    }
    private fun showAd(){
        val adCallback = object : RewardedAdCallback() {
            override fun onRewardedAdOpened() {
                // Ad opened.
            }

            override fun onRewardedAdClosed() {
                // Ad closed.
                onAdClose()
                rewardedAd = createAndLoadRewardedAd()
            }

            fun onUserEarnedReward(reward: RewardItem) {
                // User earned reward.
            }

            override fun onRewardedAdFailedToShow(errorCode: Int) {
                // Ad failed to display
            }
        }
        rewardedAd.show(this, adCallback)

    }
    /**
     * 判断当前应用是否是debug状态
     */
    private fun isApkInDebug(context: Context): Boolean {
        try {
            return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
            return false
        }

    }
}
