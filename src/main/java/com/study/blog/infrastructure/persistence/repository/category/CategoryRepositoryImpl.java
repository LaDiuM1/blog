package com.study.blog.infrastructure.persistence.repository.category;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.blog.service.category.response.CategoryListResponse;
import com.study.blog.infrastructure.persistence.entity.Category;
import com.study.blog.infrastructure.persistence.entity.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Repository
@Transactional
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepositoryCustom {

    private final JPAQueryFactory query;

    public Category findByIdOrThrow(Long id) {
        QCategory qCategory = QCategory.category;

        Category category = query.selectFrom(qCategory)
                .where(qCategory.id.eq(id))
                .fetchOne();

        if (category == null) { throw new EntityNotFoundException(); }

        return category;
    }

    private NumberExpression<Integer> sequenceCaseBuilder(QCategory category, Set<Long> idSet){
        CaseBuilder caseBuilder = new CaseBuilder();
        NumberExpression<Integer> categorySequence = category.sequence;
//        번호 설정 코드 최적화
        int sequenceNumber = 1;
        for (Long id : idSet) {
            categorySequence = caseBuilder.when(category.id.eq(id)).then(sequenceNumber++).otherwise(categorySequence);
        }
        return categorySequence;
    }

    public Integer getCreateSequenceNumber(){
        QCategory category = QCategory.category;

        return query.select(category.sequence.max().add(1).coalesce(1))
                .from(category)
                .fetchOne();

    }

    public List<CategoryListResponse> getAdminCategoryList() {
        QCategory category = QCategory.category;

        return query.select(Projections.constructor(CategoryListResponse.class,
                        category.id,
                        category.name,
                        category.description,
                        category.status,
                        category.sequence))
                .from(category)
                .fetch();
    }

    public boolean updateCategoryValid(Set<Long> idSet){
        QCategory category = QCategory.category;

        JPAQuery<Long> jpaQuery = query.select(category.count())
                .from(category);

        Long categoryCount = jpaQuery.fetchOne();
        Long requestCount = jpaQuery.where(category.id.in(idSet)).fetchOne();

        return !Objects.equals(requestCount, categoryCount);
    }

    public void updateCategorySequence(Set<Long> idSet){
        QCategory category = QCategory.category;
        NumberExpression<Integer> cases = sequenceCaseBuilder(category, idSet);

        query.update(category)
                .set(category.sequence, cases)
                .where(category.id.in(idSet))
                .execute();
    }
}