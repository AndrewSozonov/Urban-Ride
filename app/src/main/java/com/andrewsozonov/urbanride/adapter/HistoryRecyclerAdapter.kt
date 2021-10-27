package com.andrewsozonov.urbanride.adapter

import android.content.res.Resources
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.util.DataFormatter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class HistoryRecyclerAdapter : RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>() {

    private var data: List<Ride> = mutableListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnClickListener {
            if (holder.expandableView.visibility == GONE) {
                TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
                holder.expandableView.visibility = VISIBLE
                holder.cardView.background.setTint(ContextCompat.getColor(holder.itemView.context, R.color.light_gray))
                holder.arrowButton.animate().rotationBy(180F).start()
                holder.dateFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.startFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.finishFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.durationFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.distanceFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.averageSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
                holder.maxSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.middle_blue))
            } else {
                TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
                holder.expandableView.visibility = GONE
                holder.cardView.background.setTint(ContextCompat.getColor(holder.itemView.context, R.color.light_cyan))
                holder.arrowButton.animate().rotationBy(180.0F).start()
                holder.dateFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.startFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.finishFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.durationFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.distanceFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.averageSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                holder.maxSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(list : List<Ride>) {
        data = list
        notifyDataSetChanged()
    }


    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val expandableView : ConstraintLayout = itemView.findViewById(R.id.expandable_root_item)
        val cardView : CardView = itemView.findViewById(R.id.item_cardView)
        val arrowButton: Button = itemView.findViewById(R.id.arrow_button)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_textView)
        private val startTextView: TextView = itemView.findViewById(R.id.start_textView)
        private val finishTextView: TextView = itemView.findViewById(R.id.finish_textView)
        private val durationTextView: TextView = itemView.findViewById(R.id.duration_textView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.distance_textView)
        private val averageSpeedTextView: TextView = itemView.findViewById(R.id.average_speed_textView)
        private val maxSpeedTextView: TextView = itemView.findViewById(R.id.max_speed_textView)
        val dateFieldName: TextView = itemView.findViewById(R.id.date_field_name)
        val startFieldName: TextView = itemView.findViewById(R.id.start_field_name)
        val finishFieldName: TextView = itemView.findViewById(R.id.finish_field_name)
        val durationFieldName: TextView = itemView.findViewById(R.id.duration_field_name)
        val distanceFieldName: TextView = itemView.findViewById(R.id.distance_field_name)
        val averageSpeedFieldName: TextView = itemView.findViewById(R.id.average_speed_field_name)
        val maxSpeedFieldName: TextView = itemView.findViewById(R.id.max_speed_field_name)
        var mapImageView: ImageView = itemView.findViewById(R.id.history_map)
        private val resources: Resources = itemView.resources

        fun bind(ride: Ride) {
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val startTime = Calendar.getInstance().apply {
                timeInMillis = ride.startTime
                Log.d("Adapter", "finishtime: ${ride.finishTime}")
            }

            dateTextView.text = dateFormat.format(startTime.time)
            startTextView.text = timeFormat.format(startTime.time)


            val finishTime = Calendar.getInstance().apply {
                timeInMillis = ride.finishTime
                Log.d("Adapter", "finishtime: ${ride.finishTime}")
            }
            finishTextView.text = timeFormat.format(finishTime.time)

            durationTextView.text = DataFormatter.formatTime(ride.duration)
            distanceTextView.text = resources.getString(R.string.km, ride.distance)
            averageSpeedTextView.text = resources.getString(R.string.km_h, ride.averageSpeed)
            maxSpeedTextView.text = resources.getString(R.string.km_h, ride.maxSpeed)
            Glide
                .with(itemView.context)
                .load(ride.mapImg)
                .into(mapImageView)
        }
    }
}