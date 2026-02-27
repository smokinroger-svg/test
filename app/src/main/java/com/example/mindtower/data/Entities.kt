package com.example.mindtower.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "tasks")
@Serializable
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String,
    val type: TaskType,
    val status: TaskStatus,
    val priority: Int? = null,
    val estimateMinutes: Int? = null,
    val periodicPreset: PeriodicPreset? = null,
    val periodicInterval: Int? = null,
    val nextDueAt: Long? = null,
    val lastDoneAt: Long? = null
)

@Serializable
enum class TaskType { QUICK, LONG, PERIODIC, UNASSESSED }
@Serializable
enum class TaskStatus { TODO, IN_PROGRESS, DONE, ARCHIVED }
@Serializable
enum class PeriodicPreset { DAILY, WEEKLY }

@Entity(tableName = "state_logs")
@Serializable
data class StateLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateEpochDay: Long,
    val fillDurationSeconds: Int,
    val energy: Int,
    val focus: Int,
    val stress: Int,
    val mood: Int,
    val freeText: String
)

@Entity(
    tableName = "emotions",
    foreignKeys = [ForeignKey(
        entity = StateLogEntity::class,
        parentColumns = ["id"],
        childColumns = ["stateLogId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("stateLogId")]
)
@Serializable
data class EmotionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stateLogId: Long,
    val name: String,
    val intensity: Int
)

@Entity(
    tableName = "state_log_task_snapshots",
    foreignKeys = [
        ForeignKey(entity = StateLogEntity::class, parentColumns = ["id"], childColumns = ["stateLogId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = TaskEntity::class, parentColumns = ["id"], childColumns = ["taskId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("stateLogId"), Index("taskId")]
)
@Serializable
data class StateLogTaskSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val stateLogId: Long,
    val taskId: Long,
    val status: TaskStatus,
    val comment: String
)

@Entity(tableName = "training_sessions")
@Serializable
data class TrainingSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long?,
    val startedAt: Long,
    val workSeconds: Int,
    val restSeconds: Int,
    val plannedSessions: Int,
    val completedSessions: Int,
    val progressionStepSeconds: Int,
    val progressionEveryNSessions: Int,
    val resultNote: String
)

@Entity(tableName = "refusal_logs")
@Serializable
data class RefusalLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val impulse: String,
    val urgeIntensity: Int,
    val delaySeconds: Int,
    val didResist: Boolean,
    val note: String,
    val createdAt: Long
)

@Entity(tableName = "walk_break_logs")
@Serializable
data class WalkBreakLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startedAt: Long,
    val durationSeconds: Int,
    val reminderText: String
)

@Entity(tableName = "tower_rooms")
@Serializable
data class TowerRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String
)

@Entity(
    tableName = "tower_notes",
    foreignKeys = [ForeignKey(entity = TowerRoomEntity::class, parentColumns = ["id"], childColumns = ["roomId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("roomId")]
)
@Serializable
data class TowerNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val title: String,
    val content: String
)

@Entity(
    tableName = "tower_projects",
    foreignKeys = [ForeignKey(entity = TowerRoomEntity::class, parentColumns = ["id"], childColumns = ["roomId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("roomId")]
)
@Serializable
data class TowerProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomId: Long,
    val title: String
)

@Entity(
    tableName = "tower_project_items",
    foreignKeys = [ForeignKey(entity = TowerProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("projectId")]
)
@Serializable
data class TowerProjectItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long,
    val value: String
)
