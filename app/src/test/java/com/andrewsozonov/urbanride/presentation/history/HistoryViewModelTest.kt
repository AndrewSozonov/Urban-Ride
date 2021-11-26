package com.andrewsozonov.urbanride.presentation.history

import android.graphics.Bitmap
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andrewsozonov.urbanride.domain.interactor.HistoryInteractor
import com.andrewsozonov.urbanride.models.presentation.history.HistoryLocationPoint
import com.andrewsozonov.urbanride.models.presentation.history.HistoryModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.andrewsozonov.urbanride.util.TestConstants.AVG_SPEED_KM_H_DB
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE1_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE2_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE3_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE4_KM
import com.andrewsozonov.urbanride.util.TestConstants.DISTANCE_KM_DB
import com.andrewsozonov.urbanride.util.TestConstants.DURATION_STRING
import com.andrewsozonov.urbanride.util.TestConstants.FINISH_TIME_STRING
import com.andrewsozonov.urbanride.util.TestConstants.ID
import com.andrewsozonov.urbanride.util.TestConstants.LAT1
import com.andrewsozonov.urbanride.util.TestConstants.LAT2
import com.andrewsozonov.urbanride.util.TestConstants.LAT3
import com.andrewsozonov.urbanride.util.TestConstants.LAT4
import com.andrewsozonov.urbanride.util.TestConstants.LONG1
import com.andrewsozonov.urbanride.util.TestConstants.LONG2
import com.andrewsozonov.urbanride.util.TestConstants.LONG3
import com.andrewsozonov.urbanride.util.TestConstants.LONG4
import com.andrewsozonov.urbanride.util.TestConstants.MAX_SPEED_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED1_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED2_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED3_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.SPEED4_KM_H
import com.andrewsozonov.urbanride.util.TestConstants.START_DATE_STRING
import com.andrewsozonov.urbanride.util.TestConstants.START_TIME_STRING
import com.andrewsozonov.urbanride.util.TestConstants.TIME1_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME2_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME3_MIN
import com.andrewsozonov.urbanride.util.TestConstants.TIME4_MIN
import io.mockk.*
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Тест класс для [HistoryViewModel]
 *
 * @author Андрей Созонов
 */
class HistoryViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val interactor: HistoryInteractor = mockk()
    private val schedulersProvider: ISchedulersProvider = mockk()
    private var isLoadingObserver: androidx.lifecycle.Observer<Boolean> = mockk()
    private var dataObserver: androidx.lifecycle.Observer<List<HistoryModel>> = mockk()
    private var historyViewModel = HistoryViewModel(interactor, schedulersProvider)
    private val mapImage: Bitmap = mockk()

    @Before
    fun setUp() {
        every { schedulersProvider.io() } returns Schedulers.trampoline()
        every { schedulersProvider.ui() } returns Schedulers.trampoline()

        historyViewModel.isLoading.observeForever(isLoadingObserver)
        historyViewModel.listOfRides.observeForever(dataObserver)

    }

    @Test
    fun `test getRidesFromDB`() {

        every { interactor.getAllRides() } returns createHistoryModel()
        every { dataObserver.onChanged(any()) } just Runs
        every { isLoadingObserver.onChanged(any()) } just Runs

        historyViewModel.getRidesFromDB()

        verifySequence {
            isLoadingObserver.onChanged(true)
            dataObserver.onChanged(createHistoryModel())
            isLoadingObserver.onChanged(false)

        }
    }

    @Test
    fun `test deleteRide`() {
        every { interactor.deleteRide(any()) } just Runs
        every { interactor.getAllRides() } returns createHistoryModel()
        every { dataObserver.onChanged(any()) } just Runs
        every { isLoadingObserver.onChanged(any()) } just Runs

        historyViewModel.deleteRide(ID)

        verifySequence {
            isLoadingObserver.onChanged(true)
            interactor.deleteRide(ID)
            interactor.getAllRides()
            dataObserver.onChanged(createHistoryModel())
            isLoadingObserver.onChanged(false)
        }
    }

    private fun createHistoryModel(): List<HistoryModel> {

        val historyTrackingPoints: MutableList<MutableList<HistoryLocationPoint>> =
            mutableListOf(
                mutableListOf(
                    HistoryLocationPoint(LAT1, LONG1, SPEED1_KM_H, TIME1_MIN, DISTANCE1_KM),
                    HistoryLocationPoint(LAT2, LONG2, SPEED2_KM_H, TIME2_MIN, DISTANCE2_KM)
                ), mutableListOf(
                    HistoryLocationPoint(LAT3, LONG3, SPEED3_KM_H, TIME3_MIN, DISTANCE3_KM),
                    HistoryLocationPoint(LAT4, LONG4, SPEED4_KM_H, TIME4_MIN, DISTANCE4_KM)
                )
            )

        val historyModel = HistoryModel(
            ID, START_DATE_STRING, START_TIME_STRING,
            FINISH_TIME_STRING,
            DURATION_STRING,
            DISTANCE_KM_DB,
            AVG_SPEED_KM_H_DB,
            MAX_SPEED_KM_H,
            mapImage,
            historyTrackingPoints,
            true
        )
        return mutableListOf(historyModel, historyModel)
    }
}