package com.study.blog.domain.category.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
public class UpdateCategoryRequest {

    @NotBlank(message = "카테고리 이름을 입력해 주세요.")
    @Size(max = 100, message = "카테고리 이름은 100글자를 넘을 수 없습니다.")
    private String name;

    @NotBlank(message = "카테고리 설명을 입력해 주세요.")
    @Size(max = 255, message = "카테고리 설명은 255글자 이내로 입력해 주세요.")
    private String description;

    public UpdateCategoryRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
