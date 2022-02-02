package com.ead.course.specifications;

import com.ead.course.models.CourseModel;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.models.UserModel;
import net.kaczmarzyk.spring.data.jpa.domain.Between;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.UUID;

public class SpecificationTemplate {

    @And({
            @Spec(path = "courseLevel", spec = Equal.class),
            @Spec(path = "courseStatus", spec = Equal.class),
            @Spec(path = "name", spec = LikeIgnoreCase.class),
            @Spec(path="creationDate", params={"createdAfter","createdBefore"}, spec=Between.class, config="yyyy-MM-dd'T'HH:mm:ss'Z'")
    })
    public interface CourseSpec extends Specification<CourseModel> { }

    @And({
            @Spec(path = "userType", spec = Equal.class),
            @Spec(path = "userStatus", spec = Equal.class),
            @Spec(path = "email", spec = LikeIgnoreCase.class),
            @Spec(path = "fullName", spec = LikeIgnoreCase.class)
    })
    public interface UserSpec extends Specification<UserModel> { }

    @And({
            @Spec(path = "title", spec = LikeIgnoreCase.class),
            @Spec(path="creationDate", params={"createdAfter","createdBefore"}, spec=Between.class, config="yyyy-MM-dd'T'HH:mm:ss'Z'")
    })
    public interface ModuleSpec extends Specification<ModuleModel> { }

    @And({
            @Spec(path = "title", spec = LikeIgnoreCase.class),
            @Spec(path="creationDate", params={"createdAfter","createdBefore"}, spec=Between.class, config="yyyy-MM-dd'T'HH:mm:ss'Z'")
    })
    public interface LessonSpec extends Specification<LessonModel> { }

    public static Specification<ModuleModel> moduleCourseId(final UUID courseId) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            // Entity A
            Root<ModuleModel> moduleModelRoot = root;
            // Entity B
            Root<CourseModel> courseModelRoot = query.from(CourseModel.class);
            // Collection from Entity A into Entity B
            Expression<Collection<ModuleModel>> courseModules = courseModelRoot.get("modules");
            // It was used AND type to built CriteriaBuilder to return data of modules from courseId
            return criteriaBuilder.and(criteriaBuilder.equal(courseModelRoot.get("courseId"), courseId), criteriaBuilder.isMember(moduleModelRoot, courseModules));
        });
    }

    public static Specification<LessonModel> lessonModuleId(final UUID moduleId) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            // Entity A
            Root<LessonModel> lessonModelRoot = root;
            // Entity B
            Root<ModuleModel> moduleModelRoot = query.from(ModuleModel.class);
            // Collection from Entity A into Entity B
            Expression<Collection<LessonModel>> moduleLessons = moduleModelRoot.get("lessons");
            // It was used AND type to built CriteriaBuilder to return data of lessons from moduleId
            return criteriaBuilder.and(criteriaBuilder.equal(moduleModelRoot.get("moduleId"), moduleId), criteriaBuilder.isMember(lessonModelRoot, moduleLessons));
        });
    }

    public static Specification<UserModel> userCourseId(final UUID courseId) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            // Entity A
            Root<UserModel> userModelRoot = root;
            // Entity B
            Root<CourseModel> courseModelRoot = query.from(CourseModel.class);
            // Collection from Entity A into Entity B
            Expression<Collection<UserModel>> courseUsers = courseModelRoot.get("users");
            // It was used AND type to built CriteriaBuilder to return data of users from courseId
            return criteriaBuilder.and(criteriaBuilder.equal(courseModelRoot.get("courseId"), courseId), criteriaBuilder.isMember(userModelRoot, courseUsers));
        });
    }

    public static Specification<CourseModel> courseUserId(final UUID userId) {
        return ((root, query, criteriaBuilder) -> {
            query.distinct(true);
            // Entity A
            Root<CourseModel> courseModelRoot = root;
            // Entity B
            Root<UserModel> userModelRoot = query.from(UserModel.class);
            // Collection from Entity A into Entity B
            Expression<Collection<CourseModel>> userCourses = userModelRoot.get("courses");
            // It was used AND type to built CriteriaBuilder to return data of users from courseId
            return criteriaBuilder.and(criteriaBuilder.equal(userModelRoot.get("userId"), userId), criteriaBuilder.isMember(courseModelRoot, userCourses));
        });
    }
}
