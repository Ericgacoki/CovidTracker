package com.ericg.usccrecord.onBoardFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ericg.usccrecord.R
import com.ericg.usccrecord.adapters.ViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_view_pager2.view.*

/**
 * @author eric
 * @date 9/14/20
 */
class ViewPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_pager2, container, false)

        val fragmentList = arrayListOf(
            OnBoard1(),
            OnBoard2(),
            OnBoard3()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.viewPager.adapter = adapter
        return view
    }
}