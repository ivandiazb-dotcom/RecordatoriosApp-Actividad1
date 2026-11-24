package com.example.recordatoriosapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY due_at ASC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    fun observeById(id: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE completed = :done ORDER BY due_at ASC")
    fun observeByStatus(done: Boolean): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun findById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE sync_status = 'PENDING' ORDER BY updated_at ASC")
    suspend fun findPendingSync(): List<ReminderEntity>

    @Upsert
    suspend fun save(reminder: ReminderEntity)

    @Upsert
    suspend fun saveAll(reminders: List<ReminderEntity>)

    @Query(
        """
        UPDATE reminders
        SET completed = :completed,
            updated_at = :updatedAt,
            sync_status = 'PENDING'
        WHERE id = :id
        """
    )
    suspend fun updateCompleted(id: String, completed: Boolean, updatedAt: Long)

    @Delete
    suspend fun delete(reminder: ReminderEntity)
}