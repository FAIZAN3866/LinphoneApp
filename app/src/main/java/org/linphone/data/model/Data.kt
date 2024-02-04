package org.linphone.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("display_phone")
    val displayPhone: String,
    @SerializedName("full_phone_number")
    val fullPhoneNumber: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_verified")
    val isVerified: Boolean,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("phone_country_code")
    val phoneCountryCode: String,
    @SerializedName("provider")
    val provider: String,
    @SerializedName("updated_at")
    val updatedAt: String
)
