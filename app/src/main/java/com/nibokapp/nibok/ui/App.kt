package com.nibokapp.nibok.ui

import android.app.Application
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.facebook.drawee.backends.pipeline.Fresco
import io.realm.Realm
import io.realm.RealmConfiguration
import org.jetbrains.anko.doAsync

/**
 * App.
 *
 * This class initializes the required configurations on application's start.
 *
 */
class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()

        // Save instance
        instance = this

        initRealmDb()
        initFresco()
        initBaasBox()

        // Test. logout user on start
        doAsync {
            BaasUser.current()?.logoutSync()
        }
    }

    /**
     * Initialize the local database.
     */
    private fun initRealmDb() {

        Realm.init(this)

        // Get Realm configuration
        val realmConfig = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded() // Delete the DB instead of migrating
                .build()

        // Delete realm on restart
        Realm.deleteRealm(realmConfig)

        // Set the Realm configuration
        Realm.setDefaultConfiguration(realmConfig)
    }

    /**
     * Initialize Fresco.
     * Used for the pictures gallery.
     */
    private fun initFresco() {
        Fresco.initialize(this)
    }

    /**
     * Initialize communications with the server.
     */
    private fun initBaasBox() {
        BaasBox.builder(this).setAuthentication(BaasBox.Config.AuthType.SESSION_TOKEN)
                .setApiDomain("10.0.3.2") // 10.0.2.2 for AVD; 10.0.3.2 for Genymotion
                .setPort(9000)
                .setAppCode("1234567890")
                .init()
    }
}