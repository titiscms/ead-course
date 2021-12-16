package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
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
@RequestMapping(path = "/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseModel> saveCourse(@Valid @RequestBody CourseDto courseDto) {
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        CourseModel courseModelSaved = courseService.save(courseModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModelSaved);
    }

    @DeleteMapping(path = "/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        courseService.delete(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Course deleted successfully!");
    }

    @PutMapping(path = "/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable UUID courseId, @Valid @RequestBody CourseDto courseDto) {
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        var courseModelUpdate = courseModelOptional.get();
        BeanUtils.copyProperties(courseDto, courseModelUpdate);
        courseModelUpdate.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        CourseModel courseModelSaved = courseService.save(courseModelUpdate);
        return ResponseEntity.status(HttpStatus.OK).body(courseModelSaved);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec, @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CourseModel> courseModelPage = courseService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(courseModelPage);
    }

    @GetMapping(path = "/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable UUID courseId) {
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
