package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = "/courses")
public class CourseUserController {

    @Autowired
    private CourseService courseService;

    @GetMapping(path = "/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(@PathVariable UUID courseId,
                                                             @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)Pageable pageable) {
        log.debug("GET getAllUsersByCourse courseId received {} ", courseId);
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
//        Page<UserDto> getAllUsersByCoursePage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0,10), 10);
//        log.debug("GET getAllUsersByCourse totalElements {} ", getAllUsersByCoursePage.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }

    @PostMapping(path = "/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto) {
        log.debug("POST saveSubscriptionUserInCourse subscriptionDto received {} ", subscriptionDto.toString());
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        // TODO: State transfer validation
        return ResponseEntity.status(HttpStatus.CREATED).body(courseModelOptional.get());
    }
}
