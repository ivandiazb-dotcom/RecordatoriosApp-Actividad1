package com.example.recordatoriosapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.UUID

enum class ReminderPriority {
    LOW,
    MEDIUM,
    HIGH
}

enum class SyncStatus {
    SYNCED,
    PENDING,
    PENDING_DELETE,
    ERROR
}

@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["remote_id"], unique = true)
    ]
)
data class ReminderEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "remote_id")
    val remoteId: String? = null,
    val title: String,
    val description: String? = null,
    val category: String = "General",
    val priority: ReminderPriority = ReminderPriority.MEDIUM,
    val completed: Boolean = false,
    @ColumnInfo(name = "due_at")
    val dueAt: Long? = null,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

class DatabaseConverters {
    @TypeConverter
    fun priorityToString(priority: ReminderPriority): String = priority.name

    @TypeConverter
    fun stringToPriority(value: String): ReminderPriority =
        runCatching { ReminderPriority.valueOf(value) }.getOrDefault(ReminderPriority.MEDIUM)

    @TypeConverter
    fun syncStatusToString(status: SyncStatus): String = status.name

    @TypeConverter
    fun stringToSyncStatus(value: String): SyncStatus =
        runCatching { SyncStatus.valueOf(value) }.getOrDefault(SyncStatus.PENDING)
}