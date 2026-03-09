package io.github.snorrefo.event_driven_social.domain.repository


import io.github.snorrefo.event_driven_social.domain.model.Post
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<Post, UUID> {

    fun findByAuthorIdOrderByCreatedAtDesc(
        authorId: UUID,
        pageable: Pageable
    ): List<Post>

    @Query(
        """
        SELECT t FROM Post t 
        WHERE t.authorId IN :authorIds 
        ORDER BY t.createdAt DESC
    """,
    )
    fun findTimelineForUsers(
        @Param("authorIds") authorIds: List<UUID>,
        pageable: Pageable
    ): List<Post>

    fun countByAuthorId(authorId: UUID): Long
}