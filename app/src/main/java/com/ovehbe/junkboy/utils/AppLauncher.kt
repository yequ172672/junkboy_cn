package com.ovehbe.junkboy.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

/**
 * Utility class for launching messaging apps and opening specific conversations
 */
class AppLauncher(private val context: Context) {
    
    companion object {
        private const val TAG = "AppLauncher"
    }
    
    /**
     * Attempt to open a specific conversation in the messaging app
     * Falls back to opening the app if conversation opening fails
     */
    fun openConversation(appName: String, packageName: String, senderName: String): Boolean {
        try {
            // First try to open specific conversation
            val conversationIntent = createConversationIntent(appName, packageName, senderName)
            if (conversationIntent != null && canResolveIntent(conversationIntent)) {
                context.startActivity(conversationIntent)
                Log.d(TAG, "Opened conversation with $senderName in $appName")
                return true
            }
            
            // Fall back to opening the app
            return openApp(packageName, appName)
            
        } catch (e: Exception) {
            Log.e(TAG, "Error opening conversation in $appName", e)
            return openApp(packageName, appName)
        }
    }
    
    /**
     * Open the messaging app
     */
    fun openApp(packageName: String, appName: String): Boolean {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.d(TAG, "Opened $appName")
                return true
            } else {
                Log.w(TAG, "$appName is not installed")
                return false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening $appName", e)
            return false
        }
    }
    
    /**
     * Create intent to open specific conversation based on app
     */
    private fun createConversationIntent(appName: String, packageName: String, senderName: String): Intent? {
        return when (packageName) {
            "com.whatsapp" -> {
                // WhatsApp: Try to open chat with contact
                // Note: This requires the contact to be in contacts or recent chats
                createWhatsAppIntent(senderName)
            }
            
            "org.telegram.messenger" -> {
                // Telegram: Try to open specific user
                createTelegramIntent(senderName)
            }
            
            "org.thoughtcrime.securesms" -> {
                // Signal: Try to open conversation
                createSignalIntent(senderName)
            }
            
            "com.facebook.orca" -> {
                // Messenger: Try to open conversation
                createMessengerIntent(senderName)
            }
            
            "com.google.android.gm" -> {
                // Gmail: Try to open email thread
                createGmailIntent(senderName)
            }
            
            "com.microsoft.office.outlook" -> {
                // Outlook: Try to open email thread
                createOutlookIntent(senderName)
            }
            
            else -> {
                // Generic approach: just open the app
                null
            }
        }
    }
    
    private fun createWhatsAppIntent(senderName: String): Intent? {
        // WhatsApp doesn't have reliable deep linking for specific contacts
        // We can try the generic WhatsApp intent
        return Intent().apply {
            action = Intent.ACTION_VIEW
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    private fun createTelegramIntent(senderName: String): Intent? {
        // Telegram supports deep linking to users
        return try {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("tg://resolve?domain=$senderName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun createSignalIntent(senderName: String): Intent? {
        // Signal doesn't have public deep linking API
        return Intent().apply {
            action = Intent.ACTION_VIEW
            setPackage("org.thoughtcrime.securesms")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    private fun createMessengerIntent(senderName: String): Intent? {
        // Messenger has limited deep linking
        return Intent().apply {
            action = Intent.ACTION_VIEW
            setPackage("com.facebook.orca")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    private fun createGmailIntent(senderName: String): Intent? {
        // Gmail: Try to search for emails from sender
        return try {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("https://mail.google.com/mail/u/0/#search/from:$senderName")
                setPackage("com.google.android.gm")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun createOutlookIntent(senderName: String): Intent? {
        // Outlook: Try to open the app and search
        return Intent().apply {
            action = Intent.ACTION_VIEW
            setPackage("com.microsoft.office.outlook")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
    
    /**
     * Check if an intent can be resolved
     */
    private fun canResolveIntent(intent: Intent): Boolean {
        return try {
            val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            resolveInfo != null
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if an app is installed
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
} 