package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
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
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = "/courses")
public class CourseUserController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping(path = "/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(SpecificationTemplate.UserSpec spec,
                                                      @PathVariable UUID courseId,
                                                      @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC) Pageable pageable) {
        log.debug("GET getAllUsersByCourse courseId received {} ", courseId);
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        Page<UserModel> userModelPage = userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable);
        log.debug("GET getAllUsersByCourse totalElements {} ", userModelPage.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(userModelPage);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping(path = "/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto) {
        log.debug("POST saveSubscriptionUserInCourse subscriptionDto received {} ", subscriptionDto.toString());
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        if (courseService.existsByCourseAndUser(courseId, subscriptionDto.getUserId())) {
            log.warn("Subscription already exists for user {}", subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Subscription already exists!");
        }
        Optional<UserModel> userModelOptional = userService.findById(subscriptionDto.getUserId());
        if (userModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        if (userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString())) {
            log.warn("User is blocked userId {}", subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: User is blocked");
        }
        courseService.saveSubscriptionUserInCourseAndSendNotification(courseModelOptional.get(), userModelOptional.get());
        log.debug("POST saveSubscriptionUserInCourse userId {} + courseId {}", userModelOptional.get().getUserId(), courseModelOptional.get().getCourseId());
        log.info("Subscription created successfully userId {} + courseId {} ", userModelOptional.get().getUserId(), courseModelOptional.get().getCourseId());
        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }
}
