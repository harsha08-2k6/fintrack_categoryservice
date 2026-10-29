package com.spendwise.category.repository;

import com.spendwise.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.userId IS NULL OR c.userId = :userId ORDER BY c.name ASC, c.id ASC")
    List<Category> findAllAvailableForUser(@Param("userId") Long userId);

    List<Category> findByUserId(Long userId);
    List<Category> findByUserIdIsNull();

    @Query("""
            SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
            FROM Category c
            WHERE (c.userId IS NULL OR c.userId = :userId)
              AND LOWER(TRIM(c.name)) = LOWER(TRIM(:name))
            """)
    boolean existsAvailableForUserByNormalizedName(@Param("userId") Long userId, @Param("name") String name);
}
