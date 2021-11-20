package com.andrewsozonov.urbanride.util

import com.google.common.truth.Truth
import org.junit.Assert.*
import org.junit.Test

class DataFormatterTest {

    @Test
    fun `test formatTime with zero`() {
        val time = 3600000L
        val expectedResult = "01:00:00"
        val result = DataFormatter.formatTime(time)
        Truth.assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `test formatTime without zero`() {
        val time = 39599000L
        val expectedResult = "10:59:59"
        val result = DataFormatter.formatTime(time)
        Truth.assertThat(result).isEqualTo(expectedResult)
    }
}