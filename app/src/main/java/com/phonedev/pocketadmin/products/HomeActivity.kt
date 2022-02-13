package com.phonedev.pocketadmin.products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.phonedev.pocketadmin.R
import com.phonedev.pocketadmin.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setClick()
    }

    private fun setClick() {
        binding.btnAcc.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
        binding.btnArt.setOnClickListener {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}