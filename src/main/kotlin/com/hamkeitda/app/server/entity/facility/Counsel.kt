package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.context.annotation.Description

@Entity
@Description(
    """
        일반 사용자가 상담신청을 하면 해당 Table에 기록되며,
        기관의 Alert Tab 에서 확인 가능합니다.
        (추후 FCM 등을 활용하여 Push Notification도 가능합니다.)
    """
)
@Table(name = "counsel")
class Counsel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** 소유자 쪽 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    val answers: String,                // 제출값(JSON)

    @Column(nullable = false)
    val applicantName: String,

    @Column(length = 50)
    val applicantPhone: String? = null,

    @Column(nullable = false)
    val schemaVersion: Int = 1
) {
    protected constructor() : this(
        id = 0, facility = Facility.stub(), answers = "", applicantName = "", applicantPhone = "", schemaVersion = 1
    )
}