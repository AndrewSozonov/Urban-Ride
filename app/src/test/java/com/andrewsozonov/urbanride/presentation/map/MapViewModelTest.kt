package com.andrewsozonov.urbanride.presentation.map

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.andrewsozonov.urbanride.domain.interactor.MapScreenInteractor
import com.andrewsozonov.urbanride.presentation.ride.model.RideModel
import com.andrewsozonov.urbanride.util.ISchedulersProvider
import com.andrewsozonov.urbanride.util.TestConstants
import com.andrewsozonov.urbanride.util.TestConstants.ID
import com.google.android.gms.maps.model.LatLng
import io.mockk.*
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MapViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val screenInteractor: MapScreenInteractor = mockk()
    private val schedulersProvider: ISchedulersProvider = mockk()
    private var isLoadingObserver: androidx.lifecycle.Observer<Boolean> = mockk()
    private var dataObserver: androidx.lifecycle.Observer<List<List<LatLng>>> = mockk()

    private var mapViewModel = MapViewModel(screenInteractor, schedulersProvider)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { schedulersProvider.io() } returns Schedulers.trampoline()
        every { schedulersProvider.ui() } returns Schedulers.trampoline()

        mapViewModel.isLoading.observeForever(isLoadingObserver)
        mapViewModel.trackingPoints.observeForever(dataObserver)

    }

    @Test
    fun `test getRide`() {
        every { screenInteractor.getRideById(ID) } returns createRideModel()
        every { dataObserver.onChanged(any()) } just Runs
        every { isLoadingObserver.onChanged(any()) } just Runs

        mapViewModel.getRide(ID)

        verifySequence {
            isLoadingObserver.onChanged(true)
            dataObserver.onChanged(createRideModel().trackingPoints)
            isLoadingObserver.onChanged(false)
        }
    }

    private fun createRideModel(): RideModel {
        val trackingPoints = mutableListOf(
            mutableListOf(
                LatLng(TestConstants.LAT1, TestConstants.LONG1),
                LatLng(
                    TestConstants.LAT2,
                    TestConstants.LONG2
                ),
            ), mutableListOf(
                LatLng(TestConstants.LAT3, TestConstants.LONG3),
                LatLng(
                    TestConstants.LAT4,
                    TestConstants.LONG4
                ),
            )
        )
        return RideModel(
            TestConstants.DISTANCE_KM,
            TestConstants.SPEED4_M_S,
            TestConstants.AVG_SPEED_KM_H, trackingPoints,
            true
        )
    }

}

