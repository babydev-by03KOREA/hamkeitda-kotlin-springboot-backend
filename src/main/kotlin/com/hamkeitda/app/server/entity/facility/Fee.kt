package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(name = "fee")
class Fee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** 소유자 쪽 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, length = 20)
    val feeText: String, // "무료", "문의", "변동"

    /** 정렬/우선순위 (탭에서 순서 유지용) */
    @Column(nullable = false)
    var sortOrder: Int = 0
) {
    protected constructor() : this(
        id = 0,
        facility = Facility(
            id = 0, name = "", openTime = LocalTime.MIDNIGHT, closedTime = LocalTime.MIDNIGHT,
            phoneNumber = "", address = "", description = ""
        ),
        title = "",
        feeText = "",
        sortOrder = 0
    )
}