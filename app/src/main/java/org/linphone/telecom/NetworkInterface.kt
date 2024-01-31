package org.linphone.telecom

import org.linphone.data.model.CallerIdLookup
import org.linphone.data.model.PhoneConnections
import org.linphone.data.model.SipCredentials
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface NetworkInterface {

    @GET("/api/v1/telnyx/sip-credentials")
    suspend fun getSipCredentials(
        @Header("Accept") accept: String,
        @Header("Content-Type") contentType: String,
        @Header("Authorization") token: String,
        @Query("device_name") device_name: String,
        @Query("device_identifier") device_identifier: String
    ): Response<SipCredentials>

    @GET("/api/v1/phone-connections")
    suspend fun getPhoneConnections(
        @Header("Accept") accept: String,
        @Header("Content-Type") contentType: String,
        @Header("Authorization") token: String
    ): Response<PhoneConnections>

    @GET("/api/v1/phone-connections")
    fun getPhoneConnectionss(
        @Header("Accept") accept: String,
        @Header("Content-Type") contentType: String,
        @Header("Authorization") token: String
    ): Call<PhoneConnections>

    @GET("/api/v1/phone/contacts/caller-id-lookup/{id}")
    fun callerIdLookup(
        @Header("Accept") accept: String,
        @Header("Content-Type") contentType: String,
        @Header("Authorization") token: String,
        @Path(
            value = "id"

        )
        albumId: String

    ): Call<CallerIdLookup>
}
