package com.ericg.usccrecord.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R
import com.ericg.usccrecord.data.PersonData
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.raw_item_person_data.view.*

/**
 * @author eric
 * @date 9/16/20
 */
class PersonDataAdapter(
    private var context: Context?,
    var peopleDataList: List<PersonData>,
    private val clickListener: PersonClickListener
) : RecyclerView.Adapter<PersonDataAdapter.PersonViewHolder>() {
    private var itemPosition = -1

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
        itemPosition = if (((position + 2) % 2 == 0)) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.from_left))
            position
        } else {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.from_right))
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
            temp: Int,
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

            if (gender == "male") {
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