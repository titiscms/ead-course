package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
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
@RequestMapping(path = "/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    public static final String MSG_COURSE_NOT_FOUND = "Course not found.";
    @Autowired
    private CourseService courseService;

    @PostMapping
    public ResponseEntity<CourseModel> saveCourse(@Valid @RequestBody CourseDto courseDto) {
        log.debug("POST saveCourse courseDto received {} ", courseDto.toString());
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        CourseModel courseModelSaved = courseService.save(courseModel);
        log.debug("POST saveCourse courseModel saved {} ", courseModelSaved.toString());
        log.info("Course saved successfully courseId {}", courseModelSaved.getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModelSaved);
    }

    @DeleteMapping(path = "/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable UUID courseId) {
        log.debug("DELETE deleteCourse courseId received {} ", courseId);
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_COURSE_NOT_FOUND);
        }
        courseService.delete(courseModelOptional.get());
        log.debug("DELETE deleteCourse courseId deleted {} ", courseId);
        log.info("Course deleted successfully courseId {}", courseId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Course deleted successfully!");
    }

    @PutMapping(path = "/{courseId}")
    public ResponseEntity<Object> updateCourse(@PathVariable UUID courseId, @Valid @RequestBody CourseDto courseDto) {
        log.debug("PUT updateCourse courseDto {} received + courseId {} ", courseDto.toString(), courseId);
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_COURSE_NOT_FOUND);
        }
        var courseModelUpdate = courseModelOptional.get();
        BeanUtils.copyProperties(courseDto, courseModelUpdate);
        courseModelUpdate.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        CourseModel courseModelSaved = courseService.save(courseModelUpdate);
        log.debug("PUT updateCourse courseModel updated {} ", courseModelSaved.toString());
        log.info("Course updated successfully courseId {}", courseModelSaved.getCourseId());
        return ResponseEntity.status(HttpStatus.OK).body(courseModelSaved);
    }

    @GetMapping
    public ResponseEntity<Page<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec,
                                                           @PageableDefault(page = 0, size = 10, sort = "courseId", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<CourseModel> courseModelPage = courseService.findAll(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(courseModelPage);
    }

    @GetMapping(path = "/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable UUID courseId) {
        log.debug("GET getOneCourse courseId received {} ", courseId);
        var courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MSG_COURSE_NOT_FOUND);
        }
        log.debug("GET getOneCourse courseDto retrieved {} ", courseModelOptional.get().toString());
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
