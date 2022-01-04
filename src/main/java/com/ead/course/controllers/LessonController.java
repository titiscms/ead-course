package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import com.ead.course.specifications.SpecificationTemplate;
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

@RestController
@RequestMapping(path = "/modules/{moduleId}/lessons")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @PostMapping
    public ResponseEntity<Object> saveLesson(@PathVariable UUID moduleId, @Valid @RequestBody LessonDto lessonDto) {
        var moduleModelOptional = moduleService.findById(moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found.");
        }
        var lessonModel = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lessonModel);
        lessonModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lessonModel.setModule(moduleModelOptional.get());
        LessonModel lessonModelSaved = lessonService.save(lessonModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonModelSaved);
    }

    @DeleteMapping(path = "/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        lessonService.delete(lessonModelOptional.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Lesson deleted successfully!");
    }

    @PutMapping(path = "/{lessonId}")
    public ResponseEntity<Object> updateLesson(@PathVariable UUID moduleId, @PathVariable UUID lessonId, @Valid @RequestBody LessonDto lessonDto) {
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        var lessonModelUpdate = lessonModelOptional.get();
        BeanUtils.copyProperties(lessonDto, lessonModelUpdate);
        LessonModel lessonModelSaved = lessonService.save(lessonModelUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelSaved);
    }

    @GetMapping
    public ResponseEntity<Page<LessonModel>> getAllLessonsForOneModule(@PathVariable UUID moduleId,
                                                                       SpecificationTemplate.LessonSpec spec,
                                                                       @PageableDefault(page = 0, size = 10, sort = "lessonId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LessonModel> lessonModelList = lessonService.findAllModulesIntoCourse(SpecificationTemplate.lessonModuleId(moduleId).and(spec), pageable);
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelList);
    }

    @GetMapping(path = "/{lessonId}")
    public ResponseEntity<Object> getOneLessonForOneModule(@PathVariable UUID moduleId, @PathVariable UUID lessonId) {
        var lessonModelOptional = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (lessonModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson not found for this module.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(lessonModelOptional.get());
    }
}
