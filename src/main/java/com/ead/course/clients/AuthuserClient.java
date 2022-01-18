package com.ead.course.clients;

import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserCourseDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthuserClient {

    @Value("${ead.api.url.authuser}")
    private String REQUEST_URL_AUTHUSER;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UtilsService utilsService;

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {
        List<UserDto> searchResult = new ArrayList<>();
        String url = REQUEST_URL_AUTHUSER + utilsService.getUrlGetAllUsersByCourse(courseId, pageable);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        try {
            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ResponsePageDto<UserDto>> result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);
            searchResult = result.getBody().getContent();
            log.debug("Response number of elements: {} ", searchResult.size());
        } catch (HttpStatusCodeException e) {
            log.error("Error request /users ", e);
        }
        log.info("Ending request /users courseId {}", courseId);
        return new PageImpl<>(searchResult);
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.getUrlGetUserById(userId);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

    public void postSubscriptionUserInCourse(UUID courseId, UUID userId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.getUrlPostUserCourseSubscription(userId);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        var userCourseDto = new UserCourseDto();
        userCourseDto.setCourseId(courseId);
        userCourseDto.setUserId(userId);
        log.debug("UserCourseDto : {} ", userCourseDto.toString());
        restTemplate.postForEntity(url, userCourseDto, String.class);
    }

    public void deleteCourseInAuthuser(UUID courseId) {
        String url = REQUEST_URL_AUTHUSER + utilsService.getUrlDeleteCourseInAuthuser(courseId);
        log.debug("Request URL: {} ", url);
        log.info("Request URL: {} ", url);
        restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);
    }
}
