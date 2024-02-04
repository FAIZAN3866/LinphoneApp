package org.linphone.data.model

import androidx.annotation.Keep

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Keep
data class LoggedInUser(
    val userId: String,
    val displayName: String
)
