package org.linphone.data.model

import com.google.gson.annotations.SerializedName

data class SipCredentials(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("device_identifier")
    val deviceIdentifier: String,
    @SerializedName("device_name")
    val deviceName: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("provider_reference")
    val providerReference: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("username")
    val username: String
)
