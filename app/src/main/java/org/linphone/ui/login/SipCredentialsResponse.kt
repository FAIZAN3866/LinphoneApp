package org.linphone.ui.login

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SipCredentialsResponse(
    @SerializedName("device_identifier")
    val device_identifier: String,
    @SerializedName("device_name")
    val device_name: String,

    @SerializedName("provider")
    val provider: String,

    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("user_id")
    val user_id: String,
    @SerializedName("updated_at")
    val updated_at: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("id")
    val id: String
)
