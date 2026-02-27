package com.example.mindtower.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Query("UPDATE tasks SET status = :status WHERE id = :taskId")
    suspend fun updateStatus(taskId: Long, status: TaskStatus)
}

@Dao
interface StateLogDao {
    @Query("SELECT * FROM state_logs ORDER BY dateEpochDay DESC")
    fun observeStateLogs(): Flow<List<StateLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stateLog: StateLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmotions(emotions: List<EmotionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskSnapshots(items: List<StateLogTaskSnapshotEntity>)
}

@Dao
interface TrainingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TrainingSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRefusal(log: RefusalLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWalkBreak(log: WalkBreakLogEntity)
}

@Dao
interface TowerDao {
    @Query("SELECT * FROM tower_rooms ORDER BY id")
    fun observeRooms(): Flow<List<TowerRoomEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoom(room: TowerRoomEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: TowerNoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: TowerProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectItems(items: List<TowerProjectItemEntity>)
}

@Dao
interface BackupDao {
    @Query("SELECT * FROM tasks") suspend fun tasks(): List<TaskEntity>
    @Query("SELECT * FROM state_logs") suspend fun logs(): List<StateLogEntity>
    @Query("SELECT * FROM emotions") suspend fun emotions(): List<EmotionEntity>
    @Query("SELECT * FROM state_log_task_snapshots") suspend fun snapshots(): List<StateLogTaskSnapshotEntity>
    @Query("SELECT * FROM training_sessions") suspend fun sessions(): List<TrainingSessionEntity>
    @Query("SELECT * FROM refusal_logs") suspend fun refusals(): List<RefusalLogEntity>
    @Query("SELECT * FROM walk_break_logs") suspend fun walks(): List<WalkBreakLogEntity>
    @Query("SELECT * FROM tower_rooms") suspend fun rooms(): List<TowerRoomEntity>
    @Query("SELECT * FROM tower_notes") suspend fun notes(): List<TowerNoteEntity>
    @Query("SELECT * FROM tower_projects") suspend fun projects(): List<TowerProjectEntity>
    @Query("SELECT * FROM tower_project_items") suspend fun projectItems(): List<TowerProjectItemEntity>

    @Query("DELETE FROM tasks") suspend fun clearTasks()
    @Query("DELETE FROM state_logs") suspend fun clearLogs()
    @Query("DELETE FROM training_sessions") suspend fun clearSessions()
    @Query("DELETE FROM refusal_logs") suspend fun clearRefusals()
    @Query("DELETE FROM walk_break_logs") suspend fun clearWalks()
    @Query("DELETE FROM tower_rooms") suspend fun clearRooms()

    @Insert suspend fun insertTasks(items: List<TaskEntity>)
    @Insert suspend fun insertLogs(items: List<StateLogEntity>)
    @Insert suspend fun insertEmotions(items: List<EmotionEntity>)
    @Insert suspend fun insertSnapshots(items: List<StateLogTaskSnapshotEntity>)
    @Insert suspend fun insertSessions(items: List<TrainingSessionEntity>)
    @Insert suspend fun insertRefusals(items: List<RefusalLogEntity>)
    @Insert suspend fun insertWalks(items: List<WalkBreakLogEntity>)
    @Insert suspend fun insertRooms(items: List<TowerRoomEntity>)
    @Insert suspend fun insertNotes(items: List<TowerNoteEntity>)
    @Insert suspend fun insertProjects(items: List<TowerProjectEntity>)
    @Insert suspend fun insertProjectItems(items: List<TowerProjectItemEntity>)
}
