package com.ovehbe.junkboy.ui.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import com.ovehbe.junkboy.utils.SmsAppManager
import com.ovehbe.junkboy.utils.OtpHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

data class SmsConversation(
    val threadId: Long,
    val address: String,
    val displayName: String,
    val snippet: String,
    val messageCount: Int,
    val lastMessageDate: Date,
    val isUnread: Boolean
)

data class SmsMessage(
    val id: Long,
    val address: String,
    val body: String,
    val date: Date,
    val type: Int, // 1 = received, 2 = sent
    val read: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsScreen() {
    val context = LocalContext.current
    val smsAppManager = remember { SmsAppManager(context) }
    val otpHelper = remember { OtpHelper(context) }
    val serviceScope = remember { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
    
    var conversations by remember { mutableStateOf<List<SmsConversation>>(emptyList()) }
    var selectedConversation by remember { mutableStateOf<SmsConversation?>(null) }
    var conversationMessages by remember { mutableStateOf<List<SmsMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showComposeDialog by remember { mutableStateOf(false) }
    
    val isDefaultSmsApp = smsAppManager.isJunkboyDefaultSmsApp()
    
    // Load conversations
    LaunchedEffect(Unit) {
        if (isDefaultSmsApp) {
            loadConversations(context.contentResolver) { result ->
                conversations = result
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }
    
    // Load messages for selected conversation
    LaunchedEffect(selectedConversation) {
        selectedConversation?.let { conversation ->
            loadConversationMessages(context.contentResolver, conversation.threadId) { messages ->
                conversationMessages = messages
                // Check for OTPs in messages
                messages.forEach { message ->
                    if (message.type == 1) { // Received messages
                        otpHelper.detectAndCopyOtp(message.body, "SMS")
                    }
                }
            }
        }
    }
    
    // Compose new message dialog
    if (showComposeDialog) {
        ComposeMessageDialog(
            onDismiss = { showComposeDialog = false },
            onSendMessage = { recipient, message ->
                serviceScope.launch {
                    sendSms(recipient, message)
                    showComposeDialog = false
                    // Refresh conversations
                    loadConversations(context.contentResolver) { result ->
                        conversations = result
                    }
                }
            }
        )
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        TopAppBar(
            title = { 
                Text(
                    text = if (selectedConversation != null) {
                        selectedConversation!!.displayName
                    } else {
                        "SMS"
                    }
                )
            },
            navigationIcon = {
                if (selectedConversation != null) {
                    IconButton(onClick = { selectedConversation = null }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            },
            actions = {
                if (selectedConversation == null && isDefaultSmsApp) {
                    IconButton(onClick = { showComposeDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "New Message")
                    }
                }
            }
        )
        
        // Content
        when {
            !isDefaultSmsApp -> {
                NotDefaultSmsAppState(
                    onMakeDefault = { smsAppManager.requestDefaultSmsApp() }
                )
            }
            error != null -> {
                ErrorState(error = error!!) {
                    error = null
                    isLoading = true
                    serviceScope.launch {
                        loadConversations(context.contentResolver) { result ->
                            conversations = result
                            isLoading = false
                        }
                    }
                }
            }
            isLoading -> {
                LoadingState()
            }
            selectedConversation != null -> {
                ConversationView(
                    conversation = selectedConversation!!,
                    messages = conversationMessages,
                    onSendMessage = { message ->
                        serviceScope.launch {
                            sendSms(selectedConversation!!.address, message)
                            // Refresh messages
                            loadConversationMessages(context.contentResolver, selectedConversation!!.threadId) { messages ->
                                conversationMessages = messages
                            }
                        }
                    }
                )
            }
            conversations.isEmpty() -> {
                EmptySmsState()
            }
            else -> {
                ConversationsList(
                    conversations = conversations,
                    onConversationClick = { conversation ->
                        selectedConversation = conversation
                    }
                )
            }
        }
    }
}

@Composable
private fun NotDefaultSmsAppState(onMakeDefault: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Message,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Set as Default SMS App",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "To use SMS functionality, set Junkboy as your default SMS app. This enables full SMS features including sending and receiving messages.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onMakeDefault,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PhoneAndroid, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Set as Default SMS App")
        }
    }
}

@Composable
private fun ConversationsList(
    conversations: List<SmsConversation>,
    onConversationClick: (SmsConversation) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(conversations) { conversation ->
            ConversationItem(
                conversation = conversation,
                onClick = { onConversationClick(conversation) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationItem(
    conversation: SmsConversation,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (conversation.isUnread) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (conversation.isUnread) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.weight(1f)
                )
                
                Text(
                    text = formatTimestamp(conversation.lastMessageDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.snippet,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (conversation.isUnread) {
                    Badge(
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = conversation.messageCount.toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationView(
    conversation: SmsConversation,
    messages: List<SmsMessage>,
    onSendMessage: (String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message)
            }
        }
        
        // Message input
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...") },
                    modifier = Modifier.weight(1f),
                    maxLines = 4
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            onSendMessage(messageText)
                            messageText = ""
                        }
                    },
                    enabled = messageText.isNotBlank()
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: SmsMessage) {
    val isReceived = message.type == 1
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isReceived) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(
                containerColor = if (isReceived) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isReceived) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onPrimary
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = formatTimestamp(message.date),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isReceived) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ComposeMessageDialog(
    onDismiss: () -> Unit,
    onSendMessage: (String, String) -> Unit
) {
    var recipient by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Message") },
        text = {
            Column {
                OutlinedTextField(
                    value = recipient,
                    onValueChange = { recipient = it },
                    label = { Text("Recipient") },
                    placeholder = { Text("Phone number or contact name") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message") },
                    placeholder = { Text("Type your message...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (recipient.isNotBlank() && message.isNotBlank()) {
                        onSendMessage(recipient, message)
                    }
                },
                enabled = recipient.isNotBlank() && message.isNotBlank()
            ) {
                Text("Send")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmptySmsState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Message,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No conversations yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Start a new conversation by tapping the + button",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(error: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Error loading messages",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

// Helper functions
private suspend fun loadConversations(
    contentResolver: ContentResolver,
    onResult: (List<SmsConversation>) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val conversations = mutableListOf<SmsConversation>()
        val uri = Telephony.Threads.CONTENT_URI
        val projection = arrayOf(
            Telephony.Threads._ID,
            Telephony.Threads.RECIPIENT_IDS,
            Telephony.Threads.MESSAGE_COUNT,
            Telephony.Threads.SNIPPET,
            Telephony.Threads.DATE,
            Telephony.Threads.READ
        )
        
        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${Telephony.Threads.DATE} DESC"
        )
        
        cursor?.use { c ->
            while (c.moveToNext()) {
                val threadId = c.getLong(c.getColumnIndexOrThrow(Telephony.Threads._ID))
                val recipientIds = c.getString(c.getColumnIndexOrThrow(Telephony.Threads.RECIPIENT_IDS))
                val messageCount = c.getInt(c.getColumnIndexOrThrow(Telephony.Threads.MESSAGE_COUNT))
                val snippet = c.getString(c.getColumnIndexOrThrow(Telephony.Threads.SNIPPET)) ?: ""
                val date = c.getLong(c.getColumnIndexOrThrow(Telephony.Threads.DATE))
                val read = c.getInt(c.getColumnIndexOrThrow(Telephony.Threads.READ)) == 1
                
                // Get recipient address
                val address = getAddressFromRecipientIds(contentResolver, recipientIds)
                val displayName = getContactName(contentResolver, address)
                
                conversations.add(
                    SmsConversation(
                        threadId = threadId,
                        address = address,
                        displayName = displayName,
                        snippet = snippet,
                        messageCount = messageCount,
                        lastMessageDate = Date(date),
                        isUnread = !read
                    )
                )
            }
        }
        
        withContext(Dispatchers.Main) {
            onResult(conversations)
        }
    } catch (e: Exception) {
        Log.e("SmsScreen", "Error loading conversations", e)
        withContext(Dispatchers.Main) {
            onResult(emptyList())
        }
    }
}

private suspend fun loadConversationMessages(
    contentResolver: ContentResolver,
    threadId: Long,
    onResult: (List<SmsMessage>) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val messages = mutableListOf<SmsMessage>()
        val uri = Telephony.Sms.CONTENT_URI
        val projection = arrayOf(
            Telephony.Sms._ID,
            Telephony.Sms.ADDRESS,
            Telephony.Sms.BODY,
            Telephony.Sms.DATE,
            Telephony.Sms.TYPE,
            Telephony.Sms.READ
        )
        
        val cursor = contentResolver.query(
            uri,
            projection,
            "${Telephony.Sms.THREAD_ID} = ?",
            arrayOf(threadId.toString()),
            "${Telephony.Sms.DATE} ASC"
        )
        
        cursor?.use { c ->
            while (c.moveToNext()) {
                val id = c.getLong(c.getColumnIndexOrThrow(Telephony.Sms._ID))
                val address = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = c.getLong(c.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val type = c.getInt(c.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                val read = c.getInt(c.getColumnIndexOrThrow(Telephony.Sms.READ)) == 1
                
                messages.add(
                    SmsMessage(
                        id = id,
                        address = address,
                        body = body,
                        date = Date(date),
                        type = type,
                        read = read
                    )
                )
            }
        }
        
        withContext(Dispatchers.Main) {
            onResult(messages)
        }
    } catch (e: Exception) {
        Log.e("SmsScreen", "Error loading messages", e)
        withContext(Dispatchers.Main) {
            onResult(emptyList())
        }
    }
}

private fun getAddressFromRecipientIds(contentResolver: ContentResolver, recipientIds: String): String {
    try {
        val uri = Uri.parse("content://mms-sms/canonical-addresses")
        val cursor = contentResolver.query(
            uri,
            arrayOf("address"),
            "_id = ?",
            arrayOf(recipientIds),
            null
        )
        
        cursor?.use { c ->
            if (c.moveToFirst()) {
                return c.getString(0)
            }
        }
    } catch (e: Exception) {
        Log.e("SmsScreen", "Error getting address from recipient IDs", e)
    }
    
    return recipientIds
}

private fun getContactName(contentResolver: ContentResolver, address: String): String {
    try {
        val uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address))
        val cursor = contentResolver.query(
            uri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )
        
        cursor?.use { c ->
            if (c.moveToFirst()) {
                return c.getString(0)
            }
        }
    } catch (e: Exception) {
        Log.e("SmsScreen", "Error getting contact name", e)
    }
    
    return address
}

private suspend fun sendSms(recipient: String, message: String) = withContext(Dispatchers.IO) {
    try {
        val smsManager = SmsManager.getDefault()
        
        // For long messages, divide into parts
        val parts = smsManager.divideMessage(message)
        
        if (parts.size == 1) {
            smsManager.sendTextMessage(recipient, null, message, null, null)
        } else {
            smsManager.sendMultipartTextMessage(recipient, null, parts, null, null)
        }
        
        Log.d("SmsScreen", "SMS sent to $recipient")
    } catch (e: Exception) {
        Log.e("SmsScreen", "Error sending SMS", e)
    }
}

private fun formatTimestamp(date: Date): String {
    val now = Date()
    val diff = now.time - date.time
    
    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
} 