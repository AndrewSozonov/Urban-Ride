package com.andrewsozonov.urbanride.presentation.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel

/**
 * Адаптер для списка истории поездок во фрагменте HistoryFragment
 *
 * @param listener слушатель нажатий на карту и кнопку share
 *
 * @author Андрей Созонов
 */
class HistoryRecyclerAdapter(private val listener: IHistoryRecyclerListener) :
    RecyclerView.Adapter<HistoryViewHolder>() {

    private var data: List<HistoryModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

        holder.bind(data[position])

        holder.binding.historyMap.setOnClickListener {
            data[position].id.let { id -> listener.onMapClick(id) }
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
     * @param list список поездок [HistoryModel]
     */
    fun setData(list: List<HistoryModel>) {
        data = list
        notifyDataSetChanged()
    }


}