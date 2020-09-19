/*
 * Copyright (c) 2020. This software is owned by @Eric_gacoki
 */

package com.ericg.usccrecord.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R
import com.ericg.usccrecord.model.PersonData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.raw_item_person_data.view.*
import java.util.*

/**
 * @author eric
 * @date 9/16/20
 */

class PersonDataAdapter(
    private var context: Context?,
    var peopleDataList: List<PersonData>,
    private val clickListener: PersonClickListener
) : RecyclerView.Adapter<PersonDataAdapter.PersonViewHolder>() {
    private var prevPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder =
        PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.raw_item_person_data, parent, false)
        )

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {

        val currentPerson = peopleDataList[position]
        holder.bind(
            currentPerson.name,
            currentPerson.gender,
            currentPerson.age,
            currentPerson.temp,
            currentPerson.phone,
            currentPerson.date
        )

        context = holder.itemView.context
        prevPosition = if (prevPosition > position) {
            // disable animations
            // holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.quick_from_top))
            position
        } else {
            // holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.quick_from_bottom))
            position
        }
    }

    override fun getItemCount(): Int = peopleDataList.size

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val tempIndicatorDot: CircleImageView = itemView.tempIndicatorDot
        private val tempIndicatorText: TextView = itemView.tempIndicatorText
        private val personProfilePic: CircleImageView = itemView.personProfilePic
        private val personName: TextView = itemView.personName
        private val gender: TextView = itemView.gender
        private val age: TextView = itemView.age
        private val phone: TextView = itemView.phone
        private val date: TextView = itemView.date

        /* buttons */

        private val btnDelete: ImageView = itemView.deleteIcon
        private val btnCall: ImageView = itemView.callIcon
        private val btnMap: ImageView = itemView.mapIcon

        init {
            personProfilePic.setOnClickListener(this)
            btnDelete.setOnClickListener(this)
            btnCall.setOnClickListener(this)
            btnMap.setOnClickListener(this)
        }

        fun bind(
            name: String,
            gender: String,
            age: Int,
            temp: Float,
            phone: String,
            date: Any?
        ) {
            this.personName.text = name
            this.gender.text = gender
            this.age.text = "$age Yrs"
            this.tempIndicatorText.text = "$temp *C"
            this.phone.text = phone
            this.date.text = date.toString()

            /* set temp indicator */

            if (temp <= 37.5) {
                this.tempIndicatorDot.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_safe_dot)
                })
            } else {
                this.tempIndicatorDot.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(it, R.drawable.ic_danger_dot)
                })
            }

            /* set profile pic */

            if (gender.toLowerCase(Locale.ROOT) == "male") {
                this.personProfilePic.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(it, R.drawable.man_mask)
                })
            } else {
                this.personProfilePic.setImageDrawable(context?.let {
                    ContextCompat.getDrawable(it, R.drawable.woman_mask)
                })
            }
        }

        override fun onClick(view: View?) {
            // if (adapterPosition != noPosition)

            clickListener.onPersonClick(adapterPosition, view?.id)
        }
    }

    interface PersonClickListener {
        fun onPersonClick(position: Int, id: Int?) {
        }
    }
}