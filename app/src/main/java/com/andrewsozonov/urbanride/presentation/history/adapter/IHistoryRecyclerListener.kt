package com.andrewsozonov.urbanride.presentation.history.adapter

interface IHistoryRecyclerListener {

    fun onMapClick(position: Int)

    fun onShareClick(position: Int)
}