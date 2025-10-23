package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(
    name = "program",
    indexes = [ Index(name = "idx_program_facility", columnList = "facility_id") ]
)
class Program(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** 소유자 쪽 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @Column(name = "program_name", nullable = false, length = 100)
    val programName: String,

    @Column(name = "program_description", columnDefinition = "text")
    val programDescription: String? = null,

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
        programName = "",
        programDescription = null,
        sortOrder = 0
    )
}