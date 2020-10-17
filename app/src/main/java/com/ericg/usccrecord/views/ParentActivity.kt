/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.views

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.ericg.usccrecord.R
import com.ericg.usccrecord.extensions.Extensions.toast

/**
 * @author eric
 * @date 9/14/20
 */
class ParentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parent)
/*        this holds the opening navigation view   */
    }

    var goBack = false
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (goBack) {
            super.onBackPressed()
        } else {
            goBack = true
            toast("press again to exit")
            Handler().postDelayed({
                goBack = false
            }, 2000)
        }
    }
}