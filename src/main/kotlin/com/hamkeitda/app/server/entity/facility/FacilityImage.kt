package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(
    name = "facility_image",
    indexes = [
        Index(name = "idx_facility_image_id", columnList = "facility_id"),
        Index(name = "idx_facility_created_at", columnList = "created_at DESC")
    ]
)
class FacilityImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @Column(nullable = false, length = 500)
    val url: String,

    @Column(nullable = false)
    var sortOrder: Int = 0,

    @Column(nullable = false)
    var isPrimary: Boolean = false,

    @Column(length = 200)
    var caption: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    protected constructor() : this(
        0, Facility(0, "", LocalTime.MIDNIGHT, LocalTime.MIDNIGHT, "", "", ""),
        url = "", sortOrder = 0, isPrimary = false, caption = null
    )
}