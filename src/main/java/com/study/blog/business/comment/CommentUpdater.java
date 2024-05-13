package com.study.blog.business.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class CommentUpdater {
    private final CommentRepository commentRepository;

    @Transactional
    public void switchCommentStatus(Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        comment.switchStatus();
    }

}