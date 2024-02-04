package org.linphone.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CallerIdLookup(
    @SerializedName("names")
    val names: List<String>
)
