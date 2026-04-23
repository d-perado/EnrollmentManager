package domain.course;

import common.exception.BusinessException;
import common.exception.ErrorCode;
import domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_id", nullable = false)
    private User instructor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CourseStatus status;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int confirmedCount;

    public static Course create(
            User instructor,
            String title,
            String description,
            int price,
            int capacity,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        validateCreateCondition(price, capacity, startDate, endDate);

        return Course.builder()
                .instructor(instructor)
                .title(title)
                .description(description)
                .price(price)
                .capacity(capacity)
                .status(CourseStatus.DRAFT)
                .startDate(startDate)
                .endDate(endDate)
                .confirmedCount(0)
                .build();
    }

    public void open() {
        if (this.status != CourseStatus.DRAFT) {
            throw new BusinessException(ErrorCode.INVALID_COURSE_STATUS, "DRAFT 상태에서만 OPEN으로 변경할 수 있습니다.");
        }
        this.status = CourseStatus.OPEN;
    }

    public void close() {
        if (this.status != CourseStatus.OPEN) {
            throw new BusinessException(ErrorCode.INVALID_COURSE_STATUS, "OPEN 상태에서만 CLOSED로 변경할 수 있습니다.");
        }
        this.status = CourseStatus.CLOSED;
    }

    public boolean isOpen() {
        return this.status == CourseStatus.OPEN;
    }

    public boolean isFull() {
        return this.confirmedCount >= this.capacity;
    }

    public void increaseConfirmedCount() {
        if (isFull()) {
            throw new BusinessException(ErrorCode.COURSE_CAPACITY_EXCEEDED);
        }
        this.confirmedCount++;
    }

    public void decreaseConfirmedCount() {
        if (this.confirmedCount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_COURSE_STATUS, "확정 인원은 0보다 작아질 수 없습니다.");
        }
        this.confirmedCount--;
    }

    private static void validateCreateCondition(
            int price,
            int capacity,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if (price < 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "가격은 0 이상이어야 합니다.");
        }

        if (capacity <= 0) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "정원은 1 이상이어야 합니다.");
        }

        if (startDate == null || endDate == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "수강 시작일과 종료일은 필수입니다.");
        }

        if (!startDate.isBefore(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "수강 시작일은 종료일보다 이전이어야 합니다.");
        }
    }
}