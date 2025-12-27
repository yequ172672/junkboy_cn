package com.ovehbe.junkboy.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.ovehbe.junkboy.database.AppDatabase
import com.ovehbe.junkboy.database.ChatMessage
import com.ovehbe.junkboy.database.MessageCategory
import com.ovehbe.junkboy.utils.OtpHelper
import com.ovehbe.junkboy.utils.PreferencesManager
import com.ovehbe.junkboy.utils.SmsAppManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class NotificationListenerService : NotificationListenerService() {
    
    companion object {
        private const val TAG = "NotificationListener"
        
        // Supported messaging apps for Hub
        val SUPPORTED_APPS = mapOf(
            "com.whatsapp" to "WhatsApp",
            "org.telegram.messenger" to "Telegram",
            "org.thoughtcrime.securesms" to "Signal",
            "com.facebook.orca" to "Messenger",
            "com.instagram.android" to "Instagram",
            "com.google.android.gm" to "Gmail",
            "com.microsoft.office.outlook" to "Outlook",
            "com.android.email" to "Email",
            "com.samsung.android.email.provider" to "Samsung Email"
        )
        
        // Known SMS app package names
        val SMS_APP_PACKAGES = setOf(
            "com.google.android.apps.messaging",     // Google Messages
            "com.samsung.android.messaging",          // Samsung Messages
            "com.android.mms",                        // Stock Android MMS
            "com.sonyericsson.conversations",         // Sony Messages
            "com.htc.sense.mms",                      // HTC Messages
            "com.motorola.messaging",                 // Motorola Messages
            "com.oneplus.mms",                        // OnePlus Messages
            "com.asus.message",                       // ASUS Messages
            "com.lge.message",                        // LG Messages
            "com.huawei.message",                     // Huawei Messages
            "com.xiaomi.mipicks",                     // Xiaomi Messages
            "com.miui.smsextra",                      // MIUI Messages
            "org.smssecure.smssecure",               // SMS Secure
            "com.textra",                             // Textra
            "com.simplemobiletools.smsmessenger",    // Simple SMS Messenger
            "com.moez.QKSMS",                         // QKSMS
            "xyz.klinker.messenger",                  // Pulse SMS
            "com.p1.chompsms"                         // Chomp SMS
        )
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var database: AppDatabase
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var otpHelper: OtpHelper
    private lateinit var smsAppManager: SmsAppManager
    
    // Track recently processed SMS to avoid duplicates
    private val recentlyProcessedSms = mutableSetOf<String>()
    private val maxRecentSms = 50
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "NotificationListenerService created")
        
        // Initialize components
        database = AppDatabase.getDatabase(this)
        preferencesManager = PreferencesManager(this)
        otpHelper = OtpHelper(this)
        smsAppManager = SmsAppManager(this)
    }
    
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        
        val packageName = sbn.packageName
        
        // Check if this is from an SMS app
        if (isSmsAppNotification(packageName)) {
            handleSmsAppNotification(sbn)
            return
        }
        
        // Check if this is from a supported chat app
        val appName = SUPPORTED_APPS[packageName]
        if (appName != null) {
            if (preferencesManager.isHubNotificationsEnabled() && 
                preferencesManager.isAppEnabledForHub(packageName)) {
                Log.d(TAG, "Notification from supported app: $appName")
                processChatNotification(sbn, appName)
            }
        }
    }
    
    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        // Handle notification removal if needed
    }
    
    /**
     * Check if the notification is from a known SMS app
     */
    private fun isSmsAppNotification(packageName: String): Boolean {
        // Check against known SMS packages
        if (packageName in SMS_APP_PACKAGES) {
            return true
        }
        
        // Also check if it's the current default SMS app
        val defaultSmsPackage = smsAppManager.getDefaultSmsPackage()
        if (packageName == defaultSmsPackage && packageName != this.packageName) {
            return true
        }
        
        return false
    }
    
    /**
     * Handle notifications from SMS apps (dismiss/mute based on settings)
     */
    private fun handleSmsAppNotification(sbn: StatusBarNotification) {
        // Check if SMS app control is enabled
        if (!preferencesManager.isSmsAppControlEnabled()) {
            Log.d(TAG, "SMS app control disabled, ignoring notification from ${sbn.packageName}")
            return
        }
        
        val notification = sbn.notification
        val extras = notification.extras
        
        // Extract notification content for logging and matching
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
        
        Log.d(TAG, "SMS app notification detected from ${sbn.packageName}: $title - ${text.take(50)}")
        
        // Create a unique key for this SMS
        val smsKey = "${title}_${text.take(100)}_${sbn.postTime / 1000}" // Round to seconds
        
        // Avoid processing the same notification multiple times
        if (smsKey in recentlyProcessedSms) {
            Log.d(TAG, "Already processed this SMS notification, skipping")
            return
        }
        
        // Add to recently processed
        recentlyProcessedSms.add(smsKey)
        if (recentlyProcessedSms.size > maxRecentSms) {
            recentlyProcessedSms.remove(recentlyProcessedSms.first())
        }
        
        serviceScope.launch {
            try {
                // Check if we should dismiss all or only blocked
                val shouldDismiss = when {
                    preferencesManager.shouldDismissSmsAppNotifications() -> {
                        if (preferencesManager.shouldDismissBlockedOnly()) {
                            // Only dismiss if this message was blocked by Junkboy
                            isMessageBlocked(title, text)
                        } else {
                            // Dismiss all SMS app notifications
                            true
                        }
                    }
                    else -> false
                }
                
                if (shouldDismiss) {
                    // Small delay to ensure the notification is fully posted
                    delay(100)
                    
                    Log.i(TAG, "Dismissing SMS notification: $title")
                    cancelNotification(sbn.key)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error handling SMS app notification", e)
            }
        }
    }
    
    /**
     * Check if a message with this sender/content was blocked by Junkboy
     */
    private suspend fun isMessageBlocked(sender: String, content: String): Boolean {
        return try {
            // Look for a recently blocked message from this sender with similar content
            val recentMessages = database.filteredMessageDao().getBlockedMessagesLimited(20)
            var isBlocked = false
            
            recentMessages.collect { messages ->
                isBlocked = messages.any { msg ->
                    // Match by sender and partial content match
                    (msg.sender.contains(sender, ignoreCase = true) || 
                     sender.contains(msg.sender, ignoreCase = true)) &&
                    (msg.messageBody.take(50).contains(content.take(50), ignoreCase = true) ||
                     content.take(50).contains(msg.messageBody.take(50), ignoreCase = true))
                }
            }
            
            isBlocked
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if message is blocked", e)
            false
        }
    }
    
    private fun processChatNotification(sbn: StatusBarNotification, appName: String) {
        serviceScope.launch {
            try {
                val notification = sbn.notification
                val extras = notification.extras
                
                // Extract notification content
                val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: "Unknown"
                val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
                val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: text
                
                // Use bigText if available, otherwise use text
                val messageContent = if (bigText.isNotBlank()) bigText else text
                
                if (messageContent.isBlank()) {
                    Log.d(TAG, "No content in notification from $appName")
                    return@launch
                }
                
                // Filter out non-conversation notifications
                if (!isConversationMessage(title, messageContent, appName)) {
                    Log.d(TAG, "Skipping non-conversation notification from $appName: $title")
                    return@launch
                }
                
                Log.d(TAG, "Processing notification from $appName: $title - ${messageContent.take(50)}")
                
                // Apply filtering logic to the message
                val category = classifyMessage(messageContent, appName)
                
                // Create chat message record
                val chatMessage = ChatMessage(
                    appName = appName,
                    senderName = title,
                    messageContent = messageContent,
                    receivedAt = Date(sbn.postTime),
                    category = category,
                    packageName = sbn.packageName,
                    notificationId = sbn.id,
                    isRead = false
                )
                
                // Save to database
                database.chatMessageDao().insertChatMessage(chatMessage)
                
                // Detect and copy OTP if present
                otpHelper.detectAndCopyOtp(messageContent, appName)
                
                Log.d(TAG, "Processed chat notification from $appName, category: $category")
                
            } catch (e: Exception) {
                Log.e(TAG, "Error processing chat notification from $appName", e)
            }
        }
    }
    
    private fun classifyMessage(content: String, appName: String): MessageCategory {
        val lowerContent = content.lowercase()
        
        // Transaction indicators
        val transactionKeywords = listOf(
            "payment", "paid", "received", "sent", "transfer", "bank", "card",
            "ödeme", "para", "havale", "gönder", "al", "banka", "kart",
            "transaction", "purchase", "buy", "sold", "invoice", "receipt"
        )
        
        if (transactionKeywords.any { lowerContent.contains(it) }) {
            return MessageCategory.TRANSACTION
        }
        
        // Notification indicators (codes, alerts, etc.)
        val notificationKeywords = listOf(
            "code", "verify", "otp", "pin", "security", "alert", "reminder",
            "kod", "doğrula", "güvenlik", "uyarı", "hatırlatma",
            "delivery", "shipped", "arrived", "appointment", "meeting"
        )
        
        if (notificationKeywords.any { lowerContent.contains(it) }) {
            return MessageCategory.NOTIFICATION
        }
        
        // Promotion indicators
        val promotionKeywords = listOf(
            "offer", "discount", "sale", "deal", "promotion", "coupon",
            "teklif", "indirim", "satış", "kampanya", "kupon",
            "free", "win", "prize", "gift", "bonus", "special"
        )
        
        if (promotionKeywords.any { lowerContent.contains(it) }) {
            return MessageCategory.PROMOTION
        }
        
        // Junk indicators
        val junkKeywords = listOf(
            "spam", "scam", "fake", "suspicious", "click here", "urgent",
            "won", "prize", "lottery", "congratulations", "selected",
            "tıkla", "acil", "kazandın", "çekiliş", "seçildin"
        )
        
        if (junkKeywords.any { lowerContent.contains(it) }) {
            return MessageCategory.JUNK
        }
        
        // Default to general for chat messages
        return MessageCategory.GENERAL
    }
    
    /**
     * Filter out non-conversation messages like sync, summary, and service notifications
     */
    private fun isConversationMessage(title: String, content: String, appName: String): Boolean {
        val lowerTitle = title.lowercase()
        val lowerContent = content.lowercase()
        
        // Common patterns to exclude
        val excludePatterns = listOf(
            "syncing", "sync", "connecting", "connected", "disconnected",
            "backup", "restoring", "downloading", "uploading",
            "messages from", "chats", "new messages", "unread messages",
            "notifications", "missed calls", "voicemails",
            "running in background", "background", "service", "running",
            "active", "online", "offline", "status",
            "permission", "access", "settings", "updated", "install",
            "error", "failed", "retry", "trying",
            "broadcast", "announcement", "everyone",
            "senkronizasyon", "bağlanıyor", "yedekleme", "indiriliyor",
            "mesaj", "bildirim", "arayan", "çevrimdışı", "aktif"
        )
        
        val hasExcludePattern = excludePatterns.any { pattern ->
            lowerTitle.contains(pattern) || lowerContent.contains(pattern)
        }
        
        if (hasExcludePattern) {
            return false
        }
        
        // App-specific filtering
        when (appName) {
            "WhatsApp" -> {
                if (lowerTitle.contains("whatsapp web") || 
                    lowerContent.contains("web.whatsapp.com") ||
                    lowerContent.contains("backup") ||
                    lowerTitle.contains("backup")) {
                    return false
                }
                
                if (content.matches(Regex("\\d+\\s+(messages?|msgs?)\\s+from\\s+\\d+\\s+(chats?|contacts?)", RegexOption.IGNORE_CASE))) {
                    return false
                }
            }
            
            "Telegram" -> {
                if (lowerTitle.contains("telegram") && !lowerTitle.contains(":") ||
                    lowerContent.contains("joined telegram") ||
                    lowerContent.contains("telegram desktop")) {
                    return false
                }
            }
            
            "Gmail" -> {
                if (lowerTitle.contains("gmail") && !lowerTitle.contains("new email") ||
                    lowerContent.contains("sync") ||
                    lowerContent.contains("account")) {
                    return false
                }
            }
        }
        
        if (content.trim().length < 3) {
            return false
        }
        
        val hasPersonalIndicators = title.contains(":") || 
                                   content.length > 10 ||
                                   lowerContent.contains("you") ||
                                   lowerContent.contains("?") ||
                                   lowerContent.contains("!")
        
        return hasPersonalIndicators
    }
    
    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }
}
