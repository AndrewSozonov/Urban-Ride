package com.andrewsozonov.urbanride.presentation.history

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import com.andrewsozonov.urbanride.R
import com.andrewsozonov.urbanride.presentation.history.model.HistoryModel
import com.andrewsozonov.urbanride.util.constants.UIConstants.SHARE_IMAGE_STROKE_WIDTH
import com.andrewsozonov.urbanride.util.constants.UIConstants.SHARE_IMAGE_TEXT_HORIZONTAL_MARGIN
import com.andrewsozonov.urbanride.util.constants.UIConstants.SHARE_IMAGE_TEXT_SIZE
import com.andrewsozonov.urbanride.util.constants.UIConstants.SHARE_IMAGE_TEXT_VERTICAL_MARGIN

/**
 * Класс собирающий изображение для sharing
 *
 * @author Андрей Созонов
 */
class BitmapConstructor(val context: Context) {


    /**
     * Накладывает данные о текущей поездке на изображение с картой
     *
     * @param ride модель выбранной поездки [HistoryModel]
     * @return возвращает изображение с информацией
     */
    fun constructBitmapForSharing(ride: HistoryModel): Bitmap {
        val mapImage: Bitmap = ride.mapImg
        val shareMapImage = mapImage.copy(mapImage.config, true)

        val canvas = shareMapImage.let { Canvas(it) }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fillPaint.style = Paint.Style.FILL
        fillPaint.color = ContextCompat.getColor(context, R.color.middle_blue)
        fillPaint.textSize = SHARE_IMAGE_TEXT_SIZE

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = ContextCompat.getColor(context, R.color.white)
        strokePaint.strokeWidth = SHARE_IMAGE_STROKE_WIDTH
        strokePaint.textSize = SHARE_IMAGE_TEXT_SIZE
        fillPaint.setShadowLayer(2f, 0f, 1f, Color.BLACK)


        val horizontalMargin = SHARE_IMAGE_TEXT_HORIZONTAL_MARGIN
        val verticalMargin = SHARE_IMAGE_TEXT_VERTICAL_MARGIN
        val distanceTextBounds = Rect()
        val distance = context.resources.getString(R.string.distance_share_image, ride.distance.toString())
        fillPaint.getTextBounds(
            distance,
            0,
            ride.distance.toString().length,
            distanceTextBounds
        )
        var x: Float = horizontalMargin
        val y: Float = verticalMargin + distanceTextBounds.height()
        canvas.drawText(distance, x, y, fillPaint)
        canvas.drawText(distance, x, y, strokePaint)

        val timeTextBounds = Rect()
        val duration = context.resources.getString(R.string.duration_share_image, ride.duration)
        fillPaint.getTextBounds(duration, 0, duration.length, timeTextBounds)
        x = (shareMapImage.width - horizontalMargin - timeTextBounds.width())
        canvas.drawText(duration, x, y, fillPaint)
        canvas.drawText(duration, x, y, strokePaint)
        return shareMapImage
    }
}