package com.hamkeitda.app.server.entity.facility

import jakarta.persistence.*

@Entity
@Table(
    name = "bbs_image",
    indexes = [ Index(name = "idx_bbs_image_bbs", columnList = "bbs_id, sort_order") ]
)
class BbsImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bbs_id", nullable = false)
    val bbs: BBS,

    @Column(nullable = false, length = 500)
    val url: String,              // S3/CloudFront 등 CDN URL

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 0,

    @Column(name = "is_primary", nullable = false)
    var isPrimary: Boolean = false,

    @Column(length = 200)
    var caption: String? = null
) {
    protected constructor(): this(0, BBS.stub(), "", 0, false, null)
}