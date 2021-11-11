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
import com.andrewsozonov.urbanride.util.Converter
import com.andrewsozonov.urbanride.util.DataFormatter
import com.bumptech.glide.Glide
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.*

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
    val graphRootView: FrameLayout = itemView.findViewById(R.id.graph_root_view)
    var mapImageView: ImageView = itemView.findViewById(R.id.history_map)
    var graph: GraphView = itemView.findViewById(R.id.graph)
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
            distanceTextView.text = resources.getString(R.string.miles, Converter.convertKilometersToMiles(ride.distance).toString())
            averageSpeedTextView.text = resources.getString(R.string.miles_h, Converter.convertKilometersToMiles(ride.averageSpeed).toString())
            maxSpeedTextView.text = resources.getString(R.string.miles_h, Converter.convertKilometersToMiles(ride.maxSpeed).toString())
        }
        Glide
            .with(itemView.context)
            .load(ride.mapImg)
            .into(mapImageView)

        buildGraph(ride)
    }

    private fun buildGraph(ride: Ride) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries()

        for (line in ride.trackingPoints) {
            for (point in line) {
                val x = (point.time / 1000).toDouble()
                val y = point.speed.toDouble()
                Log.d("buildGraph", " x: $x  y: $y")
                series.appendData(DataPoint(x, y), true, 100)
            }
        }

        graph.addSeries(series)
//        series.title = "time/speed"
//        graph.legendRenderer.isVisible = true
//        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        graph.gridLabelRenderer.horizontalAxisTitle = itemView.context.getString(R.string.graph_time_axis)
        graph.gridLabelRenderer.verticalAxisTitle = itemView.context.getString(R.string.graph_speed_axis)
        graph.viewport.isScalable = true
    }
}