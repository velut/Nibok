package com.nibokapp.nibok.ui

import android.app.Application
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm
import io.realm.RealmConfiguration
import org.jetbrains.anko.doAsync
import kotlin.properties.Delegates

class App : Application() {

    companion object {
        var instance: App by Delegates.notNull<App>()
    }

    override fun onCreate() {
        super.onCreate()

        // Save instance
        instance = this

        // Initialize BaasBox
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("10.0.3.2") // 10.0.2.2 for AVD; 10.0.3.2 for Genymotion
                .setPort(9000)
                .setAppCode("1234567890")
                .init()

        doAsync {
            BaasUser.current()?.logoutSync()
        }

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
        //UserRepository.createLocalUser()

        // Populate the DB with test data
        //DbPopulator().populateDb()

        // For testing
        //BookInsertionRepository.toggleBookInsertionSaveStatus(2)
    }
}