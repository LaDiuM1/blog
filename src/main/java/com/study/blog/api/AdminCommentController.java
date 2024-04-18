package com.study.blog.api;

import com.study.blog.domain.comment.response.CommentListResponse;
import com.study.blog.domain.comment.service.CommentService;
import com.study.blog.domain.comment.request.CommentListRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<Page<CommentListResponse>> getCommentList(
                                                              @RequestParam(required = false) String searchKeyword,
                                                              @RequestParam(required = false) Boolean searchStatus,
                                                              Pageable pageable) {
        CommentListRequest commentListRequest = new CommentListRequest();
        commentListRequest.setSearchKeyword(searchKeyword);
        commentListRequest.setSearchStatus(searchStatus);

        Page<CommentListResponse> postList = commentService.searchCommentList(commentListRequest, pageable);

        return ResponseEntity.ok(postList);
    }

    @PutMapping("/{commentId}/status")
    public ResponseEntity<Void> updateCommentStatus(@PathVariable("commentId") Long commentId) {
        commentService.switchCommentStatus(commentId);

        return ResponseEntity.ok().build();
    }

}