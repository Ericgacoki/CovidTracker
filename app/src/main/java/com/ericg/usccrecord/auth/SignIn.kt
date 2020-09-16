package com.ericg.usccrecord.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ericg.usccrecord.Constants.AUTO_SIGN_IN
import com.ericg.usccrecord.R
import com.ericg.usccrecord.activities.HomeActivity
import com.ericg.usccrecord.extensions.Extensions.sendEmail
import com.ericg.usccrecord.extensions.Extensions.snackBuilder
import com.ericg.usccrecord.extensions.Extensions.toast
import com.ericg.usccrecord.firebase.FirebaseUtils.mAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.view.*

/**
 * @author eric
 * @date 9/14/20
 */

@RequiresApi(Build.VERSION_CODES.N)
class SignIn : AppCompatActivity() {

    private var clicks = 0
    private var elapseTime = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        handleClicks()
    }

    private fun handleClicks() {

        btnSignIn.setOnClickListener {
            signIn()
        }

        btnForgotDetails.setOnClickListener {
            sendEmail(
                "Forgot details",
                null,
                to = arrayOf("gacokieric@gmail.com")
            )
        }
        btnReportIssue.setOnClickListener {
            sendEmail(
                "Sign in issue",
                "Hello, please remind me the sign in details for USCC App.",
                to = arrayOf("gacokieric@gmail.com")
            )
        }
    }

    private fun validInputs(): Boolean {
        val inputs = arrayOf(etEmail, etPassword)

        val valid: Boolean = etEmail.text.toString().trim().isNotEmpty() &&
                etPassword.text.toString().trim().isNotEmpty()

        if (!valid) {
            inputs.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
        return valid
    }

    private fun signIn() {
        clicks += 1
        elapseTime = clicks * 6000

        when {
            clicks % 5 == 0 -> {
                showTryAgain()
            }

            clicks > 5 -> {
                /*get a view to use the snackBar*/

                btnForgotDetails.snackBuilder("Do you need help").apply {
                    setAction("Yes") {
                        clicks = 0
                        sendEmail(
                            "Forgot details",
                            "Hello, please remind me the sign in details for USCC App.",
                            to = arrayOf("gacokieric@gmail.com")
                        )
                    }

                    setBackgroundTint(ContextCompat.getColor(this@SignIn, R.color.colorPrimary))
                    setTextColor(ContextCompat.getColor(this@SignIn, R.color.colorWhite))
                    setActionTextColor(ContextCompat.getColor(this@SignIn, R.color.colorAccent))

                    show()
                }

            }

            else -> {
                if (validInputs()) {

                    val email = etEmail.text.toString()
                    val password = etPassword.text.toString()

                    loading(true)
                    mAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            loading(false)
                            toast("signed in successfully")
                            getSharedPreferences(AUTO_SIGN_IN, 0).edit()
                                .putBoolean(AUTO_SIGN_IN, true).apply()

                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        } else {
                            loading(false)
                            toast("sign in failed!")
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun showTryAgain() {
        enableViews(false)
        tryAgainLay.apply {
            visibility = VISIBLE
            startAnimation(AnimationUtils.loadAnimation(this@SignIn, R.anim.fade_out_from_bottom))

            chronometer.apply {
                isCountDown = true
                base = SystemClock.elapsedRealtime() + elapseTime
                start()
            }

            Handler().postDelayed({
                startAnimation(AnimationUtils.loadAnimation(this@SignIn, R.anim.fade_in_from_top))

                enableViews(true)
                chronometer.stop()
                tryAgainLay.visibility = INVISIBLE
            }, elapseTime.toLong())
        }
    }

    private fun enableViews(enabled: Boolean) {
        val views = arrayOf(etEmail, etPassword, btnSignIn)
        if (enabled) {
            views.forEach { view ->
                view.apply {
                    isEnabled = true
                    isClickable = true
                }
            }
        } else {
            views.forEach { view ->
                view.apply {
                    isEnabled = false
                    isClickable = false
                }
            }
        }
    }

    private fun loading(show: Boolean) {
        loadingView.visibility = if (show) {
            loadingView.apply {
                setViewColor(R.color.colorWhite)
                startAnim()
            }
            VISIBLE
        } else INVISIBLE

        if (show) {
            btnSignIn.text = ""
            btnSignIn.isClickable = false
        } else {
            btnSignIn.text = getString(R.string.sign_in)
            btnSignIn.isClickable = true
        }
    }

    private var goBack = false

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