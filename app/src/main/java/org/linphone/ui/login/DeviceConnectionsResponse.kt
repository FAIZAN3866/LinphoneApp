package org.linphone.ui.login

import com.google.gson.annotations.SerializedName

data class DeviceConnectionsResponse(
    @SerializedName("data")
    val data: ArrayList<DeviceConnection>

)

data class DeviceConnection(
    @SerializedName("id")
    val id: String,

    @SerializedName("display_phone")
    val display_phone: String,
    @SerializedName("full_phone_number")
    val full_phone_number: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("phone_country_code")
    val phone_country_code: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("is_active")
    val is_active: String,
    @SerializedName("is_verified")
    val is_verified: String,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String

)
