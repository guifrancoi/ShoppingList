package com.example.shoppinglist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // layout activity_main.xml com FrameLayout id fragment_container
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListsFragment())
                .commit()
        }
    }

    fun openListDetail(listId: Long) {
        val frag = ListDetailFragment.newInstance(listId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, frag)
            .addToBackStack(null)
            .commit()
    }
}
