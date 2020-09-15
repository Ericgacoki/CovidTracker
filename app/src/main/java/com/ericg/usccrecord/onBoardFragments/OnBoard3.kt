package com.ericg.usccrecord.onBoardFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ericg.usccrecord.Constants.AUTO_SIGN_IN
import com.ericg.usccrecord.Constants.SHOW_ON_BOARD
import com.ericg.usccrecord.R
import kotlinx.android.synthetic.main.fragment_on_board3.view.*

/**
 * @author eric
 * @date 9/14/20
 */
class OnBoard3 :
    Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_on_board3, container, false)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        val autoSignIn = requireActivity().getSharedPreferences(AUTO_SIGN_IN, 0)
            .getBoolean(AUTO_SIGN_IN, false)

        view.apply {

            board3_1st_dot.setOnClickListener {
                viewPager?.currentItem = 0
            }

            board3_2nd_dot.setOnClickListener {
                viewPager?.currentItem = 1
            }

            finishOnBoard.setOnClickListener {
                if (autoSignIn) {
                    findNavController().navigate(R.id.action_from_viewPager_to_homeActivity)
                    setPrefs()
                    requireActivity().finish()
                } else {
                    findNavController().navigate(R.id.action_from_viewPager_to_signIn)
                    setPrefs()
                    requireActivity().finish()
                }
            }
        }

        return view
    }

    private fun setPrefs() {
        requireActivity().getSharedPreferences(SHOW_ON_BOARD, 0).edit()
            .putBoolean(SHOW_ON_BOARD, false).apply()
    }
}
