package com.ead.course.repositories;

import com.ead.course.models.ModuleModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ModuleRepository extends JpaRepository<ModuleModel, UUID>, JpaSpecificationExecutor<ModuleModel> {

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

    @Query(value = "SELECT * FROM tb_modules m WHERE m.course_course_id = :courseId AND m.module_id = :moduleId", nativeQuery = true)
    Optional<ModuleModel> findModuleIntoCourse(@Param("courseId") UUID courseId, @Param("moduleId") UUID moduleId);
}
