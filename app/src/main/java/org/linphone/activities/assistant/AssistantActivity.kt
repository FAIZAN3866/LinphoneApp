/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.linphone.org).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.linphone.activities.assistant

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar
import java.net.URLEncoder
import java.util.UUID
import org.linphone.BuildConfig
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.activities.GenericActivity
import org.linphone.activities.SnackBarActivity
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.core.tools.Log
import org.linphone.utils.PKCEUtil

class AssistantActivity : GenericActivity(), SnackBarActivity {
    private lateinit var sharedViewModel: SharedAssistantViewModel
    private lateinit var coordinator: CoordinatorLayout
    private lateinit var webview: WebView
    lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.assistant_activity)

//        sharedViewModel = ViewModelProvider(this)[SharedAssistantViewModel::class.java]

        coordinator = findViewById(R.id.coordinator)
        webview = findViewById(R.id.webview)
        progress = findViewById(R.id.progress)

        corePreferences.firstStart = false
        callOauth()
    }
    private fun callOauth() {
        progress.visibility = View.VISIBLE
        var deviceIdentifier = corePreferences.uuid
        if (deviceIdentifier.isNullOrEmpty()) {
            deviceIdentifier = UUID.randomUUID().toString()
            corePreferences.uuid = deviceIdentifier
        }
        val devicName = corePreferences.deviceName

        val clientId = "9affe9e3-e2a8-48fc-bedf-fd5fcd3f5b15"
        val redirectUri = "com.insurance4truck.debug://redirect"

        fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")

        val authParams = mapOf(
            "client_id" to clientId,
            "redirect_uri" to redirectUri,
            "response_type" to "code",
            "code_challenge" to PKCEUtil.getCodeChallenge(), // Set the code challenge
            "code_challenge_method" to "S256"
        ).map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }.joinToString("&")

        val webView = findViewById<WebView>(R.id.webview)
        webView.getSettings().loadsImagesAutomatically = true
        webView.getSettings().setJavaScriptEnabled(true)
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY)
        webView.webViewClient = MyWebViewClient(this)

        webView.loadUrl("${BuildConfig.AUTHORIZE_ENDPOINT}?$authParams")

        // Initiate the OAuth 2.0 flow using CustomTabs<url>.
//        CustomTabsIntent.Builder().build().launchUrl(
//            this,
//            Uri.parse("${Config.AUTHORIZE_ENDPOINT}?$authParams")
//        )
    }

    override fun showSnackBar(@StringRes resourceId: Int) {
        Snackbar.make(coordinator, resourceId, Snackbar.LENGTH_LONG).show()
    }

    override fun showSnackBar(@StringRes resourceId: Int, action: Int, listener: () -> Unit) {
        Snackbar
            .make(findViewById(R.id.coordinator), resourceId, Snackbar.LENGTH_LONG)
            .setAction(action) {
                listener()
            }
            .show()
    }

    override fun showSnackBar(message: String) {
        Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show()
    }
    class MyWebViewClient internal constructor(private val activity: Activity) : WebViewClient() {

        var authComplete = false
        var resultIntent = Intent()

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            activity.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE

            val url = request?.url.toString()
            if (url.startsWith(BuildConfig.REDIRECT_URI)) {
                activity.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE

//            if (host == null) {
                activity.startActivity(Intent(Intent.ACTION_VIEW, request?.url))
                return true
            }

            view?.loadUrl(request?.url.toString())
            return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            activity.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE

            super.onPageStarted(view, url, favicon)
        }

        var authCode: String? = null
        override fun onPageFinished(view: WebView?, url: String) {
            super.onPageFinished(view, url)
            activity.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE

            if (url.contains("?code=") && authComplete != true) {
                val uri = Uri.parse(url)
                authCode = uri.getQueryParameter("code")
                Log.i("", "CODE : $authCode")
                authComplete = true
                resultIntent.putExtra("code", authCode)
                activity.setResult(RESULT_OK, resultIntent)
                activity.setResult(RESULT_CANCELED, resultIntent)

//                Toast.makeText(
//                    activity,
//                    "Authorization Code is: $authCode",
//                    Toast.LENGTH_SHORT
//                ).show()
            } else if (url.contains("error=access_denied")) {
                activity.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE

                Log.i("", "ACCESS_DENIED_HERE")
                resultIntent.putExtra("code", authCode)
                authComplete = true
                activity.setResult(RESULT_CANCELED, resultIntent)

                Toast.makeText(activity, "Error Occured", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
