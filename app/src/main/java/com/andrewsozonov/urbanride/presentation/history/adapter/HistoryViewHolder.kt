package com.andrewsozonov.urbanride.presentation.history.adapter

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.database.Ride
import com.andrewsozonov.urbanride.util.DataFormatter
import com.bumptech.glide.Glide
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * ViewHolder для [HistoryRecyclerAdapter]
 *
 * @author Андрей Созонов
 */
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
    val showGraphButton: ImageButton = itemView.findViewById(R.id.show_graph_textView)
    val shareButton: ImageButton = itemView.findViewById(R.id.share_button)
    val graphRootView: LinearLayout = itemView.findViewById(R.id.graph_root_view)
    var mapImageView: ImageView = itemView.findViewById(R.id.history_map)
    var graph: GraphView = itemView.findViewById(R.id.graph)
    var radioGroup: RadioGroup = itemView.findViewById(R.id.graphRadioGroup)
    var timeSpeedRadioButton: RadioButton = itemView.findViewById(R.id.time_speed_graph)
    var distSpeedRadioButton: RadioButton = itemView.findViewById(R.id.dist_speed_graph)
    private val resources: Resources = itemView.resources

    /**
     * Биндит данные во ViewHolder
     *
     * @param ride модель данных поездки [Ride]
     * @param isUnitsMetric значение единиц измерения из Preferences
     * если значение true показывает данные в км, если false - в милях
     */
    fun bind(ride: Ride, isUnitsMetric: Boolean) {
        val dateFormat = SimpleDateFormat(itemView.context.getString(R.string.history_date_pattern), Locale.getDefault())
        val timeFormat = SimpleDateFormat(itemView.context.getString(R.string.history_time_pattern), Locale.getDefault())

        val startTime = Calendar.getInstance().apply {
            timeInMillis = ride.startTime
        }

        dateTextView.text = dateFormat.format(startTime.time)
        startTextView.text = timeFormat.format(startTime.time)


        val finishTime = Calendar.getInstance().apply {
            timeInMillis = ride.finishTime
        }
        finishTextView.text = timeFormat.format(finishTime.time)
        durationTextView.text = DataFormatter.formatTime(ride.duration)

        if (isUnitsMetric) {
            distanceTextView.text = resources.getString(R.string.km, ride.distance.toString())
            averageSpeedTextView.text = resources.getString(R.string.km_h, ride.averageSpeed.toString())
            maxSpeedTextView.text = resources.getString(R.string.km_h, ride.maxSpeed.toString())
        } else {
            distanceTextView.text = resources.getString(R.string.miles, DataFormatter.convertKilometersToMiles(ride.distance).toString())
            averageSpeedTextView.text = resources.getString(R.string.miles_h, DataFormatter.convertKilometersToMiles(ride.averageSpeed).toString())
            maxSpeedTextView.text = resources.getString(R.string.miles_h, DataFormatter.convertKilometersToMiles(ride.maxSpeed).toString())
        }
        Glide
            .with(itemView.context)
            .load(ride.mapImg)
            .into(mapImageView)

        buildGraph(ride)
    }

    private fun buildGraph(ride: Ride) {

        graph.gridLabelRenderer.padding = 20
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            val speedTimeSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
            val speedDistSeries: LineGraphSeries<DataPoint> = LineGraphSeries()
            val speedTimeList: MutableList<DataPoint> = mutableListOf()
            val speedDistList: MutableList<DataPoint> = mutableListOf()

            for (line in ride.trackingPoints) {
                for (point in line) {
                    val distance = point.distance.toDouble()
                    val time = DataFormatter.convertMillisecondsToMinutes(point.time)
                    val speed = point.speed.toDouble()
                    Log.d("buildGraph", " time: $time  speed: $speed")
                    speedTimeList.add(DataPoint(time, speed))
                    speedDistList.add(DataPoint(distance, speed))
                }
            }
            speedTimeSeries.resetData(speedTimeList.toTypedArray())
            speedDistSeries.resetData(speedDistList.toTypedArray())

            when (checkedId) {
                R.id.time_speed_graph -> {
                    graph.removeAllSeries()
                    graph.addSeries(speedTimeSeries)
                    graph.gridLabelRenderer.horizontalAxisTitle = itemView.context.getString(R.string.graph_time_axis)
                    graph.gridLabelRenderer.verticalAxisTitle = itemView.context.getString(R.string.graph_speed_axis)
                    graph.onDataChanged(false, false)
                    graph.viewport.isScalable = true
                }
                R.id.dist_speed_graph ->  {
                    graph.removeAllSeries()
                    graph.addSeries(speedDistSeries)
                    graph.gridLabelRenderer.horizontalAxisTitle = itemView.context.getString(R.string.graph_distance_axis)
                    graph.gridLabelRenderer.verticalAxisTitle = itemView.context.getString(R.string.graph_speed_axis)
                    graph.onDataChanged(false, false)
                    graph.viewport.isScalable = true

                }
            }
        }
    }
}