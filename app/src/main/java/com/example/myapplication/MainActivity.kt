package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import MapViewFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().add(R.id.mainActivity, MapViewFragment(this)).commit()
    }
}
