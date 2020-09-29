/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.views

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RatingBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R
import com.ericg.usccrecord.adapters.PersonDataAdapter
import com.ericg.usccrecord.extensions.Extensions.connectOn
import com.ericg.usccrecord.extensions.Extensions.customToast
import com.ericg.usccrecord.extensions.Extensions.makeCall
import com.ericg.usccrecord.extensions.Extensions.sendEmail
import com.ericg.usccrecord.extensions.Extensions.toast
import com.ericg.usccrecord.extensions.Extensions.viewMap
import com.ericg.usccrecord.firebase.FirebaseUtils.mUser
import com.ericg.usccrecord.firebase.FirebaseUtils.userDatabase
import com.ericg.usccrecord.firebase.GetData
import com.ericg.usccrecord.firebase.SaveData
import com.ericg.usccrecord.model.PersonData
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.about_app_developer.*
import kotlinx.android.synthetic.main.about_app_developer.view.*
import kotlinx.android.synthetic.main.about_app_dialog.*
import kotlinx.android.synthetic.main.about_app_dialog.view.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.analsis_dialog.*
import kotlinx.android.synthetic.main.analsis_dialog.view.*
import kotlinx.android.synthetic.main.create_person_inputs.view.*
import kotlinx.android.synthetic.main.rate_app_dialog.*
import kotlinx.android.synthetic.main.rate_app_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

/**
 * @author eric
 * @date 9/14/20
 */

@Suppress("SpellCheckingInspection")
@RequiresApi(Build.VERSION_CODES.O)
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
        "Githure", "Gituba", "Kiamutugu", "Ngariama", "Gitemani", "Kiamugumo",
        "kajuu", "Nyangeni", "Kiriko", "Ngerwe", "Gaciongo", "Karinga", "Other"
    )

    var peopleList: List<PersonData> = ArrayList()
    var personDataAdapter = PersonDataAdapter(this, peopleList, this)

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = " USCC [Beta]"
        }
        personDataRecyclerview.adapter = personDataAdapter

        requestAppPermissions() /* if not permitted */
        setClickListeners()
        onScroll()
        onSwipeToRefresh()
        loadData(false).observe(this, { complete ->
            when {
                complete -> showLoadingView(false)
            }
        })

        /* create side drawer */
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        handleDrawerClicks()
    }

    private fun handleDrawerClicks() {

        homeNavView.setNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.seeAnalysis -> {
                    showAnalysisDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.aboutApp -> {
                    showAboutAppDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.rateApp -> {
                    showRatingDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.aboutDev -> {
                    showAboutDevDialog()
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
            }
            true
        }
    }

    /* create menu */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        /*options menu */
        when (item.itemId) {
            R.id.print -> {
                customToast(this, R.drawable.ic_print_disabled, "printing is disabled")
            }
        }

        /* drawer */
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    private fun setClickListeners() {
        homeLoadingLay.setOnClickListener { /*prevent clicks on views behind it*/ }

        btnAddPerson.setOnClickListener {

            val btmSheetView = layoutInflater.inflate(
                R.layout.create_person_inputs, findViewById(R.id.createPersonInputsLay)
            )

            val sheetDialog = BottomSheetDialog(this)
                .apply {
                    setContentView(btmSheetView, findViewById(R.id.createPersonInputsLay))
                    setCancelable(false)
                    create()
                    show()
                }

            btmSheetView.apply {
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

                        /**@__IMPORTANT__ generate a string that will be used as the id of the person document and as a field too */
                        val docId = userDatabase?.collection("USCCMembers/${mUser?.uid}/personData")
                            ?.document()?.id

                        val personData =
                            PersonData(
                                docId,
                                name,
                                gender,
                                age,
                                temp,
                                phone,
                                locationName,
                                FieldValue.serverTimestamp(),
                                null,
                                null
                            )
                        SaveData().newEntry(docId!!, personData)
                        sheetDialog.dismiss()
                        loadData(false).observe(this@HomeActivity, { complete ->
                            if (complete) showLoadingView(false)
                        })

                    } else {
                        inputs.forEach {
                            if (it.text.toString().trim().isEmpty()) {
                                it.error = "${it.hint} is required"
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestAppPermissions() = ActivityCompat.requestPermissions(this, permissions, 1)

    private fun showAnalysisDialog() {

        var safeMales = 0
        var malesInDanger = 0
        val totalMales: Int

        val totalFemales: Int
        var safeFemales = 0
        var femalesInDanger = 0

        peopleList.forEach { person ->
            if (person.gender.trim().toLowerCase(Locale.ROOT) == "male") {
                if (person.temp <= 37.5F) {
                    safeMales += 1
                } else {
                    malesInDanger += 1
                }
            } else {
                if (person.temp <= 37.5F) {
                    safeFemales += 1
                } else {
                    femalesInDanger += 1
                }
            }
        }

        totalMales = safeMales + malesInDanger
        totalFemales = safeFemales + femalesInDanger

        val analysisView = layoutInflater.inflate(R.layout.analsis_dialog, analysisRootLay).apply {

            this.tvSafeMales.text = safeMales.toString()
            this.tvMalesInDanger.text = malesInDanger.toString()
            this.tvTotalMales.text = totalMales.toString()

            this.tvSafeFemales.text = safeFemales.toString()
            this.tvFemalesInDanger.text = femalesInDanger.toString()
            this.tvTotalFemales.text = totalFemales.toString()

        }
        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setView(analysisView)
            create()
            show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showAboutAppDialog() {
        val aboutAppView =
            layoutInflater.inflate(R.layout.about_app_dialog, aboutAppRootLay).apply {
                this.userID.text = "User ID : " + mUser?.uid as String
            }

        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setView(aboutAppView)
            create()
            show()
        }
    }

    private fun showRatingDialog() {
        var ratingStars = 3F // default rating
        var feedback: String

        val ratingView = layoutInflater.inflate(R.layout.rate_app_dialog, rateAppRootLay).apply {
            ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _: RatingBar, rating: Float, _: Boolean ->
                    ratingStars = rating
                }
            btnRate.setOnClickListener {
                if (ratingStars > 0.0 && etFeedback.text.toString().trim().isNotEmpty()) {
                    feedback = etFeedback.text.toString().trim()
                    sendEmail(
                        "USCC TEST RECORD APP RATING",
                        "Rating: $ratingStars over 5.0 \nFeedback: $feedback",
                        arrayOf("gacokieric@gmail.com")
                    )
                } else if (ratingStars < 1.0) {
                    ratingBar.rating = 1F
                    toast("rating can\'t be zero")
                } else {
                    etFeedback.error = "say something about this app"
                }
            }
        }

        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setView(ratingView)
            create()
            show()
        }
    }

    private fun showAboutDevDialog() {

        val aboutDevView =
            LayoutInflater.from(this).inflate(R.layout.about_app_developer, aboutDevRootLay).apply {
                this.btnTwitter.setOnClickListener {
                    connectOn("twitter")
                }
                this.btnTwitter.setOnLongClickListener {
                    customToast(this@HomeActivity, R.drawable.mask_emoji, "twitter icon not ready")
                    true
                }

                btnLinkedIn.setOnClickListener {
                    connectOn("linkedIn")
                }
                btnLinkedIn.setOnLongClickListener {
                    customToast(this@HomeActivity, R.drawable.linkedinicon, "linkedIn")
                    true
                }

                btnEmail.setOnClickListener {
                    sendEmail(
                        "USCC Regards", "", arrayOf("gacokieric@gmail.com")
                    )
                }
                btnEmail.setOnLongClickListener {
                    customToast(this@HomeActivity, R.drawable.ic_email, "Email")
                    true
                }

                btnGitHub.setOnClickListener {
                    connectOn("gitHub")
                }
                btnGitHub.setOnLongClickListener {
                    customToast(this@HomeActivity, R.drawable.githubicon, "More of my apps")
                    true
                }
            }

        AlertDialog.Builder(this).apply {
            setView(aboutDevView)
            create()
            show()
        }
    }


    private fun onScroll() {
        personDataRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                /*  capture a bottom-to-top scroll ie a positive change in y-axis */
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

            GetData().get()?.addOnCompleteListener {
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

    @SuppressLint("ResourceType")
    override fun onPersonClick(position: Int, id: Int?) {
        super.onPersonClick(position, id)
        val name = peopleList[position].name
        when (id) {

            R.id.deleteIcon -> {
                AlertDialog.Builder(this, R.style.Theme_AppCompat_Light_Dialog_Alert).apply {
                    setIcon(R.drawable.ic_warning)
                    setTitle("Delete confirmation")
                    setMessage("Are you sure to delete $name ?")
                    setPositiveButton("Yes") { _, _ ->

                        userDatabase?.collection("USCCMembers/${mUser?.uid}/personData")
                            ?.document(peopleList[position].docId!!)?.delete()
                        loadData(false).observe(this@HomeActivity, { complete ->
                            if (complete) {
                                showLoadingView(false)
                            }
                        })
                    }
                    setNegativeButton("No") { _, _ ->
                        /* dismiss the dialog*/
                    }
                    create().show()
                }
            }

            R.id.callIcon -> {
                val msg = "proceed to call $name"
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    customToast(this, R.drawable.ic_call, msg)
                    makeCall(peopleList[position].phone)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CALL_PHONE),
                        0
                    )
                }
            }

            R.id.mapIcon -> {
                val msg = "view ${peopleList[position].locationName}'s map"

                customToast(this, R.drawable.ic_location, msg)
                viewMap(
                    "${peopleList[position].locationCode}",
                    true,
                    "l" /*l = two wheeler eg. motorbike*/
                )
            }
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
