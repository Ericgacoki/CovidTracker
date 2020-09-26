/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.views

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ericg.usccrecord.R
import com.ericg.usccrecord.extensions.Constants.AUTO_SIGN_IN
import com.ericg.usccrecord.extensions.Constants.SHOW_ON_BOARD
import kotlinx.android.synthetic.main.fragment_splash.view.*

class SplashScreen : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      //   userDatabase?.clearPersistence()

        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        fun getDrawable(drawable: Int) = ContextCompat.getDrawable(this.requireContext(), drawable)
        val images = arrayOf(
            getDrawable(R.drawable.doc_male),
            getDrawable(R.drawable.doc_female),
            getDrawable(R.drawable.social_distance2),
            getDrawable(R.drawable.social_distance3),
            getDrawable(R.drawable.hand_wash1),
            getDrawable(R.drawable.trace1)
        )
        val randomIndex = (0..5).random()
        val randomImage = images[randomIndex]

        view.randomSplashImage.setImageDrawable(randomImage)

        view.randomSplashText.text = when (randomIndex) {
            0 -> {
                "I can get it, you can get it, anyone can get it!"
            }
            1 -> {
                "Protect me I protect you!"
            }
            2 -> {
                "Keep a distance of 1M from one another!"
            }
            3 -> {
                "Always put on your mask when in public!"
            }
            4 -> {
                "Sanitize, wash your hands regularly and stay safe!"
            }
            // 5
            else -> {
                "Avoid stigmatizing fellow humans!"
            }
        }

        animateViews()
        nextAction()

        return view
    }

    private fun animateViews() {
        /*  randomSplashText.startAnimation(
              AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.fade_out_from_bottom)
          )
          randomSplashText.startAnimation(
              AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.fade_in_from_top)
          )*/
    }

    @Suppress("DEPRECATION")
    private fun nextAction() {

        val autoSignIn =
            requireActivity().getSharedPreferences(AUTO_SIGN_IN, 0).getBoolean(AUTO_SIGN_IN, false)
        val showOnBoardScreen =
            requireActivity().getSharedPreferences(SHOW_ON_BOARD, 0).getBoolean(SHOW_ON_BOARD, true)

        Handler().postDelayed({
            if (!autoSignIn && showOnBoardScreen) {
                /* show onBoard screen */
                findNavController().navigate(R.id.action_from_splashScreen_to_viewPager)

            } else if (!autoSignIn && !showOnBoardScreen) {
                /* prompt to sign in */
                findNavController().navigate(R.id.action_from_splashScreen_to_signIn)
                requireActivity().finish()

            } else if (autoSignIn && !showOnBoardScreen) {
                /* user is already signed in */
                findNavController().navigate(R.id.action_from_splashScreen_to_homeActivity)
                requireActivity().finish()

            } else {/*autoSignIn && showOnBoardScreen  */
                // this is practically impossible
                findNavController().navigate(R.id.action_from_splashScreen_to_viewPager)
            }

        }, 3000)
    }
}