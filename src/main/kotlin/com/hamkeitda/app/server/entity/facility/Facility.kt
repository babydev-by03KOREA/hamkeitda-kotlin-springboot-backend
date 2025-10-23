package com.hamkeitda.app.server.entity.facility

import com.hamkeitda.app.server.dto.facility.NecessaryDocumentCreate
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import java.time.LocalTime

@Entity
@Table(
    name = "facility",
    // 읽기 쿼리를 자주 수행하는 컬럼 조회 속도 향상 & SELECT, JOIN, ORDER BY, WHERE 조건 최적화
    indexes = [
        Index(name = "idx_facility_name", columnList = "name"),
        Index(name = "idx_facility_address", columnList = "address")
    ]
)
class Facility(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    val name: String,

    @Column(nullable = false)
    val openTime: LocalTime,

    @Column(nullable = false)
    val closedTime: LocalTime,

    @Column(nullable = false, length = 20)
    val phoneNumber: String,

    @Column(nullable = false, length = 200)
    val address: String,

    @Column(nullable = false, columnDefinition = "text")
    val description: String,

    // 시설 사진
    @OneToMany(mappedBy = "facility", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50)
    @OrderBy("sortOrder ASC, id ASC") // 정렬 보장
    val images: MutableList<FacilityImage> = mutableListOf(),

    // 필요 서류
    @OneToMany(
        mappedBy = "facility",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,            // 리스트에서 빠지면 DB에서도 삭제
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")   // 정렬 보장(아래 필드 기준)
    val necessaryDocuments: MutableList<NecessaryDocument> = mutableListOf(),

    // 프로그램
    @OneToMany(
        mappedBy = "facility",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,            // 리스트에서 빠지면 DB에서도 삭제
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")   // 정렬 보장(아래 필드 기준)
    val programs: MutableList<Program> = mutableListOf(),

    // 이용료
    @OneToMany(
        mappedBy = "facility",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,            // 리스트에서 빠지면 DB에서도 삭제
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")   // 정렬 보장(아래 필드 기준)
    val fees: MutableList<Fee> = mutableListOf(),

    // 게시판
    @OneToMany(
        mappedBy = "facility",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,            // 리스트에서 빠지면 DB에서도 삭제
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")   // 정렬 보장(아래 필드 기준)
    val bbsList: MutableList<BBS> = mutableListOf(),

    // 문의하기
    @OneToMany(
        mappedBy = "facility",
        cascade = [CascadeType.ALL],
        orphanRemoval = true,            // 리스트에서 빠지면 DB에서도 삭제
        fetch = FetchType.LAZY
    )
    @OrderBy("sortOrder ASC, id ASC")   // 정렬 보장(아래 필드 기준)
    val counsels: MutableList<Counsel> = mutableListOf(),
) {
    protected constructor() : this(
        id = 0, name = "", openTime = LocalTime.MIDNIGHT, closedTime = LocalTime.MIDNIGHT,
        phoneNumber = "", address = "", description = ""
    )

    fun addImage(url: String, isPrimary: Boolean = false, caption: String? = null, sortOrder: Int? = null) {
        val order = sortOrder ?: ((this.images.maxOfOrNull { it.sortOrder } ?: -1) + 1)
        val image = FacilityImage(
            facility = this,
            url = url,
            isPrimary = isPrimary,
            caption = caption,
            sortOrder = order
        )
        if (isPrimary) {
            images.forEach { it.isPrimary = false }
        }
        images += image
    }

    fun removeImage(imageId: Long) {
        images.removeIf { it.id == imageId }
    }

    fun setPrimary(imageId: Long) {
        images.forEach { it.isPrimary = it.id == imageId }
    }

    fun reorder(imageId: Long, newOrder: Int) {
        images.find { it.id == imageId }?.sortOrder = newOrder
        // 간단 정규화: 중복 order 정리
        images.sortedBy { it.sortOrder }.forEachIndexed { idx, img -> img.sortOrder = idx }
    }

    fun addDocument(name: String, howToGet: String? = null, sortOrder: Int? = null) {
        val order = sortOrder ?: ((this.necessaryDocuments.maxOfOrNull { it.sortOrder } ?: -1) + 1)
        necessaryDocuments += NecessaryDocument(
            facility = this,
            documentName = name,
            howToGet = howToGet,
            sortOrder = order
        )
    }

    fun removeDocument(docId: Long) {
        necessaryDocuments.removeIf { it.id == docId }
    }

    fun replaceAllDocuments(newDocs: List<NecessaryDocumentCreate>) {
        necessaryDocuments.clear()
        newDocs.forEachIndexed { idx, it ->
            necessaryDocuments += NecessaryDocument(
                facility = this,
                documentName = it.documentName,
                howToGet = it.howToGet,
                sortOrder = idx
            )
        }
    }

    companion object
}