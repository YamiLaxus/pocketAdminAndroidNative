package com.phonedev.pocketadmin.order

import com.phonedev.pocketadmin.entities.Order

interface OnOrderListener {
    fun onTrack(order: Order)
    fun onStartChat(order: Order)
}