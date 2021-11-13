package com.andrewsozonov.urbanride.presentation.history.adapter

import android.content.res.Resources
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.database.Ride

/**
 * Адаптер для списка истории поездок во фрагменте HistoryFragment
 *
 * @author Андрей Созонов
 */
class HistoryRecyclerAdapter(private val listener : IHistoryRecyclerListener) : RecyclerView.Adapter<HistoryViewHolder>() {

    private var data: List<Ride> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(holder.itemView.context)
        val unitsKm = holder.itemView.context.getString(R.string.units_kilometers)
        val isMetricUnits = (sharedPrefs.getString(holder.itemView.context.getString(R.string.unit_system_pref_key), unitsKm) == unitsKm)

        holder.bind(data[position], isMetricUnits)

        holder.itemView.setOnClickListener {
            if (holder.expandableView.visibility == GONE) {
                expandItemView(holder)
            } else {
                closeItemView(holder)
                closeGraph(holder)
            }
        }

        holder.mapImageView.setOnClickListener {
            data[position].id?.let { id -> listener.onMapClick(id) }
        }

        holder.showGraphButton.setOnClickListener {
            showGraph(holder)
        }

        holder.shareButton.setOnClickListener {
            listener.onShareClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Устанавливает список поездок в адаптер
     *
     * @param list список поездок [Ride]
     */
    fun setData(list : List<Ride>) {
        data = list
        notifyDataSetChanged()
    }

    private fun expandItemView(holder: HistoryViewHolder) {
        TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
        holder.expandableView.visibility = VISIBLE

        val theme: Resources.Theme = holder.itemView.context.theme

        val typedValueColorSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorSurface, typedValueColorSurface, true)
        val cardViewColor = typedValueColorSurface.data

        val typedValueColorOnSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorOnSurface, typedValueColorOnSurface, true)
        val textFieldColor = typedValueColorOnSurface.data
        holder.cardView.background.setTint(cardViewColor)

        holder.arrowButton.animate().rotationBy(180F).start()
        holder.dateFieldName.setTextColor(textFieldColor)
        holder.startFieldName.setTextColor(textFieldColor)
        holder.finishFieldName.setTextColor(textFieldColor)
        holder.durationFieldName.setTextColor(textFieldColor)
        holder.distanceFieldName.setTextColor(textFieldColor)
        holder.averageSpeedFieldName.setTextColor(textFieldColor)
        holder.maxSpeedFieldName.setTextColor(textFieldColor)
    }

    private fun closeItemView(holder: HistoryViewHolder) {
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

    private fun showGraph(holder: HistoryViewHolder) {
        if (holder.graphRootView.visibility == GONE) {
            TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
            holder.graphRootView.visibility = VISIBLE
            holder.timeSpeedRadioButton.performClick()
        } else {
            closeGraph(holder)
        }
    }

    private fun closeGraph(holder: HistoryViewHolder) {
        TransitionManager.beginDelayedTransition(holder.cardView, AutoTransition())
        holder.graphRootView.visibility = GONE
    }
}