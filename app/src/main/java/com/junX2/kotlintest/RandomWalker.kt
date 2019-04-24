package com.junX2.kotlintest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_random_walker.*
import kotlin.random.Random

class RandomWalker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
