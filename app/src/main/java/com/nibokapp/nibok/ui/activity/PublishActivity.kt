package com.nibokapp.nibok.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.nibokapp.nibok.R
import com.nibokapp.nibok.ui.fragment.PublishFragment

/**
 * Activity hosting the publishing fragment
 */
class PublishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)

        // If restoring do not create overlapping fragments
        if (savedInstanceState != null) {
            return
        }

        val publishFragment = PublishFragment()

        // Pass eventual intent extras to the fragment
        publishFragment.arguments = intent.extras

        // Add the fragment to the container
        supportFragmentManager.beginTransaction()
                .add(R.id.publishFragmentContainer, publishFragment)
                .commit()
    }
}
