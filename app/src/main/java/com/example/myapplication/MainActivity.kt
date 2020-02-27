package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import MapViewFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.mainActivity, MapViewFragment(this)).commit()
    }
}
