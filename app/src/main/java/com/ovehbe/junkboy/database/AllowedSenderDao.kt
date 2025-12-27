package com.ovehbe.junkboy.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AllowedSenderDao {
    
    @Query("SELECT * FROM allowed_senders WHERE isActive = 1 ORDER BY addedAt DESC")
    fun getAllowedSenders(): Flow<List<AllowedSender>>
    
    @Query("SELECT * FROM allowed_senders WHERE isActive = 1")
    suspend fun getAllowedSendersList(): List<AllowedSender>
    
    @Query("SELECT * FROM allowed_senders WHERE phoneNumber = :phoneNumber AND isActive = 1")
    suspend fun getAllowedSender(phoneNumber: String): AllowedSender?
    
    // Exact match - use isAllowedSenderFlexible() for better matching
    @Query("SELECT COUNT(*) > 0 FROM allowed_senders WHERE phoneNumber = :phoneNumber AND isActive = 1")
    suspend fun isAllowedSender(phoneNumber: String): Boolean
    
    // Case-insensitive match for text senders like "BANKNAME"
    @Query("SELECT COUNT(*) > 0 FROM allowed_senders WHERE LOWER(phoneNumber) = LOWER(:phoneNumber) AND isActive = 1")
    suspend fun isAllowedSenderCaseInsensitive(phoneNumber: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllowedSender(allowedSender: AllowedSender): Long
    
    @Update
    suspend fun updateAllowedSender(allowedSender: AllowedSender)
    
    @Delete
    suspend fun deleteAllowedSender(allowedSender: AllowedSender)
    
    @Query("UPDATE allowed_senders SET isActive = 0 WHERE phoneNumber = :phoneNumber")
    suspend fun deactivateAllowedSender(phoneNumber: String)
    
    @Query("UPDATE allowed_senders SET isActive = 1 WHERE phoneNumber = :phoneNumber")
    suspend fun activateAllowedSender(phoneNumber: String)
    
    @Query("DELETE FROM allowed_senders WHERE phoneNumber = :phoneNumber")
    suspend fun removeAllowedSender(phoneNumber: String)
} 