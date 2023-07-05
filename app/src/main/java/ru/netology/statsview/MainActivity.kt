package ru.netology.statsview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.netology.statsview.ui.StatsView

class MainActivity : AppCompatActivity() {

    private lateinit var statsView: StatsView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statsView = findViewById<StatsView>(R.id.statsView)
        statsView.data = listOf(
            500F,
            500F,
            500F,
            500F,
        )
        statsView.maxData = 3000F
    }
}