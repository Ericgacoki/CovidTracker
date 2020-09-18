package com.ericg.usccrecord.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.ericg.usccrecord.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.custom_toast.view.*

/**
 * @author eric
 * @date 9/14/20
 */
object Extensions {

    fun Activity.toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun Activity.customToast(
        context: Context,
        image: Int? = R.drawable.mask_emoji,
        message: String
    ) {
        val toastView = layoutInflater.inflate(
            R.layout.custom_toast, findViewById(R.id.customToastLay), false
        ).apply {
            image?.let {
                toastImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        context, it
                    )
                )
            }
            toastText.text = message
        }
        @Suppress("DEPRECATION")

        return Toast(this).apply {
            setGravity(Gravity.TOP, 0, 0)
            duration = Toast.LENGTH_LONG
            view = toastView
        }.show()
    }

    fun View.snackBuilder(text: String = "", duration: Int = 3000): Snackbar {
        return Snackbar.make(this, text, duration)
    }

    fun Activity.sendEmail(subject: String?, msg: String?, to: Array<String>) {
        val emailIntent = Intent(Intent.ACTION_SEND, Uri.parse("mailto:"))
        emailIntent.apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, msg)
            putExtra(Intent.EXTRA_EMAIL, to)
        }
        startActivity(Intent.createChooser(emailIntent, "Select Email client [for USCC Help desk]"))
    }

    fun Activity.viewMap(locationCode: String, turnByTurnNavigation: Boolean, mode: String) {
        val mapIntent = if (turnByTurnNavigation) {
            /* open map in navigation view */
            Intent(
                Intent.ACTION_VIEW, Uri.parse("google.navigation:q=${locationCode}&mode=${mode}")
            ).apply { setPackage("com.google.android.apps.maps") }

        } else {
            /*view map zoomed by 10 units*/
            Intent(Intent.ACTION_VIEW, Uri.parse("geo:${locationCode}?z=10")).apply {
                setPackage("com.google.android.apps.maps")
            }
        }

        mapIntent.resolveActivity(packageManager)?.let {
            startActivity(mapIntent)
        }
    }

    fun Activity.makeCall(phone: String) {
        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${phone}"))
        callIntent.resolveActivity(packageManager)?.let {
            startActivity(Intent.createChooser(callIntent, "Select calling app"))
        }
    }
}