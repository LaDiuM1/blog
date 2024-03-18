package com.study.blog.infrastructure.persistence.repository.comment;

import com.study.blog.infrastructure.persistence.entity.Comment;
import com.study.blog.infrastructure.persistence.repository.comment.response.CommentListResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@SqlGroup({
        @Sql(value = "/sql/test-comment-repository-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/test-truncate-all.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class CommentRepositoryImplTest {
    @Autowired
    private CommentRepository commentRepository;



    @Test
    @DisplayName("keyword = '테스트', status = true : 댓글 검색")
    void getCommentList_validKeyword_success() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchKeyword = "테스트";
        Boolean searchStatus = true;

        List<Comment> comments = commentRepository.findAll();
        System.out.println("comments = " + comments);

        Page<CommentListResponse> searchCommentList = commentRepository.getCommentList(searchKeyword, searchStatus, pageable);

        searchCommentList.getContent().forEach( comment -> {
            System.out.println("comment.getCommentContent() = " + comment.getCommentContent());
            System.out.println("comment.isStatus() = " + comment.isStatus());
            assertThat(comment.getCommentContent()).contains("테스트");
            assertThat(comment.isStatus()).isEqualTo(false);
        });
    }
}