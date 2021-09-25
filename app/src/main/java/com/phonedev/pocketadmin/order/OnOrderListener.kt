package com.phonedev.pocketadmin.order

import com.phonedev.pocketadmin.entities.Order

interface OnOrderListener {

    fun onStartChat(order: Order)
    fun onStatusChange(order: Order)
}