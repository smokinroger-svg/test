package com.example.mindtower

import android.app.Application
import com.example.mindtower.data.AppDatabase
import com.example.mindtower.domain.AppRepository
import com.example.mindtower.workers.ReminderScheduler

class MindTowerApp : Application() {
    lateinit var repository: AppRepository

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.create(this)
        repository = AppRepository(db)
        ReminderScheduler.schedule(this)
    }
}
