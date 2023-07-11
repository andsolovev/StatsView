package ru.netology.statsview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {

    private lateinit var statsView: StatsView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statsView = findViewById(R.id.statsView)

        val data: List<Float> = listOf(
            500F,
            500F,
            500F,
            500F
        )

        statsView.maxData = 3000F

        val button0: Button = findViewById(R.id.button0)
        val button1: Button = findViewById(R.id.button1)
        val button2: Button = findViewById(R.id.button2)
        val button3: Button = findViewById(R.id.button3)

        button0.setOnClickListener { buttonAction(0, data) }

        button1.setOnClickListener { buttonAction(1, data) }

        button2.setOnClickListener { buttonAction(2, data) }

        button3.setOnClickListener { buttonAction(3, data) }

//        val textView = findViewById<TextView>(R.id.text)

//        statsView.startAnimation(
//            AnimationUtils.loadAnimation(this, R.anim.animation)
//                .apply {
//                setAnimationListener(object: Animation.AnimationListener {
//                    override fun onAnimationStart(p0: Animation?) {
//                        textView.text = "onAnimationStart"
//                    }
//
//                    override fun onAnimationEnd(p0: Animation?) {
//                        textView.text = "onAnimationEnd"
//                    }
//
//                    override fun onAnimationRepeat(p0: Animation?) {
//                        textView.text = "onAnimationRepeat"
//                    }
//
//                })
//            }
//        )

    }

    private fun buttonAction(animationType: Int, data: List<Float>) {
        statsView.animationType = animationType
        statsView.postDelayed({ statsView.data = data}, 1000)
    }

}