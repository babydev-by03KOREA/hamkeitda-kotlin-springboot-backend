package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import java.time.LocalTime

@Entity
@Table(
    name = "necessary_document",
    uniqueConstraints = [ UniqueConstraint(
        name = "uk_facility_document_name",
        columnNames = ["facility_id","document_name"]
    )],
    indexes = [ Index(name = "idx_necessary_document_facility", columnList = "facility_id") ]
)
class NecessaryDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** 소유자 쪽 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @Column(name = "document_name", nullable = false, length = 100)
    val documentName: String,

    @Column(name = "how_to_get", columnDefinition = "varchar(200)")
    val howToGet: String? = null,

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
        documentName = "",
        howToGet = null,
        sortOrder = 0
    )
}