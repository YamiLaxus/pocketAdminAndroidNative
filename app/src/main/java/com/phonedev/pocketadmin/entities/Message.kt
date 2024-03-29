package com.phonedev.pocketadmin.entities

import com.google.firebase.database.Exclude

data class Message(
    @get:Exclude var id: String = "",
    var message: String = "",
    var sender: String = "",
    @get: Exclude var myUId: String = ""){

    @Exclude
    fun isSendByMe(): Boolean = sender.equals(myUId)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}


