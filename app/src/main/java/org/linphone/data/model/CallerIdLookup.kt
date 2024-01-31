package org.linphone.data.model

import com.google.gson.annotations.SerializedName

data class CallerIdLookup(
    @SerializedName("names")
    val names: List<String>
)
