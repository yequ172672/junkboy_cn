package com.ovehbe.junkboy.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.core.app.NotificationCompat
import com.ovehbe.junkboy.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class OtpHelper(private val context: Context) {
    
    companion object {
        private const val TAG = "OtpHelper"
        private const val OTP_CHANNEL_ID = "otp_notifications"
        // Regex pattern for OTP detection (4-8 digits)
        private val OTP_PATTERN = Pattern.compile("\\b\\d{4,8}\\b")
        
        // Common OTP context keywords to increase detection accuracy
        private val OTP_CONTEXT_KEYWORDS = listOf(
            "otp", "code", "verification", "verify", "authenticate", "login",
            "doğrulama", "kod", "güvenlik", "onay", "giriş", "aktivasyon",
            "pin", "password", "passcode", "token", "auth", "security",
            "confirm", "confirmation", "validate", "temporary", "one-time"
        )
    }
    
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val preferencesManager = PreferencesManager(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    init {
        createOtpNotificationChannel()
    }
    
    /**
     * Detect and copy OTP from message text
     * @param messageText The message content to scan for OTPs
     * @param sourceName The source of the message (e.g., "SMS", "WhatsApp", "Telegram")
     * @param snackbarHostState Optional snackbar host for showing feedback
     * @param coroutineScope Optional coroutine scope for snackbar
     * @return The detected OTP string, or null if none found
     */
    fun detectAndCopyOtp(
        messageText: String,
        sourceName: String,
        snackbarHostState: SnackbarHostState? = null,
        coroutineScope: CoroutineScope? = null
    ): String? {
        // Check if OTP auto-copy is enabled
        if (!preferencesManager.isOtpAutoCopyEnabled()) {
            return null
        }
        
        val detectedOtp = detectOtp(messageText)
        
        if (detectedOtp != null) {
            Log.d(TAG, "OTP detected: $detectedOtp from $sourceName")
            
            // Copy to clipboard
            copyToClipboard(detectedOtp, sourceName)
            
            // Show feedback
            if (snackbarHostState != null && coroutineScope != null) {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Copied OTP $detectedOtp from $sourceName",
                        duration = SnackbarDuration.Short
                    )
                }
            } else {
                // Show notification for background OTP copying
                showOtpCopiedNotification(detectedOtp, sourceName)
            }
            
            // Update statistics
            preferencesManager.incrementOtpCopyCount()
            
            return detectedOtp
        }
        
        return null
    }
    
    /**
     * Detect OTP from message text using pattern matching and context analysis
     */
    private fun detectOtp(messageText: String): String? {
        val matcher = OTP_PATTERN.matcher(messageText)
        val candidates = mutableListOf<String>()
        
        // Find all potential OTP matches
        while (matcher.find()) {
            candidates.add(matcher.group())
        }
        
        if (candidates.isEmpty()) {
            return null
        }
        
        // If only one candidate, return it
        if (candidates.size == 1) {
            return candidates.first()
        }
        
        // Multiple candidates - use context analysis
        val lowerText = messageText.lowercase()
        
        // Check if message contains OTP context keywords
        val hasOtpContext = OTP_CONTEXT_KEYWORDS.any { keyword ->
            lowerText.contains(keyword.lowercase())
        }
        
        if (hasOtpContext) {
            // For messages with OTP context, prefer:
            // 1. 6-digit codes (most common)
            // 2. 4-digit codes  
            // 3. 5-digit codes
            // 4. 7-8 digit codes
            
            val prioritizedCandidates = candidates.sortedWith(compareBy<String> {
                when (it.length) {
                    6 -> 0
                    4 -> 1
                    5 -> 2
                    7 -> 3
                    8 -> 4
                    else -> 5
                }
            })
            
            return prioritizedCandidates.first()
        }
        
        // No OTP context - be more conservative
        // Only return 6-digit codes as they're most likely to be OTPs
        val sixDigitCodes = candidates.filter { it.length == 6 }
        if (sixDigitCodes.isNotEmpty()) {
            return sixDigitCodes.first()
        }
        
        // No clear OTP found
        return null
    }
    
    /**
     * Copy OTP to clipboard
     */
    private fun copyToClipboard(otp: String, sourceName: String) {
        try {
            val clipData = ClipData.newPlainText("OTP from $sourceName", otp)
            clipboardManager.setPrimaryClip(clipData)
            Log.d(TAG, "OTP copied to clipboard: $otp")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to copy OTP to clipboard", e)
        }
    }
    
    /**
     * Test OTP detection (for debugging/testing)
     */
    fun testOtpDetection(messageText: String): String? {
        return detectOtp(messageText)
    }
    
    /**
     * Create notification channel for OTP notifications
     */
    private fun createOtpNotificationChannel() {
        val channel = NotificationChannel(
            OTP_CHANNEL_ID,
            "OTP Notifications",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifications when OTP codes are automatically copied"
            enableVibration(false)
            setShowBadge(false)
            setSound(null, null)
        }
        
        notificationManager.createNotificationChannel(channel)
    }
    
    /**
     * Show notification when OTP is copied in background
     */
    private fun showOtpCopiedNotification(otp: String, sourceName: String) {
        try {
            val notification = NotificationCompat.Builder(context, OTP_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check)
                .setContentTitle("OTP Copied")
                .setContentText("Copied OTP $otp from $sourceName")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setTimeoutAfter(2000) // Auto-dismiss after 2 seconds
                .setSilent(true)
                .build()
            
            notificationManager.notify(
                System.currentTimeMillis().toInt(), // Unique ID
                notification
            )
            
            Log.d(TAG, "Showed OTP notification for: $otp from $sourceName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show OTP notification", e)
        }
    }
} 