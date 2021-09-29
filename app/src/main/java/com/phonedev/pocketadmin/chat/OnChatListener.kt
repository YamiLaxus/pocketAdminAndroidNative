package com.phonedev.pocketadmin.chat

import com.phonedev.pocketadmin.entities.Message

interface OnChatListener {
    fun deleteMessage(message: Message)
}