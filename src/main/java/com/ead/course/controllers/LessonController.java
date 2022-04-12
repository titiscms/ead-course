package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping(path = "/modules/{moduleId}/lessons")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    public static final String MSG_LESSON_NOT_FOUND_FOR_THIS_MODULE = "Lesson not found for this module.";
    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PostMapping
    public ResponseEntity<Object> saveLesson(@PathVariable UUID moduleId, @Valid @RequestBody LessonDto lessonDto) {
        log.debug("POST saveLesson lessonDto received {} ", lessonDto.toString());
        var moduleModelOptional = moduleService.findById(moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found.");
        }
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        LessonModel lessonModelSaved = lessonService.save(lessonModel);
        log.debug("POST saveLesson lessonId {} ", lessonModelSaved.getLessonId());
        log.info("Lesson saved successfully lessonId {}", lessonModelSaved.getLessonId());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModelSaved);
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @DeleteMapping(path = "/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        log.debug("DELETE deleteLesson lessonId received {} ", lessonId);
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_LESSON_NOT_FOUND_FOR_THIS_MODULE);
        }
        lessonService.delete(lessonModelOptional.get());
        log.debug("DELETE deleteLesson lessonId deleted {} ", lessonId);
        log.info("Lesson deleted successfully lessonId {}", lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Lesson deleted successfully!");
    }

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @PutMapping(path = "/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId, @Valid @RequestBody LessonDto lessonDto) {
        log.debug("PUT updateCourse lessonDto {} + lessonId {} received ", lessonDto.toString(), lessonId);
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_LESSON_NOT_FOUND_FOR_THIS_MODULE);
        }
        var lessonModelUpdate = lessonModelOptional.get();
        BeanUtils.copyProperties(lessonDto, lessonModelUpdate);
        LessonModel lessonModelSaved = lessonService.save(lessonModelUpdate);
        log.debug("PUT updateLesson lessonId {} ", lessonModelSaved.getLessonId());
        log.info("Lesson updated successfully lessonId {}", lessonModelSaved.getLessonId());
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelSaved);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping
    public ResponseEntity<Page<LessonModel>> getAllLessonsForOneModule(@PathVariable UUID moduleId,
                                                                       SpecificationTemplate.LessonSpec spec,
                                                                       @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LessonModel> lessonModelList = lessonService.findAllModulesIntoCourse(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelList);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @GetMapping(path = "/{lessonId}")
    public ResponseEntity<Object> getOneLessonForOneModule(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        log.debug("GET getOneLessonForOneModule lessonId received {} ", lessonId);
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_LESSON_NOT_FOUND_FOR_THIS_MODULE);
        }
        log.debug("GET getOneLessonForOneModule lessonId retrieved {} ", lessonModelOptional.get().getLessonId());
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
