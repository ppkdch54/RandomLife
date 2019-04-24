package com.junX2.kotlintest

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_random_dice.*

class RandomDice : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_random_dice)
    }

    fun onCreateDiceClick(view: View) {
        var intent= Intent(this, ShowDice::class.java)
        intent.putExtra("txt",this.editText.text.toString())
        intent.putExtra("txt1",this.editText1.text.toString())
        intent.putExtra("txt2",this.editText2.text.toString())
        intent.putExtra("txt3",this.editText3.text.toString())

        startService(intent)
        moveTaskToBack(true)
    }
}
