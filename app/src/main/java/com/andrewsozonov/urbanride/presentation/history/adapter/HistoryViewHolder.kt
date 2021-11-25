package com.andrewsozonov.urbanride.presentation.history.adapter

import android.content.res.Resources
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.databinding.RecyclerItemHistoryBinding
import com.andrewsozonov.urbanride.models.presentation.history.HistoryModel
import com.andrewsozonov.urbanride.util.constants.UIConstants.GRAPH_PADDING
import com.bumptech.glide.Glide
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

/**
 * ViewHolder для [HistoryRecyclerAdapter]
 *
 * @author Андрей Созонов
 */
class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val binding = RecyclerItemHistoryBinding.bind(itemView)
    private val resources: Resources = itemView.resources

    /**
     * Биндит данные во ViewHolder
     *
     * @param ride модель данных поездки [HistoryModel]
     *
     */
    fun bind(ride: HistoryModel) {
        binding.dateTextView.text = ride.date
        binding.startTextView.text = ride.startTime
        binding.finishTextView.text = ride.finishTime
        binding.durationTextView.text = ride.duration

        if (ride.isUnitsMetric) {
            binding.distanceTextView.text =
                resources.getString(R.string.km, ride.distance.toString())
            binding.averageSpeedTextView.text =
                resources.getString(R.string.km_h, ride.averageSpeed.toString())
            binding.maxSpeedTextView.text =
                resources.getString(R.string.km_h, ride.maxSpeed.toString())
        } else {
            binding.distanceTextView.text =
                resources.getString(R.string.miles, ride.distance.toString())
            binding.averageSpeedTextView.text =
                resources.getString(R.string.miles_h, ride.averageSpeed.toString())
            binding.maxSpeedTextView.text =
                resources.getString(R.string.miles_h, ride.maxSpeed.toString())
        }

        itemView.setOnClickListener {
            if (binding.expandableRootItem.visibility == View.GONE) {
                expandItemView()
            } else {
                closeItemView()
                closeGraph()
            }
        }

        binding.showGraphButton.setOnClickListener {
            showGraph()
        }

        Glide
            .with(itemView.context)
            .load(ride.mapImg)
            .into(binding.historyMap)

        buildGraph(ride)
    }

    private fun expandItemView() {
        TransitionManager.beginDelayedTransition(binding.itemCardView, AutoTransition())
        binding.expandableRootItem.visibility = View.VISIBLE

        val theme: Resources.Theme = itemView.context.theme

        val typedValueColorSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorSurface, typedValueColorSurface, true)
        val cardViewColor = typedValueColorSurface.data

        val typedValueColorOnSurface = TypedValue()
        theme.resolveAttribute(R.attr.colorOnSurface, typedValueColorOnSurface, true)
        val textFieldColor = typedValueColorOnSurface.data
        binding.itemCardView.background.setTint(cardViewColor)

        binding.arrowButton.animate().rotationBy(180F).start()
        binding.dateFieldName.setTextColor(textFieldColor)
        binding.startFieldName.setTextColor(textFieldColor)
        binding.finishFieldName.setTextColor(textFieldColor)
        binding.durationFieldName.setTextColor(textFieldColor)
        binding.distanceFieldName.setTextColor(textFieldColor)
        binding.averageSpeedFieldName.setTextColor(textFieldColor)
        binding.maxSpeedFieldName.setTextColor(textFieldColor)
    }

    private fun closeItemView() {
        TransitionManager.beginDelayedTransition(binding.itemCardView, AutoTransition())
        binding.expandableRootItem.visibility = View.GONE
        binding.itemCardView.background.setTint(
            ContextCompat.getColor(
                itemView.context,
                R.color.light_cyan
            )
        )
        binding.arrowButton.animate().rotationBy(180.0F).start()
        binding.dateFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.startFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.finishFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.durationFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.distanceFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.averageSpeedFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
        binding.maxSpeedFieldName.setTextColor(
            ContextCompat.getColor(
                itemView.context,
                R.color.white
            )
        )
    }

    private fun showGraph() {
        if (binding.graphRootView.visibility == View.GONE) {
            TransitionManager.beginDelayedTransition(binding.itemCardView, AutoTransition())
            binding.graphRootView.visibility = View.VISIBLE
            binding.timeSpeedGraph.performClick()
        } else {
            closeGraph()
        }
    }

    private fun closeGraph() {
        TransitionManager.beginDelayedTransition(binding.itemCardView, AutoTransition())
        binding.graphRootView.visibility = View.GONE
    }

    private fun buildGraph(ride: HistoryModel) {

        binding.graph.gridLabelRenderer.padding = GRAPH_PADDING
        binding.graphRadioGroup.setOnCheckedChangeListener { _, checkedId ->

            val speedTimeSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
            val speedDistSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
            val speedTimeList: MutableList<DataPoint> = mutableListOf()
            val speedDistList: MutableList<DataPoint> = mutableListOf()

            ride.trackingPoints.map {
                it.map { point ->
                    speedTimeList.add(DataPoint(point.time, point.speed))
                    speedDistList.add(DataPoint(point.distance, point.speed))
                }
            }

            speedTimeSeries.resetData(speedTimeList.toTypedArray())
            speedDistSeries.resetData(speedDistList.toTypedArray())

            when (checkedId) {
                R.id.time_speed_graph -> {
                    binding.graph.gridLabelRenderer.horizontalAxisTitle =
                        itemView.context.getString(R.string.graph_time_axis)
                    if (ride.isUnitsMetric) {
                        binding.graph.gridLabelRenderer.verticalAxisTitle =
                            itemView.context.getString(R.string.graph_speed_axis_km)
                    } else {
                        binding.graph.gridLabelRenderer.verticalAxisTitle =
                            itemView.context.getString(R.string.graph_speed_axis_mile)
                    }
                    showGraphSeries(binding.graph, speedTimeSeries)
                }
                R.id.dist_speed_graph -> {
                    if (ride.isUnitsMetric) {
                        binding.graph.gridLabelRenderer.horizontalAxisTitle =
                            itemView.context.getString(R.string.graph_distance_axis_km)
                        binding.graph.gridLabelRenderer.verticalAxisTitle =
                            itemView.context.getString(R.string.graph_speed_axis_km)
                    } else {
                        binding.graph.gridLabelRenderer.horizontalAxisTitle =
                            itemView.context.getString(R.string.graph_distance_axis_mile)
                        binding.graph.gridLabelRenderer.verticalAxisTitle =
                            itemView.context.getString(R.string.graph_speed_axis_mile)
                    }
                    showGraphSeries(binding.graph, speedDistSeries)
                }
            }
        }
    }

    private fun showGraphSeries(graph: GraphView, series: LineGraphSeries<DataPoint>) {
        graph.removeAllSeries()
        graph.onDataChanged(false, false)
        graph.viewport.setMaxX(series.highestValueX * 1.1)
        graph.addSeries(series)
        graph.viewport.isScalable = true
        graph.viewport.isScrollable = true
    }
}