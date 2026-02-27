package com.example.mindtower.domain

import android.content.Context
import com.example.mindtower.data.AppDatabase
import com.example.mindtower.data.BackupDao
import com.example.mindtower.data.PeriodicPreset
import com.example.mindtower.data.RefusalLogEntity
import com.example.mindtower.data.StateLogEntity
import com.example.mindtower.data.TaskEntity
import com.example.mindtower.data.TaskStatus
import com.example.mindtower.data.TaskType
import com.example.mindtower.data.TowerRoomEntity
import com.example.mindtower.data.TrainingSessionEntity
import com.example.mindtower.data.WalkBreakLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import androidx.room.withTransaction

class AppRepository(private val db: AppDatabase) {
    val tasks: Flow<List<TaskEntity>> = db.taskDao().observeTasks()
    val stateLogs: Flow<List<StateLogEntity>> = db.stateLogDao().observeStateLogs()
    val rooms: Flow<List<TowerRoomEntity>> = db.towerDao().observeRooms()

    suspend fun addTask(title: String, type: TaskType) {
        db.taskDao().upsert(TaskEntity(title = title, notes = "", type = type, status = TaskStatus.TODO))
    }

    suspend fun markPeriodicDone(task: TaskEntity) {
        val now = System.currentTimeMillis()
        val interval = (task.periodicInterval ?: 1).coerceAtLeast(1)
        val millis = when (task.periodicPreset ?: PeriodicPreset.DAILY) {
            PeriodicPreset.DAILY -> interval * 24L * 60L * 60L * 1000L
            PeriodicPreset.WEEKLY -> interval * 7L * 24L * 60L * 60L * 1000L
        }
        db.taskDao().upsert(task.copy(lastDoneAt = now, nextDueAt = now + millis, status = TaskStatus.TODO))
    }

    suspend fun addStateLog(fillDurationSeconds: Int, freeText: String) {
        db.stateLogDao().insert(
            StateLogEntity(
                dateEpochDay = System.currentTimeMillis() / (24 * 60 * 60 * 1000),
                fillDurationSeconds = fillDurationSeconds,
                energy = 3,
                focus = 3,
                stress = 2,
                mood = 3,
                freeText = freeText
            )
        )
    }

    suspend fun addFocusSession(taskId: Long?) {
        db.trainingDao().insertSession(
            TrainingSessionEntity(
                taskId = taskId,
                startedAt = System.currentTimeMillis(),
                workSeconds = 1500,
                restSeconds = 300,
                plannedSessions = 4,
                completedSessions = 4,
                progressionStepSeconds = 60,
                progressionEveryNSessions = 3,
                resultNote = "Finished"
            )
        )
    }

    suspend fun addRefusalLog(impulse: String) {
        db.trainingDao().insertRefusal(
            RefusalLogEntity(impulse = impulse, urgeIntensity = 3, delaySeconds = 60, didResist = true, note = "", createdAt = System.currentTimeMillis())
        )
    }

    suspend fun addWalkBreak(seconds: Int) {
        db.trainingDao().insertWalkBreak(
            WalkBreakLogEntity(startedAt = System.currentTimeMillis(), durationSeconds = seconds, reminderText = "Walk + unload")
        )
    }

    suspend fun addRoom(title: String) {
        db.towerDao().insertRoom(TowerRoomEntity(title = title, description = ""))
    }

    suspend fun exportBackup(context: Context): File {
        val payload = BackupPayload.fromDao(db.backupDao())
        val output = File(context.filesDir, "mindtower_backup.json")
        output.writeText(Json { prettyPrint = true }.encodeToString(payload))
        return output
    }

    suspend fun importBackup(json: String) {
        val payload = Json.decodeFromString<BackupPayload>(json)
        val dao = db.backupDao()
        db.withTransaction {
            dao.clearTasks(); dao.clearLogs(); dao.clearSessions(); dao.clearRefusals(); dao.clearWalks(); dao.clearRooms()
            dao.insertTasks(payload.tasks)
            dao.insertLogs(payload.logs)
            dao.insertEmotions(payload.emotions)
            dao.insertSnapshots(payload.snapshots)
            dao.insertSessions(payload.sessions)
            dao.insertRefusals(payload.refusals)
            dao.insertWalks(payload.walks)
            dao.insertRooms(payload.rooms)
            dao.insertNotes(payload.notes)
            dao.insertProjects(payload.projects)
            dao.insertProjectItems(payload.projectItems)
        }
    }

    fun rawDbPath(context: Context): File = context.getDatabasePath("mind_tower.db")
}

@Serializable
data class BackupPayload(
    val tasks: List<TaskEntity>,
    val logs: List<StateLogEntity>,
    val emotions: List<com.example.mindtower.data.EmotionEntity>,
    val snapshots: List<com.example.mindtower.data.StateLogTaskSnapshotEntity>,
    val sessions: List<TrainingSessionEntity>,
    val refusals: List<RefusalLogEntity>,
    val walks: List<WalkBreakLogEntity>,
    val rooms: List<TowerRoomEntity>,
    val notes: List<com.example.mindtower.data.TowerNoteEntity>,
    val projects: List<com.example.mindtower.data.TowerProjectEntity>,
    val projectItems: List<com.example.mindtower.data.TowerProjectItemEntity>
) {
    companion object {
        suspend fun fromDao(dao: BackupDao): BackupPayload = BackupPayload(
            tasks = dao.tasks(),
            logs = dao.logs(),
            emotions = dao.emotions(),
            snapshots = dao.snapshots(),
            sessions = dao.sessions(),
            refusals = dao.refusals(),
            walks = dao.walks(),
            rooms = dao.rooms(),
            notes = dao.notes(),
            projects = dao.projects(),
            projectItems = dao.projectItems()
        )
    }
}
