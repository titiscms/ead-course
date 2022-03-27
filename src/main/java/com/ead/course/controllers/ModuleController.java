package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(path = "/courses/{courseId}/modules")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    public static final String MSG_MODULE_NOT_FOUND_FOR_THIS_COURSE = "Module not found for this course.";
    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> saveModule(@PathVariable UUID courseId, @Valid @RequestBody ModuleDto moduleDto) {
        log.debug("POST saveModule moduleDto received {} ", moduleDto.toString());
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());
        ModuleModel moduleModelSaved = moduleService.save(moduleModel);
        log.debug("POST saveModule moduleId {} ", moduleModelSaved.getModuleId());
        log.info("Module saved successfully moduleId {}", moduleModelSaved.getModuleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModelSaved);
    }

    @DeleteMapping(path = "/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        log.debug("DELETE deleteModule moduleId received {} ", moduleId);
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_MODULE_NOT_FOUND_FOR_THIS_COURSE);
        }
        moduleService.delete(moduleModelOptional.get());
        log.debug("DELETE deleteModule moduleId deleted {} ", moduleId);
        log.info("Module deleted successfully moduleId {}", moduleId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Module deleted successfully!");
    }

    @PutMapping(path = "/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable UUID courseId, @PathVariable UUID moduleId, @Valid @RequestBody ModuleDto moduleDto) {
        log.debug("PUT updateModule moduleDto {} + moduleId {} received ", moduleDto.toString(), moduleId);
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_MODULE_NOT_FOUND_FOR_THIS_COURSE);
        }
        var moduleModelUpdate = moduleModelOptional.get();
        BeanUtils.copyProperties(moduleDto, moduleModelUpdate);
        ModuleModel moduleModelSaved = moduleService.save(moduleModelUpdate);
        log.debug("PUT updateModule moduleId{}", moduleModelSaved.getModuleId());
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelSaved);
    }

    @GetMapping
    public ResponseEntity<Page<ModuleModel>> getAllModulesForOneCourse(@PathVariable UUID courseId,
                                                                       SpecificationTemplate.ModuleSpec spec,
                                                                       @PageableDefault(page = 0, size = 10, sort = "moduleId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<ModuleModel> moduleModelPage = moduleService.findAllModulesIntoCourse(SpecificationTemplate.moduleCourseId(courseId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelPage);
    }

    @GetMapping(path = "/{moduleId}")
    public ResponseEntity<Object> getOneModuleForOneCourse(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        log.debug("GET getOneModuleForOneCourse moduleId received {} ", moduleId);
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_MODULE_NOT_FOUND_FOR_THIS_COURSE);
        }
        log.debug("GET getOneModuleForOneCourse moduleId {} ", moduleModelOptional.get().getModuleId());
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}
