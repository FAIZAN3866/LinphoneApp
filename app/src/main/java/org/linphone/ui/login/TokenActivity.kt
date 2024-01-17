package org.linphone.ui.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.linphone.R
import org.linphone.databinding.ActivityTokenBinding

class TokenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTokenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_token)
        binding = ActivityTokenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.e("return tokenactivity", "token activity")

        // Get reference to the text view for displaying result.
//        val idTokenTextView = binding.idToken
        if (savedInstanceState == null) {
            // The payload is passed via intent. Verify it isn't empty and display it.
            val extra = intent.extras?.get(getString(R.string.payload)) as String
            if (extra.isEmpty()) {
//                idTokenTextView.setText("Failed to obtain token")
                return
            }
            Log.e("return tokenactivity", extra.toString())

//            idTokenTextView.setText(extra)
        } else {
            Log.e("return tokenactivity", "savedinstance null")
//
//            idTokenTextView.setText(
//                savedInstanceState.getSerializable(getString(R.string.payload)).toString()
//            )
        }
    }
}
