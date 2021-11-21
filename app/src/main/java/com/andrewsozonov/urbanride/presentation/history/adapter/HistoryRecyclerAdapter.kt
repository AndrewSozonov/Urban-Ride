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
import com.andrewsozonov.urbanride.data.database.RideDBModel
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel

/**
 * Адаптер для списка истории поездок во фрагменте HistoryFragment
 *
 * @author Андрей Созонов
 */
class HistoryRecyclerAdapter(private val listener : IHistoryRecyclerListener) : RecyclerView.Adapter<HistoryViewHolder>() {

    private var data: List<HistoryModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        holder.bind(data[position])

        holder.itemView.setOnClickListener {
            if (holder.binding.expandableRootItem.visibility == GONE) {
                expandItemView(holder)
            } else {
                closeItemView(holder)
                closeGraph(holder)
            }
        }

        holder.binding.historyMap.setOnClickListener {
            data[position].id.let { id -> listener.onMapClick(id) }
        }

        holder.binding.showGraphButton.setOnClickListener {
            showGraph(holder)
        }

        holder.binding.shareButton.setOnClickListener {
            listener.onShareClick(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    /**
     * Устанавливает список поездок в адаптер
     *
     * @param list список поездок [RideDBModel]
     */
    fun setData(list : List<HistoryModel>) {
        data = list
        notifyDataSetChanged()
    }

    private fun expandItemView(holder: HistoryViewHolder) {
        TransitionManager.beginDelayedTransition(holder.binding.itemCardView, AutoTransition())
        holder.binding.expandableRootItem.visibility = VISIBLE

        val theme: Resources.Theme = holder.itemView.context.theme

        val typedValueColorSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorSurface, typedValueColorSurface, true)
        val cardViewColor = typedValueColorSurface.data

        val typedValueColorOnSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorOnSurface, typedValueColorOnSurface, true)
        val textFieldColor = typedValueColorOnSurface.data
        holder.binding.itemCardView.background.setTint(cardViewColor)

        holder.binding.arrowButton.animate().rotationBy(180F).start()
        holder.binding.dateFieldName.setTextColor(textFieldColor)
        holder.binding.startFieldName.setTextColor(textFieldColor)
        holder.binding.finishFieldName.setTextColor(textFieldColor)
        holder.binding.durationFieldName.setTextColor(textFieldColor)
        holder.binding.distanceFieldName.setTextColor(textFieldColor)
        holder.binding.averageSpeedFieldName.setTextColor(textFieldColor)
        holder.binding.maxSpeedFieldName.setTextColor(textFieldColor)
    }

    private fun closeItemView(holder: HistoryViewHolder) {
        TransitionManager.beginDelayedTransition(holder.binding.itemCardView, AutoTransition())
        holder.binding.expandableRootItem.visibility = GONE
        holder.binding.itemCardView.background.setTint(ContextCompat.getColor(holder.itemView.context, R.color.light_cyan))
        holder.binding.arrowButton.animate().rotationBy(180.0F).start()
        holder.binding.dateFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.startFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.finishFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.durationFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.distanceFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.averageSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        holder.binding.maxSpeedFieldName.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
    }

    private fun showGraph(holder: HistoryViewHolder) {
        if (holder.binding.graphRootView.visibility == GONE) {
            TransitionManager.beginDelayedTransition(holder.binding.itemCardView, AutoTransition())
            holder.binding.graphRootView.visibility = VISIBLE
            holder.binding.timeSpeedGraph.performClick()
        } else {
            closeGraph(holder)
        }
    }

    private fun closeGraph(holder: HistoryViewHolder) {
        TransitionManager.beginDelayedTransition(holder.binding.itemCardView, AutoTransition())
        holder.binding.graphRootView.visibility = GONE
    }
}