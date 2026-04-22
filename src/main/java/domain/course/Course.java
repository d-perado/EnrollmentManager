package domain.course;

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

    private String title;

    @Column(length = 1000)
    private String description;

    private int price;

    // 최대 정원
    private int capacity;

    // 강의 상태
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int confirmedCount;

    public static Course create(String title, String description, int price, int capacity,
                                LocalDateTime startDate, LocalDateTime endDate) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("정원은 1 이상이어야 합니다.");
        }

        return Course.builder()
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

    public void increaseConfirmedCount() {
        if (isFull()) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }
        this.confirmedCount++;
    }

    public void decreaseConfirmedCount() {
        if (this.confirmedCount <= 0) {
            throw new IllegalStateException("확정 인원이 0보다 작을 수 없습니다.");
        }
        this.confirmedCount--;
    }

    public boolean isFull() {
        return confirmedCount >= capacity;
    }

    public boolean isOpen() {
        return this.status == CourseStatus.OPEN;
    }

    public void open() {
        if (this.status != CourseStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태에서만 OPEN으로 변경 가능합니다.");
        }
        this.status = CourseStatus.OPEN;
    }

    public void close() {
        if (this.status != CourseStatus.OPEN) {
            throw new IllegalStateException("OPEN 상태에서만 CLOSED로 변경 가능합니다.");
        }
        this.status = CourseStatus.CLOSED;
    }
}