package com.ericg.usccrecord.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.ericg.usccrecord.R

/**
 * @author eric
 * @date 9/16/20
 */
class PersonDataAdapter(
    private var context: Context?,
    /*private val peopleDataList: List<PersonData>,*/
    private val clickListener: PersonClickListener
) : RecyclerView.Adapter<PersonDataAdapter.PersonViewHolder>() {
    private var adapterPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {

        return PersonViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.raw_item_person_data, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {

        holder.bind()
        context = holder.itemView.context
        adapterPosition = if (adapterPosition < position) {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.from_left))
            position
        } else {
            holder.itemView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out))
            position
        }
    }

    override fun getItemCount(): Int {
        return 11
    }

    inner class PersonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {


        init {
            itemView.setOnClickListener(this)
        }

        fun bind() {

        }

        override fun onClick(view: View?) {
            clickListener.onPersonClick(adapterPosition, view)
        }
    }

    interface PersonClickListener {
        fun onPersonClick(position: Int, view: View?) {
        }
    }
}