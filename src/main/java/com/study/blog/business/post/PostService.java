package com.study.blog.business.post;

import com.study.blog.business.post.data.PostData;
import com.study.blog.business.post.dto.PostDto;
import com.study.blog.business.post.dto.PostListDto;
import com.study.blog.presentation.controller.request.SearchPostRequest;
import com.study.blog.presentation.controller.request.UpdatePostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final ReadPost readPost;
    private final CreatePost createPost;
    private final UpdatePost updatePost;
    private final DeletePost deletePost;

    private final UpdatePostTag updatePostTag;

    @Transactional
    public Long createPost(PostData postData){
        Post createdPost = createPost.createPost(postData);
        updatePostTag.postAddTags(createdPost, postData.getTagNames());
        return createdPost.getId();
    }

    public Page<PostListDto> searchPostList(SearchPostRequest request, Pageable pageable){
        Page<PostListDto> postList = readPost.searchPostList(request, pageable);
        updatePostTag.matchPostAndTags(postList.getContent());

        return postList;
    }

    public PostDto getPost(Long id){
        return readPost.getPost(id);
    }

    @Transactional
    public void updatePost(Long postId, UpdatePostRequest request){
        Post post = updatePost.updatePost(postId, request);
        updatePostTag.postAddTags(post, request.getTagNames());
    }

    public void updatePostStatus(Long postId){
        updatePost.updatePostStatus(postId);
    }

    public void deletePost(Long postId){
        deletePost.deletePost(postId);
    }

}