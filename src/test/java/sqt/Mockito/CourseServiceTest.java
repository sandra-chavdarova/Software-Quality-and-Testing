package sqt.Mockito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course activeCourse;
    private Course inactiveCourse;

    @BeforeEach
    void setUp() {
        activeCourse = new Course(1L, "Mathematics", 3, true);
        inactiveCourse = new Course(2L, "History", 2, false);
    }

    @Nested
    @DisplayName("findActiveCourses()")
    class FindActiveCoursesTests {

        @Test
        @DisplayName("returns only active courses from the repository")
        void returnsOnlyActiveCourses() {
            given(courseRepository.findAll()).willReturn(List.of(activeCourse, inactiveCourse));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result)
                    .hasSize(1)
                    .containsExactly(activeCourse);
        }

        @Test
        @DisplayName("returns empty list when no courses are active")
        void returnsEmptyListWhenNoActiveCourses() {
            given(courseRepository.findAll()).willReturn(List.of(inactiveCourse));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty list when repository has no courses")
        void returnsEmptyListWhenRepositoryIsEmpty() {
            given(courseRepository.findAll()).willReturn(List.of());

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns all courses when every course is active")
        void returnsAllCoursesWhenAllAreActive() {
            Course anotherActive = new Course(3L, "Physics", 4, true);
            given(courseRepository.findAll()).willReturn(List.of(activeCourse, anotherActive));

            List<Course> result = courseService.findActiveCourses();

            assertThat(result).hasSize(2).containsExactlyInAnyOrder(activeCourse, anotherActive);
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindByIdTests {

        @Test
        @DisplayName("returns the course when it exists")
        void returnsCourseWhenFound() {
            given(courseRepository.findById(1L)).willReturn(Optional.of(activeCourse));

            Course result = courseService.findById(1L);

            assertThat(result).isEqualTo(activeCourse);
        }

        @Test
        @DisplayName("throws RuntimeException when course is not found")
        void throwsWhenCourseNotFound() {
            given(courseRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.findById(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");
        }
    }

    @Nested
    @DisplayName("createCourse()")
    class CreateCourseTests {

        @Test
        @DisplayName("saves and returns the course with the correct fields")
        void savesAndReturnsCourse() {
            Course saved = new Course(10L, "Biology", 3, true);
            given(courseRepository.save(any(Course.class))).willReturn(saved);

            Course result = courseService.createCourse("Biology", 3);

            assertThat(result).isEqualTo(saved);
        }

        @Test
        @DisplayName("persists the course as active with no pre-assigned id")
        void persistsCourseAsActiveWithNullId() {
            Course saved = new Course(10L, "Biology", 3, true);
            given(courseRepository.save(any(Course.class))).willReturn(saved);

            courseService.createCourse("Biology", 3);

            ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
            then(courseRepository).should().save(captor.capture());
            Course persisted = captor.getValue();

            assertThat(persisted.getId()).isNull();
            assertThat(persisted.getName()).isEqualTo("Biology");
            assertThat(persisted.getCredits()).isEqualTo(3);
            assertThat(persisted.isActive()).isTrue();
        }

        @Test
        @DisplayName("throws IllegalArgumentException when name is null")
        void throwsWhenNameIsNull() {
            assertThatThrownBy(() -> courseService.createCourse(null, 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course name is required");

            then(courseRepository).should(never()).save(any());
        }

        @ParameterizedTest(name = "name = \"{0}\"")
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("throws IllegalArgumentException when name is blank")
        void throwsWhenNameIsBlank(String blankName) {
            assertThatThrownBy(() -> courseService.createCourse(blankName, 3))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Course name is required");

            then(courseRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when credits are zero")
        void throwsWhenCreditsAreZero() {
            assertThatThrownBy(() -> courseService.createCourse("Math", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Credits must be positive");

            then(courseRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("throws IllegalArgumentException when credits are negative")
        void throwsWhenCreditsAreNegative() {
            assertThatThrownBy(() -> courseService.createCourse("Math", -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Credits must be positive");

            then(courseRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("deleteCourse()")
    class DeleteCourseTests {

        @Test
        @DisplayName("deletes the course when it exists")
        void deletesCourseWhenFound() {
            given(courseRepository.findById(1L)).willReturn(Optional.of(activeCourse));

            courseService.deleteCourse(1L);

            then(courseRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("throws RuntimeException when course to delete is not found")
        void throwsWhenCourseNotFound() {
            given(courseRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> courseService.deleteCourse(99L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Course not found");

            then(courseRepository).should(never()).deleteById(any());
        }

        @Test
        @DisplayName("uses the id from the found course, not the raw argument")
        void usesIdFromFoundCourse() {
            given(courseRepository.findById(1L)).willReturn(Optional.of(activeCourse));

            courseService.deleteCourse(1L);

            then(courseRepository).should().deleteById(activeCourse.getId());
        }
    }
}
