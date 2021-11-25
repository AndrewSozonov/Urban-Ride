package com.andrewsozonov.urbanride.data.database

import android.graphics.Bitmap
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.andrewsozonov.urbanride.models.data.RideDBModel
import com.andrewsozonov.urbanride.models.presentation.service.LocationPoint
import com.andrewsozonov.urbanride.util.AndroidTestConstants.AVG_SPEED_M_S
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DISTANCE1_METERS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DISTANCE2_METERS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DISTANCE3_METERS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DISTANCE4_METERS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DISTANCE_METERS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.DURATION_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.FINISH_TIME_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.ID
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LAT1
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LAT2
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LAT3
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LAT4
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LONG1
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LONG2
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LONG3
import com.andrewsozonov.urbanride.util.AndroidTestConstants.LONG4
import com.andrewsozonov.urbanride.util.AndroidTestConstants.SPEED1_M_S
import com.andrewsozonov.urbanride.util.AndroidTestConstants.SPEED2_M_S
import com.andrewsozonov.urbanride.util.AndroidTestConstants.SPEED3_M_S
import com.andrewsozonov.urbanride.util.AndroidTestConstants.SPEED4_M_S
import com.andrewsozonov.urbanride.util.AndroidTestConstants.START_TIME_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.TIME1_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.TIME2_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.TIME3_MS
import com.andrewsozonov.urbanride.util.AndroidTestConstants.TIME4_MS
import com.google.common.truth.Truth
import io.mockk.MockKAnnotations
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class RideDaoTest {

    private lateinit var database: RidingDatabase
    private lateinit var dao: RideDao

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RidingDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.getRideDAO()
    }

    @Test
    fun addRide() {
        val rideDBModel = createRideDBModel()
        dao.addRide(rideDBModel)
        val result = dao.getRideByID(1)
        Truth.assertThat(result.trackingPoints).isEqualTo(rideDBModel.trackingPoints)
    }

    @Test
    fun deleteRideById() {
        val rideDBModel = createRideDBModel()
        rideDBModel.id = ID
        dao.addRide(rideDBModel)
        dao.deleteRide(ID)
        val result = dao.getAllRides()
        Truth.assertThat(result).isEmpty()
    }

    @After
    fun teardown() {
        database.close()
    }

    private fun createRideDBModel(): RideDBModel {
        val trackingPoints: MutableList<MutableList<LocationPoint>> = mutableListOf(
            mutableListOf(
                LocationPoint(LAT1, LONG1, SPEED1_M_S, TIME1_MS, DISTANCE1_METERS),
                LocationPoint(LAT2, LONG2, SPEED2_M_S, TIME2_MS, DISTANCE2_METERS),
            ), mutableListOf(
                LocationPoint(LAT3, LONG3, SPEED3_M_S, TIME3_MS, DISTANCE3_METERS),
                LocationPoint(LAT4, LONG4, SPEED4_M_S, TIME4_MS, DISTANCE4_METERS),
            )
        )

        val mapImage: Bitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)

        return RideDBModel(
            START_TIME_MS,
            FINISH_TIME_MS,
            DURATION_MS,
            DISTANCE_METERS,
            AVG_SPEED_M_S, mapImage, trackingPoints
        )
    }
}