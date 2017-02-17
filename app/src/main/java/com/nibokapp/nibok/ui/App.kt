package com.nibokapp.nibok.ui

import android.Manifest
import android.app.Application
import com.baasbox.android.BaasBox
import com.baasbox.android.BaasUser
import com.facebook.drawee.backends.pipeline.Fresco
import com.nibokapp.nibok.authentication.Authenticator
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

        /**
         * Server config.
         */
        // Localhost is 10.0.2.2 for AVD and 10.0.3.2 for Genymotion
        const val API_DOMAIN = "10.0.2.2"
        const val API_PORT = 9000
        const val API_BASE_URL = "http://$API_DOMAIN:$API_PORT/"

        const val APP_CODE = "1234567890"
        const val APP_CODE_REQUEST = "?X-BAASBOX-APPCODE=$APP_CODE"

        private const val PLACEHOLDER_IMAGE = "asset/book_picture_placeholder"
        const val PLACEHOLDER_IMAGE_URL = "$API_BASE_URL$PLACEHOLDER_IMAGE$APP_CODE_REQUEST"

        /**
         * Permissions.
         */
        const val PERMISSION_INTERNET = Manifest.permission.INTERNET
        const val REQUEST_PERMISSION_INTERNET = 1
        const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        const val REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2
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
            Authenticator.login("qwer", "ciaociao")
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
                .setApiDomain(API_DOMAIN)
                .setPort(API_PORT)
                .setAppCode(APP_CODE)
                .init()
    }
}