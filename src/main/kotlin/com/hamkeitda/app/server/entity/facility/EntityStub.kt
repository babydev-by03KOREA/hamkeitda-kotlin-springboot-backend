package com.hamkeitda.app.server.entity.facility

import java.time.LocalTime

internal fun Facility.Companion.stub() = Facility(
    id = 0, name = "", openTime = LocalTime.MIDNIGHT, closedTime = LocalTime.MIDNIGHT,
    phoneNumber = "", address = "", description = ""
)

internal fun BBS.Companion.stub() = Bbs(id = 0, facility = Facility.stub(), title = "", content = "")