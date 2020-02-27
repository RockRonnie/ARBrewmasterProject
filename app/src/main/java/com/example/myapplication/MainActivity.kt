package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import MapViewFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    private lateinit var mapViewFragment: MapViewFragment
    private lateinit var myManager: FragmentManager
    lateinit var myTransaction: FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportFragmentManager.beginTransaction().add(R.id.mainActivity, MapViewFragment(this)).commit()
        mapViewFragment = MapViewFragment.newInstance()
        if (savedInstanceState == null) {
            myManager = supportFragmentManager
            myTransaction = myManager.beginTransaction()
            myTransaction.add(R.id.root_frame, mapViewFragment)
            myTransaction.commit()
        }
    }
}
