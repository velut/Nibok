package com.nibokapp.nibok.ui

import android.app.Application
import com.nibokapp.nibok.data.repository.DbPopulator
import com.nibokapp.nibok.data.repository.UserManager
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

        // Get Realm configuration
        val realmConfig = RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded() // Delete the DB instead of migrating
                .build()

        Realm.deleteRealm(realmConfig) // Delete realm on restart

        // Set the Realm configuration
        Realm.setDefaultConfiguration(realmConfig)

        // Create the local user
        UserManager.createUser()

        // Populate the DB with test data
        DbPopulator().populateDb()

        // For testing
        UserManager.toggleSaveInsertion(1)
    }
}