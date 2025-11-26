package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "bbs",
    indexes = [
        Index(name = "idx_bbs_facility", columnList = "facility_id"),
        Index(name = "idx_bbs_created_at", columnList = "created_at DESC")
    ]
)
class BBS(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** 소유자 쪽 (N:1) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    val facility: Facility,

    @Column(nullable = false, length = 150)
    val title: String,

    @Column(name = "content", nullable = false, columnDefinition = "text")
    val content: String,

    @Column(name = "is_pinned", nullable = false)
    var isPinned: Boolean = false,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(
        mappedBy = "bbs",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")
    val images: MutableList<BbsImage> = mutableListOf()
) {
    constructor() : this(
        id = 0, facility = Facility.stub(), title = "", content = ""
    )

    fun addImage(url: String, isPrimary: Boolean = false, caption: String? = null, sortOrder: Int? = null) {
        val order = sortOrder ?: ((this.images.maxOfOrNull { it.sortOrder } ?: -1) + 1)
        if (isPrimary) images.forEach { it.isPrimary = false }
        images += BbsImage(bbs = this, url = url, isPrimary = isPrimary, caption = caption, sortOrder = order)
    }

    companion object
}