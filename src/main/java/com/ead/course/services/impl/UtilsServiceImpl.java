package com.ead.course.services.impl;

import com.ead.course.services.UtilsService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UtilsServiceImpl implements UtilsService {

    @Override
    public String getUrlGetAllUsersByCourse(UUID courseId, Pageable pageable) {
        return "/users?courseId=" + courseId + "&page=" + pageable.getPageNumber() + "&size=" + pageable.getPageSize()
                + "&sort=" + pageable.getSort().toString().replace(": ", ",");
    }

    @Override
    public String getUrlGetUserById(UUID userId) {
        return "/users/" + userId;
    }

    @Override
    public String getUrlPostUserCourseSubscription(UUID userId) {
        return "/users/" + userId + "/courses/subscription";
    }

    @Override
    public String getUrlDeleteCourseInAuthuser(UUID courseId) {
        return "/users/courses/" + courseId;
    }
}
