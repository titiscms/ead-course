package com.ead.course.repositories;

import com.ead.course.models.LessonModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<LessonModel, UUID>, JpaSpecificationExecutor<LessonModel> {

    @Query(value = "SELECT * FROM tb_lessons l WHERE l.module_module_id = :moduleId",nativeQuery = true)
    List<LessonModel> findAllLessonIntoModule(@Param("moduleId") UUID moduleId);

    @Query(value = "SELECT * FROM tb_lessons l WHERE l.module_module_id = :moduleId AND l.lesson_id = :lessonId",nativeQuery = true)
    Optional<LessonModel> findLessonIntoModule(@Param("moduleId") UUID moduleId, @Param("lessonId")UUID lessonId);
}
