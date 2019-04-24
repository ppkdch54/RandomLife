package com.junX2.kotlintest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_random_walker.*
import kotlin.random.Random

class RandomWalker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //无title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //全屏
        getWindow().setFlags(
            WindowManager.LayoutParams. FLAG_FULLSCREEN ,
            WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_random_walker)
    }
    var rnd=Random(100)
    fun onWalkArrowClick(view: View){
        var direc = rnd.nextInt(4)
        when (direc){
            1->{
                imageView.rotation=90f
            }
            2->{
                imageView.rotation=-90f
            }
            3->{
                imageView.rotation=0f
            }
        }

    }
}
