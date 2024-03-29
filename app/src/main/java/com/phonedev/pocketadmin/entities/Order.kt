package com.phonedev.pocketadmin.entities

import com.google.firebase.firestore.Exclude

data class Order(
    @get:Exclude var id: String = "",
    val clientID: String = "",
    var products: Map<String, ProductOrder> = hashMapOf(),
    var totalPrice: Double = 0.0, var status: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Order

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
