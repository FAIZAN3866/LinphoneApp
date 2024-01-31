package org.linphone.ui.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import java.util.UUID
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.linphone.BuildConfig
import org.linphone.LinphoneApplication
import org.linphone.LinphoneApplication.Companion.corePreferences
import org.linphone.R
import org.linphone.activities.assistant.viewmodels.SharedAssistantViewModel
import org.linphone.activities.main.MainActivity
import org.linphone.core.TransportType
import org.linphone.core.tools.Log
import org.linphone.databinding.ActivityLoginBinding
import org.linphone.utils.PKCEUtil

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            val callbackURI = Uri.parse(intent.toString())
//            handleCallback(callbackURI)
        } else {
//            Toast.makeText(this, "Intent Null", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleCallback(uri: Uri) {
        val code = uri.getQueryParameter("code")

        if (code?.isEmpty() == false) {
//            Toast.makeText(this, code, Toast.LENGTH_SHORT).show()
            getToken(code)
        }
    }

    private fun getToken(code: String) {
        val client = OkHttpClient()
        /* Previously we sent the code challenge. Now we send the code verifier used
           to generate the code challenge.
        */
        val clientId = BuildConfig.CLIENT_ID
        val redirectUri = BuildConfig.REDIRECT_URI
        val baseUrl = BuildConfig.OAUTH_TOKEN_URL
        val grantType = "authorization_code"

        // Get the form body passing in the code verifier.
        val formBody: RequestBody = FormBody.Builder()
            .add("client_id", clientId)
            .add("redirect_uri", redirectUri)
            .add("grant_type", grantType)
            .add("code_verifier", PKCEUtil.getCodeVerifier()) // Send the code verifier
            .add("code", code)
            .build()
//        Log.e("return PKCEUtil.getCodeVerifier() ", PKCEUtil.getCodeVerifier())
//        Log.e("return PKCEUtil.code ", code)

        // Build the request ensuring the content type is set to `application/x-www-form-urlencoded`
        // and the form body
        val request = Request.Builder()
            .url(baseUrl)
            .post(formBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()

        // Perform the token request on a background thread.
        Thread {
            val response = client.newCall(request).execute()
            if (response.body != null) {
                val gson = Gson()
                // Marshal the response from the token endpoint.
                val tokenResponse = gson.fromJson(
                    response.body?.string(),
                    TokenResponse::class.java
                )
//                Log.e("return token", gson.toJson(response.body))
                Log.e("return token response ", tokenResponse)

                // Split the ID Token to get attempt to get the payload.
                val segments = tokenResponse.accessToken.split(".").toTypedArray()
//                Log.e("return token segments ", segments)

                // Create an intent for starting the `TokenActivity`.
                val intent = Intent(this, TokenActivity::class.java)
                // Verify the ID Token has the correct number of segments.
                if (segments.size == 3) {
//                    Log.e("return token segments size", segments)

                    // Extract the payload from ID Token segments and include in the intent.
                    val idToken = tokenResponse.accessToken
                    getAccessToken(idToken)
                    intent.putExtra(getString(R.string.payload), idToken)
                } else {
                    // Invalid number of segments so ignore the token as it is invalid.
                    intent.putExtra(getString(R.string.payload), "Invalid Payload")
                }

//                startActivity(intent)
            }
        }.start()
    }
    private fun getAccessToken(code: String) {
        val client = OkHttpClient()
        /* Previously we sent the code challenge. Now we send the code verifier used
           to generate the code challenge.
        */
        var deviceIdentifier = corePreferences.uuid
        if (deviceIdentifier.isNullOrEmpty()) {
            deviceIdentifier = UUID.randomUUID().toString()
            corePreferences.uuid = deviceIdentifier
        }
        val devicName = corePreferences.deviceName
//        Log.e("identifier", deviceIdentifier)
        val baseUrl = BuildConfig.API_URL + "v1/telnyx/sip-credentials?device_name=$devicName&device_identifier=ANDR $deviceIdentifier"

        // Build the request ensuring the content type is set to `application/x-www-form-urlencoded`
        // and the form body
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Bearer $code")
            .build()

        // Perform the token request on a background thread.
        Thread {
            val response = client.newCall(request).execute()
            if (response.body != null) {
                val gson = Gson()
                // Marshal the response from the token endpoint.
                val tokenResponse = gson.fromJson(
                    response.body?.string(),
                    SipCredentialsResponse::class.java
                )
//                Log.e("return sip", gson.toJson(response.body))
//                Log.e("return sip cred:  ", tokenResponse)
//                var sharedAssistantViewModel: SharedAssistantViewModel = this.run {
//                    ViewModelProvider(this)[SharedAssistantViewModel::class.java]
//                }
//                val accountCreator = sharedAssistantViewModel.getAccountCreator(true)
//                accountCreator.username = tokenResponse.username
//                accountCreator.password = tokenResponse.password
//                accountCreator.domain = corePreferences.defaultDomain
// //                accountCreator.displayName = "llllllll"
//                accountCreator.transport = TransportType.Tls
//
//                val account = accountCreator.createAccountInCore()
                corePreferences.accessToken = code
                getConnections(code, tokenResponse)

//                val intent = Intent(this, MainActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                startActivity(intent)
            }
        }.start()
    }
    private fun getConnections(code: String, tokenResponse: SipCredentialsResponse) {
        val client = OkHttpClient()
        /* Previously we sent the code challenge. Now we send the code verifier used
           to generate the code challenge.
        */
        val baseUrl = BuildConfig.API_URL + "v1/phone-connections"

        // Build the request ensuring the content type is set to `application/x-www-form-urlencoded`
        // and the form body
        val request = Request.Builder()
            .url(baseUrl)
            .get()
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Authorization", "Bearer $code")
            .build()

        // Perform the token request on a background thread.
        Thread {
            val response = client.newCall(request).execute()
            if (response.body != null) {
                val gson = Gson()
                // Marshal the response from the token endpoint.
                val deviceConnectionsResponse = gson.fromJson(
                    response.body?.string(),
                    DeviceConnectionsResponse::class.java
                )
                Log.e("connections list", response.body)
                Log.e("connections list 2 ", tokenResponse)
                var sharedAssistantViewModel: SharedAssistantViewModel = this.run {
                    ViewModelProvider(this)[SharedAssistantViewModel::class.java]
                }
                val account = LinphoneApplication.coreContext.core.accountList

                if (account.size <= 0) {
                    val accountCreator = sharedAssistantViewModel.getAccountCreator(false)
                    accountCreator.username = tokenResponse.username
                    accountCreator.password = tokenResponse.password
                    accountCreator.domain = corePreferences.defaultDomain
                    accountCreator.displayName = tokenResponse.username
                    accountCreator.transport = TransportType.Tls
                    accountCreator.setAsDefault(true)
                    accountCreator.displayName = deviceConnectionsResponse.data[0].full_phone_number

                    accountCreator.createAccountInCore()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                } else {
                    for (a in account) {
                        LinphoneApplication.coreContext.core.removeAccount(a)
                    }
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                    startActivity(intent)
                }
            }
        }.start()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val parametros = intent.dataString
        val callbackURI = Uri.parse(intent.dataString)
        handleCallback(callbackURI)
//        binding.loading.visibility = View.GONE

        if (parametros != null) {
            Log.e("return", parametros)
        }
//        val browserIntent = Intent(Intent.ACTION_VIEW, url)
//        startActivity(browserIntent)

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(
            this@LoginActivity,
            Observer {
                val loginState = it ?: return@Observer

                // disable login button unless both username / password is valid
//                login.isEnabled = loginState.isDataValid

//                if (loginFormState.usernameError != null) {
//                    username.error = getString(loginState.usernameError)
//                }
//                if (loginState.passwordError != null) {
//                    password.error = getString(loginState.passwordError)
//                }
            }
        )

        loginViewModel.loginResult.observe(
            this@LoginActivity,
            Observer {
                val loginResult = it ?: return@Observer

//                loading.visibility = View.GONE
//                if (loginResult.error != null) {
//                    showLoginFailed(loginResult.error)
//                }
//                if (loginResult.success != null) {
//                    updateUiWithUser(loginResult.success)
//                }
                setResult(Activity.RESULT_OK)

                // Complete and destroy login activity once successful
                finish()
            }
        )
//
//        username.afterTextChanged {
//            loginViewModel.loginDataChanged(
//                username.text.toString(),
//                password.text.toString()
//            )
//        }

//        password.apply {
//            afterTextChanged {
//                loginViewModel.loginDataChanged(
//                    username.text.toString(),
//                    password.text.toString()
//                )
//            }

//            setOnEditorActionListener { _, actionId, _ ->
//                when (actionId) {
//                    EditorInfo.IME_ACTION_DONE ->
//                        loginViewModel.login(
//                            username.text.toString(),
//                            password.text.toString()
//                        )
//                }
//                false
//            }

//            login.setOnClickListener {
//                loading.visibility = View.VISIBLE
//                loginViewModel.login(username.text.toString(), password.text.toString())
//            }
    }
}
