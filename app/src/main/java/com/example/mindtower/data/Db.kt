package com.example.mindtower.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Converters {
    @TypeConverter
    fun toTaskType(value: String): TaskType = TaskType.valueOf(value)

    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toStatus(value: String): TaskStatus = TaskStatus.valueOf(value)

    @TypeConverter
    fun fromStatus(value: TaskStatus): String = value.name

    @TypeConverter
    fun toPreset(value: String?): PeriodicPreset? = value?.let { PeriodicPreset.valueOf(it) }

    @TypeConverter
    fun fromPreset(value: PeriodicPreset?): String? = value?.name
}

@Database(
    entities = [
        TaskEntity::class,
        StateLogEntity::class,
        EmotionEntity::class,
        StateLogTaskSnapshotEntity::class,
        TrainingSessionEntity::class,
        RefusalLogEntity::class,
        WalkBreakLogEntity::class,
        TowerRoomEntity::class,
        TowerNoteEntity::class,
        TowerProjectEntity::class,
        TowerProjectItemEntity::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun stateLogDao(): StateLogDao
    abstract fun trainingDao(): TrainingDao
    abstract fun towerDao(): TowerDao
    abstract fun backupDao(): BackupDao

    companion object {
        val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN periodicPreset TEXT")
                db.execSQL("ALTER TABLE tasks ADD COLUMN periodicInterval INTEGER")
                db.execSQL("ALTER TABLE tasks ADD COLUMN nextDueAt INTEGER")
                db.execSQL("ALTER TABLE tasks ADD COLUMN lastDoneAt INTEGER")
            }
        }

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "mind_tower.db")
                .addMigrations(migration1To2)
                .fallbackToDestructiveMigrationOnDowngrade()
                .build()
        }
    }
}
