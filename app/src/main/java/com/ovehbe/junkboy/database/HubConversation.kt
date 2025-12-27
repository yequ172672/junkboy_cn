package com.ovehbe.junkboy.database

import java.util.Date

/**
 * Represents a grouped conversation in the Hub screen
 * Groups messages by app and sender for clean BlackBerry Hub-style display
 */
data class HubConversation(
    val appName: String,
    val packageName: String,
    val senderName: String,
    val lastMessage: String,
    val lastMessageDate: Date,
    val messageCount: Int,
    val unreadCount: Int,
    val category: MessageCategory,
    val messages: List<ChatMessage> = emptyList()
)

/**
 * Represents a grouped section by app in the Hub
 */
data class HubAppSection(
    val appName: String,
    val packageName: String,
    val conversations: List<HubConversation>,
    val totalUnread: Int
) 