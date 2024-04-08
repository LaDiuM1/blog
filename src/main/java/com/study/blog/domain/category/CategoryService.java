package com.study.blog.domain.category;

import com.study.blog.domain.category.repository.Category;
import com.study.blog.domain.category.repository.CategoryRepository;
import com.study.blog.domain.category.response.CategoryListResponse;
import com.study.blog.domain.category.request.CreateCategoryRequest;
import com.study.blog.domain.category.request.UpdateCategoryRequest;
import com.study.blog.domain.category.request.UpdateCategorySequenceRequest;
import com.study.blog.domain.category.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryCreator categoryCreator;
    private final CategoryQuery categoryQuery;
    private final CategoryUpdater categoryUpdater;
    private final CategoryDeleter categoryDeleter;

    private final CategoryRepository categoryRepository;

    public List<CategoryListResponse> getCategoryList() {
        return categoryQuery.getCategoryList();
    }

    public CategoryResponse getCategory(Long id){
        return categoryQuery.getCategory(id);
    }

    public Long createCategory(CreateCategoryRequest request){
        return categoryCreator.createCategory(request);
    }

    public void updateCategoryStatus(Long id){
        categoryUpdater.updateCategoryStatus(id);
    }

    public void updateCategorySequence(UpdateCategorySequenceRequest request){
        categoryUpdater.updateCategorySequence(request);
    }

    public void updateCategory(Long categoryId, UpdateCategoryRequest request){
        categoryUpdater.updateCategory(categoryId, request);
    }

    public void deleteCategory(Long id){
        categoryDeleter.deleteCategory(id);
    }

}
