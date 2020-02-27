package com.example.myapplication

import ArViewFragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import MapViewFragment
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class MainActivity : AppCompatActivity() {
    private lateinit var mapViewFragment: MapViewFragment
    private lateinit var arViewFragment: ArViewFragment
    private lateinit var myManager: FragmentManager
    lateinit var myTransaction: FragmentTransaction
    lateinit var toggle: Button
    private var state: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggle = findViewById<Button>(R.id.toggleButton)
        toggle.setOnClickListener {
            changeFragment(state)
        }
        if (savedInstanceState == null) {
           changeFragment(state)
        }
    }
    private fun toggleState(newState: String){
        state = newState
        toggle.text = newState
    }
    private fun changeFragment(myState: String){
        when(myState){
            "map" -> {
                toggleState("ar")
                mapViewFragment = MapViewFragment.newInstance()
                myManager = supportFragmentManager
                myTransaction = myManager.beginTransaction()
                myTransaction.add(R.id.root_frame, mapViewFragment)
                myTransaction.commit()
            }
            "ar" -> {
                toggleState("map")
                arViewFragment = ArViewFragment.newInstance()
                myManager = supportFragmentManager
                myTransaction = myManager.beginTransaction()
                myTransaction.add(R.id.root_frame, arViewFragment)
                myTransaction.commit()
            }
        }
    }
}
