package com.study.blog.admin.request;

import lombok.Getter;

import javax.validation.constraints.*;

@Getter
public class UpdateCategoryStatusRequest {

    @NotNull(message = "카테고리 id값이 null입니다.&20101")
    @Min(value = 1, message = "카테고리 id는 정수 1 이상 요청바랍니다.&20102")
    private Long id;

    @NotNull(message = "카테고리 상태값이 null입니다.&20103")
    private Boolean status;
}
