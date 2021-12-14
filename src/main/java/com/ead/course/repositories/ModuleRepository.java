package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID> {

    // nessa consulta especifica o @EntityGraph traz os valores de curso mesmo setando o atributo como fetchType.LAZY na entity
    @EntityGraph(attributePaths = {"course"})
    ModuleModel findByTitle(String title);

    // consulta customizada usamos o @Query
    @Query(value = "SELECT * FROM tb_modules m WHERE m.course_course_id = :courseId", nativeQuery = true)
    List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId);

    // inserindo, atualizando ou excluindo customizados é necessário o @Modifying
    @Modifying
    @Query(value = "DELETE * FROM tb_modules m WHERE m.course_course_id = :courseId AND m.module_id = :moduleId", nativeQuery = true)
    void deleteModuleIntoCourse(@Param("courseId") UUID courseId, @Param("moduleId") UUID moduleId);
}
