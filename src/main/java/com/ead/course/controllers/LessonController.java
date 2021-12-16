package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
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
    public ResponseEntity<List<LessonModel>> getAllLessonsForOneModule(@PathVariable UUID moduleId) {
        List<LessonModel> lessonModelList = lessonService.findAllModulesIntoCourse(moduleId);
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
