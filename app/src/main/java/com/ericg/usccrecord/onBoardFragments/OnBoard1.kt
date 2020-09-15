package com.ericg.usccrecord.onBoardFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ericg.usccrecord.R
import kotlinx.android.synthetic.main.fragment_on_board1.view.*

/**
 * @author eric
 * @date 9/14/20
 */

class OnBoard1 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_on_board1, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        view.board1_2nd_dot.setOnClickListener {
            viewPager?.currentItem = 1
        }
        view.board1_3rd_dot.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }
}