package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourseService;
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
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/courses/{courseId}/modules")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<Object> saveModule(@PathVariable UUID courseId, @Valid @RequestBody ModuleDto moduleDto) {
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        var moduleModel = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, moduleModel);
        moduleModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        moduleModel.setCourse(courseModelOptional.get());
        ModuleModel moduleModelSaved = moduleService.save(moduleModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleModelSaved);
    }

    @DeleteMapping(path = "/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Module deleted successfully!");
    }

    @PutMapping(path = "/{moduleId}")
    public ResponseEntity<Object> updateModule(@PathVariable UUID courseId, @PathVariable UUID moduleId, @Valid @RequestBody ModuleDto moduleDto) {
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        var moduleModelUpdate = moduleModelOptional.get();
        BeanUtils.copyProperties(moduleDto, moduleModelUpdate);
        ModuleModel moduleModelSaved = moduleService.save(moduleModelUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelSaved);
    }

    @GetMapping
    public ResponseEntity<List<ModuleModel>> getAllModulesForOneCourse(@PathVariable UUID courseId) {
        List<ModuleModel> moduleModelList = moduleService.findAllModulesIntoCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelList);
    }

    @GetMapping(path = "/{moduleId}")
    public ResponseEntity<Object> getOneModuleForOneCourse(@PathVariable UUID courseId, @PathVariable UUID moduleId) {
        var moduleModelOptional = moduleService.findModuleIntoCourse(courseId, moduleId);
        if (moduleModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module not found for this course.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}