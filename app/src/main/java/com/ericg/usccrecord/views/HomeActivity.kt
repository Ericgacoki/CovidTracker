/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.views

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.ericg.usccrecord.firebase.SaveData
import com.ericg.usccrecord.model.PersonData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.create_person_inputs.view.*
import kotlin.properties.Delegates

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
    private lateinit var name: String
    private lateinit var phone: String

    private lateinit var gender: String
    private lateinit var locationName: String
    private lateinit var inputs: Array<TextInputEditText>
    private var notEmpty: Boolean by Delegates.notNull()

    private val genders: Array<String> = arrayOf("Male", "Female")
    private val locations: Array<String> = arrayOf(
        "Githure", "Gituba", "Ngerwe", "Ngariama", "Nyangeni",
        "Gitemani", "Gaciongo", "Kiriko", "Karinga", "Kiamugumo", "Other"
    )

    private var peopleList: List<PersonData> = ArrayList()
    private var personDataAdapter = PersonDataAdapter(this, peopleList, this)

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        personDataRecyclerview.adapter = personDataAdapter

        requestAppPermissions() /* if not permitted */
        setClickListeners()
        onScroll()
        onSwipeToRefresh()
        loadData(false).observe(this, { done ->
            when {
                done -> homeLoadingLay.visibility = INVISIBLE
            }
        })

        /* create side drawer */
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

    }

    /* create menu*/

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.print -> {
                customToast(this, R.drawable.ic_print_disabled, "printing is not ready")
            }
        }
        return true
    }

    @SuppressLint("InflateParams")
    private fun setClickListeners() {
        homeLoadingLay.setOnClickListener {/*prevent clicks on views behind it*/ }

        btnAddPerson.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val sheetDialog = BottomSheetDialog(this)
                val sheetView = layoutInflater.inflate(
                    R.layout.create_person_inputs, findViewById(R.id.createPersonInputsLay)
                )

                sheetView.apply {
                    inputs = arrayOf(
                        personName, personPhone, personTemp, personAge
                    )
                    this.genderSpinner.apply {
                        adapter = ArrayAdapter(
                            this@HomeActivity,
                            R.layout.support_simple_spinner_dropdown_item, genders
                        )

                        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                gender = adapterView?.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {}
                        }
                    }

                    this.locationSpinner.apply {
                        adapter = ArrayAdapter(
                            this@HomeActivity,
                            R.layout.support_simple_spinner_dropdown_item, locations
                        )
                        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                adapterView: AdapterView<*>?, view: View?, position: Int, id: Long
                            ) {
                                locationName = adapterView?.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(p0: AdapterView<*>?) {}
                        }
                    }

                    btnCancelEntry.setOnClickListener { sheetDialog.dismiss() }

                    btnSaveNewEntry.setOnClickListener {

                        name = personName.text.toString().trim()
                        phone = personPhone.text.toString().trim()

                        notEmpty = personName.text.toString().trim().isNotEmpty() &&
                                personPhone.text.toString().trim().isNotEmpty() &&
                                personTemp.text.toString().trim().isNotEmpty() &&
                                personAge.text.toString().trim().isNotEmpty()

                        if (notEmpty) {
                            val age = personAge.text.toString().toInt()
                            val temp = personTemp.text.toString().toFloat()

                            val personData =
                                PersonData(name, gender, age, temp, phone, null, locationName, null)
                            SaveData().newEntry("personData", personData, null)
                                .observe(this@HomeActivity, { success ->
                                    if (success) {
                                        sheetDialog.dismiss()
                                        loadData(false).observe(this@HomeActivity, { complete ->
                                            if (complete) showLoadingView(false)
                                        })
                                    }
                                })
                            // todo remove the following 2 lines
                            sheetDialog.dismiss()
                            loadData(true)
                        } else {
                            inputs.forEach {
                                if (it.text.toString().trim().isEmpty()) {
                                    it.error = "${it.hint} is required"
                                }
                            }
                        }
                    }
                }

                sheetDialog.apply {
                    setContentView(sheetView, findViewById(R.id.createPersonInputsLay))
                    setCancelable(false)
                    create()
                    show()
                }

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permissions[3]), 5)
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

    private fun loadData(onRefresh: Boolean): MutableLiveData<Boolean> {
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

        if (onRefresh) {
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
        val name = peopleList[position].name
        when (id) {
            R.id.deleteIcon -> {
                val msg = "delete $name is not ready"
                customToast(this, R.drawable.ic_delete_forever, msg)
                removeAt(position)
            }
            R.id.callIcon -> {
                val msg = "proceed to call $name"
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
        position + 0
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