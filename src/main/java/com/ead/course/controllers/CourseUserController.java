package com.ead.course.controllers;

import com.ead.course.clients.AuthuserClient;
import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(path = "/courses")
public class CourseUserController {

    @Autowired
    private AuthuserClient authuserClient;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseUserService courseUserService;

    @GetMapping(path = "/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(@PathVariable UUID courseId,
                                                             @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)Pageable pageable) {
        log.debug("GET getAllUsersByCourse courseId received {} ", courseId);
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        Page<UserDto> getAllUsersByCoursePage = authuserClient.getAllUsersByCourse(courseId, pageable);
        log.debug("GET getAllUsersByCourse totalElements {} ", getAllUsersByCoursePage.getTotalElements());
        return ResponseEntity.status(HttpStatus.OK).body(getAllUsersByCoursePage);
    }

    @PostMapping(path = "/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(@PathVariable UUID courseId,
                                                               @RequestBody @Valid SubscriptionDto subscriptionDto) {
        log.debug("POST saveSubscriptionUserInCourse subscriptionDto received {} ", subscriptionDto.toString());
        ResponseEntity<UserDto> responseUser;
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);
        if (courseModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.getUserId())) {
            log.warn("Subscription already exists for user {}", subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Subscription already exists.");
        }
        try {
            responseUser = authuserClient.getOneUserById(subscriptionDto.getUserId());
            if (responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED)) {
                log.warn("User is blocked userId {}", subscriptionDto.getUserId());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: User is blocked.");
            }
        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            log.error("Could not retrieve user userId {} ", subscriptionDto.getUserId(), e);
        }
        CourseUserModel courseUserModelSaved = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));
        log.debug("POST saveSubscriptionUserInCourse userId saved {} ", courseUserModelSaved.getUserId());
        log.info("Subscription created successfully userId {}", courseUserModelSaved.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModelSaved);
    }

    @DeleteMapping(path = "/users/{userId}")
    public ResponseEntity<Object> deleteCourseUserByUser(@PathVariable UUID userId) {
        log.debug("DELETE deleteCourseUserByUser courseUserId received {} ", userId);
        if (!courseUserService.existsByUserId(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseUser not found.");
        }
        courseUserService.deleteCourseUserByUser(userId);
        log.debug("DELETE deleteCourseUserByUser userId deleted {} ", userId);
        log.info("CourseUser deleted successfully userId {}", userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("CourseUser deleted successfully.");
    }

}
