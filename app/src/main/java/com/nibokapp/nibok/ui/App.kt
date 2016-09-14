package com.nibokapp.nibok.ui

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.nibokapp.nibok.data.repository.BookInsertionRepository
import com.nibokapp.nibok.data.repository.DbPopulator
import com.nibokapp.nibok.data.repository.UserRepository
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        var instance: App by Delegates.notNull<App>()
    }

    override fun onCreate() {
        super.onCreate()

        // Save instance
        instance = this

        // Initialize Fresco
        Fresco.initialize(this)

        // Get Realm configuration
        val realmConfig = RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded() // Delete the DB instead of migrating
                .build()

        Realm.deleteRealm(realmConfig) // Delete realm on restart

        // Set the Realm configuration
        Realm.setDefaultConfiguration(realmConfig)

        // Create the local user
        UserRepository.createLocalUser()

        // Populate the DB with test data
        DbPopulator().populateDb()

        // For testing
        BookInsertionRepository.toggleBookInsertionSaveStatus(2)
    }
}