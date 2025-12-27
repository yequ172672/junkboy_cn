package com.ovehbe.junkboy.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val appName: String,
    val senderName: String,
    val messageContent: String,
    val receivedAt: Date,
    val category: MessageCategory,
    val packageName: String,
    val notificationId: Int,
    val isRead: Boolean = false
) 