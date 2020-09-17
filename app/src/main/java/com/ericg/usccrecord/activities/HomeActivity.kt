package com.ericg.usccrecord.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R
import com.ericg.usccrecord.adapters.PersonDataAdapter
import com.ericg.usccrecord.data.PersonData
import com.ericg.usccrecord.extensions.Extensions.toast
import com.ericg.usccrecord.firebase.SaveData
import kotlinx.android.synthetic.main.activity_home.*

/**
 * @author eric
 * @date 9/14/20
 */
class HomeActivity : AppCompatActivity(), PersonDataAdapter.PersonClickListener {

    private val permissions = arrayOf(
        android.Manifest.permission.SEND_SMS,
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.WRITE_CALENDAR,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.CALL_PHONE
    )

    var peopleList : List<PersonData> = ArrayList()
    var personDataAdapter = PersonDataAdapter(this, peopleList,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        requestAppPermissions()

        onScroll()
        onSwipeToRefresh()

        personDataRecyclerview.adapter = personDataAdapter

        btnAddPerson.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, permissions[3]
                ) == PackageManager.PERMISSION_GRANTED
            ) { // todo open createPerson data btm sheet dialog

                val person = PersonData("Eric", "male", 19, 36, "0716965216", null)
                SaveData().newEntry("personData", person, null)

            } else {
                toast("Please grant the requested permissions")
                ActivityCompat.requestPermissions(this, arrayOf(permissions[3]), 1)
            }
        }
    }

    private fun requestAppPermissions() = ActivityCompat.requestPermissions(this, permissions, 0)

    private fun onScroll() {
        personDataRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*  capture a bottom-to-top scroll */
                if (dy > dx) {
                    btnAddPerson.startAnimation(
                        AnimationUtils.loadAnimation(
                            this@HomeActivity, R.anim.button_from_bottom
                        )
                    )
                }
            }
        })
    }

    private fun loadData(refresh: Boolean){
        if (refresh){
            showLoading(false)

        } else{
            showLoading(true)

        }
    }

    private fun onSwipeToRefresh() {


    }

    override fun onPersonClick(position: Int, id:Int?) {
        super.onPersonClick(position, id)
        toast("you clicked item ${position + 1}")

        when (id) {

        }
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