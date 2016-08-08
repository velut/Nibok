package com.nibokapp.nibok.ui

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val realmConfig = RealmConfiguration.Builder(this)
                .deleteRealmIfMigrationNeeded() // Delete the DB instead of migrating
                .build()
        Realm.setDefaultConfiguration(realmConfig)
    }
}