/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.views

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R
import com.ericg.usccrecord.adapters.PersonDataAdapter
import com.ericg.usccrecord.extensions.Extensions.customToast
import com.ericg.usccrecord.extensions.Extensions.makeCall
import com.ericg.usccrecord.extensions.Extensions.toast
import com.ericg.usccrecord.extensions.Extensions.viewMap
import com.ericg.usccrecord.firebase.GetData
import com.ericg.usccrecord.model.PersonData
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.create_person_inputs.view.*

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

    var peopleList: List<PersonData> = ArrayList()
    var personDataAdapter = PersonDataAdapter(this, peopleList, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        personDataRecyclerview.adapter = personDataAdapter

        requestAppPermissions()
        setClickListeners()
        onScroll()
        onSwipeToRefresh()
        loadData(false).observe(this, { done ->
            when {
                done -> homeLoadingLay.visibility = INVISIBLE
            }
        })
    }

    @SuppressLint("InflateParams")
    private fun setClickListeners() {
        homeLoadingLay.setOnClickListener { }

        btnAddPerson.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, permissions[3]
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val sheetDialog = BottomSheetDialog(this, 0)
                val sheetView = layoutInflater.inflate(R.layout.create_person_inputs, null)

                sheetView.apply {
                    btnCancelEntry.setOnClickListener {
                        sheetDialog.cancel()
                    }
                    btnSaveNewEntry.setOnClickListener {
                        customToast(context = this@HomeActivity, message = "saving is not ready" )
                    }
                }

                sheetDialog.apply {
                    setContentView(sheetView)
                    setCancelable(false)
                    show()
                }
                /* sample person
                val person = PersonData(
                    "Frank Helen", "male", 47, 35.81F, "07456236771", null, "kiamutugu", null
                )    SaveData().newEntry("personData", person, null) */
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permissions[3]), 5)
                toast("Please grant the requested permissions")
            }
        }
    }

    private fun requestAppPermissions() = ActivityCompat.requestPermissions(this, permissions, 1)

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

    private fun showLoadingView(show: Boolean) {
        if (show) {
            homeLoadingLay.apply {
                visibility = VISIBLE

                homeLoadingView.apply {
                    setViewColor(getColor(R.color.colorPurple))
                    startAnim()
                }
            }
        } else {
            homeLoadingLay.visibility = INVISIBLE
        }
    }

    private fun loadData(refreshing: Boolean): MutableLiveData<Boolean> {
        val complete: MutableLiveData<Boolean> = MutableLiveData(false)

        fun getData() {
            complete.value = false

            GetData().get("personData")?.addOnCompleteListener {
                complete.value = true
                if (it.isSuccessful) {

                    peopleList = it.result!!.toObjects(PersonData::class.java)
                    personDataAdapter.peopleDataList = peopleList
                    personDataAdapter.notifyDataSetChanged()

                    when (val loadedPeople = peopleList.size) {
                        0 -> {
                            displayNoDataMessage(true)
                            customToast(context = this, message = "no data yet!")
                        }
                        1 -> {
                            displayNoDataMessage(false)
                            customToast(context = this, message = "loaded 1 person successfully!")
                        }
                        else -> {
                            displayNoDataMessage(false)
                            customToast(
                                context = this,
                                message = "loaded $loadedPeople people successfully!"
                            )
                        }
                    }
                } else {
                    toast("loading failed")
                }
            }
        }

        if (refreshing) {
            showLoadingView(false)
            getData()

        } else {
            showLoadingView(true)
            getData()
        }

        return complete
    }

    private fun displayNoDataMessage(display: Boolean) {
        if (display) {
            ivNoData.visibility = VISIBLE
            tvNoData.visibility = VISIBLE
        } else {
            ivNoData.visibility = INVISIBLE
            tvNoData.visibility = INVISIBLE
        }
    }

    private fun onSwipeToRefresh() {
        homeSwipeToRefresh.apply {
            setOnRefreshListener {
                loadData(true).observe(this@HomeActivity, { done ->
                    when {
                        done -> this.isRefreshing = false
                    }
                })
            }
        }
    }

    override fun onPersonClick(position: Int, id: Int?) {
        super.onPersonClick(position, id)
        when (id) {
            R.id.deleteIcon -> {
                val msg = "delete ${peopleList[position].name} not ready"
                customToast(this, R.drawable.ic_delete_forever, msg)
                removeAt(position)
            }
            R.id.callIcon -> {
                val msg = "proceed to call ${peopleList[position].name}"
                customToast(this, R.drawable.ic_call, msg)
                makeCall(peopleList[position].phone)
            }
            R.id.mapIcon -> {
                val msg = "view ${peopleList[position].locationName} map"
                customToast(this, R.drawable.ic_location, msg)
                viewMap(
                    "${peopleList[position].locationCode}",
                    true,
                    "l" /*l = two wheeler eg. motorbike*/
                )
            }
        }
    }

    private fun removeAt(position: Int) {

/* val userUID = mUser?.uid as String
 val oldList = peopleList
 val convertedList = oldList as ArrayList<PersonData>
 val newList = convertedList.removeAt(position)

 FirebaseUtils.userDatabasecollection("USCCMember1/${userUID}/personData")
     ?.id?.removeRange(position..position)
      personDataAdapter.peopleDataList = listOf(newList)
       personDataAdapter.notifyItemRemoved(position)
     */

        personDataAdapter.notifyDataSetChanged()
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