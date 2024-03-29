/*
 * Copyright (c) 2010-2020 Belledonne Communications SARL.
 *
 * This file is part of linphone-android
 * (see https://www.telnyx.com).
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
package org.linphone.activities.main.chat.data

import org.linphone.LinphoneApplication.Companion.coreContext
import org.linphone.R
import org.linphone.core.ChatRoom
import org.linphone.core.ParticipantDevice

class DevicesListChildData(private val device: ParticipantDevice) {
    val deviceName: String = device.name.orEmpty()

    val securityLevelIcon: Int by lazy {
        when (device.securityLevel) {
            ChatRoom.SecurityLevel.Safe -> R.drawable.security_2_indicator
            ChatRoom.SecurityLevel.Encrypted -> R.drawable.security_1_indicator
            else -> R.drawable.security_alert_indicator
        }
    }

    val securityContentDescription: Int by lazy {
        when (device.securityLevel) {
            ChatRoom.SecurityLevel.Safe -> R.string.content_description_security_level_safe
            ChatRoom.SecurityLevel.Encrypted -> R.string.content_description_security_level_encrypted
            else -> R.string.content_description_security_level_unsafe
        }
    }

    fun onClick() {
        coreContext.startCall(device.address, forceZRTP = true)
    }
}
