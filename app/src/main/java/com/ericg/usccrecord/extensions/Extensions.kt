package com.ericg.usccrecord.extensions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

/**
 * @author eric
 * @date 9/14/20
 */
object Extensions {

    fun Activity.toast(text: String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun View.snackBuilder(text: String = "", duration:Int = 3000): Snackbar {
        return Snackbar.make(this, text, duration)
    }

    fun Activity.sendEmail(subject: String?, msg: String?, to:Array<String>){
        val emailIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
        emailIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, msg)
            putExtra(Intent.EXTRA_EMAIL, to)
        }
        startActivity(Intent.createChooser(emailIntent, "Select Email client [for USCC Help desk]"))
    }
}