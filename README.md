# Class Enrollment System

## 프로젝트 개요

본 프로젝트는 강의 수강 신청 시스템을 구현한 백엔드 애플리케이션입니다.

사용자는 강의를 조회하고 수강 신청을 할 수 있으며,  
결제 확정 과정을 통해 수강이 확정됩니다.  
정원이 초과된 경우에는 대기열(waitlist)에 등록되며,  
수강 취소 시 대기열 사용자에게 기회가 제공됩니다.

---

## 기술 스택

- Language: Java 17
- Framework: Spring Boot
- ORM: Spring Data JPA
- Database: MySQL
- Build Tool: Gradle
- Test: JUnit5

---

## 실행 방법

```bash
./gradlew bootRun
```
테스트 실행:
```bash
./gradlew test
```
### 요구사항 해석 및 가정
수강 신청 상태는 WAITLIST → PENDING → CONFIRMED → CANCELLED로 관리합니다.
정원은 CONFIRMED 기준으로 관리합니다.
PENDING 상태는 결제 대기 상태이며 좌석을 점유하지 않습니다.
결제는 외부 PG 시스템을 연동하지 않고, 결제 확정 API로 대체합니다.
결제 대기 시간은 30분으로 제한합니다.
수강 취소는 결제 후 7일 이내에만 가능합니다.
동일 사용자의 중복 신청은 허용하지 않습니다.
설계 결정과 이유

### 1. 상태 기반 설계

수강 신청은 상태 전이 기반으로 설계했습니다.

WAITLIST: 정원 초과 시 대기 상태
PENDING: 결제 대기 상태
CONFIRMED: 결제 완료
CANCELLED: 취소

이를 통해 비즈니스 흐름을 명확하게 표현했습니다.

### 2. 정원 관리 정책

정원은 CONFIRMED 기준으로 관리했습니다.

이유:

결제 완료된 사용자만 실제 수강생으로 간주
결제 대기(PENDING)는 좌석을 점유하지 않음

### 3. 대기열(waitlist) 설계

대기열은 별도의 테이블이 아닌 Enrollment 상태로 관리했습니다.

정원 초과 시 WAITLIST 상태로 생성
수강 취소 시 FIFO 방식으로 승격
승격 시 WAITLIST → PENDING

### 4. 동시성 처리

결제 확정 시 PESSIMISTIC_WRITE 락을 사용했습니다.

이유:

동시에 여러 사용자가 마지막 자리를 결제하는 경우 방지
정원 초과 문제 방지

### 5. 결제 처리 방식

외부 PG 연동은 제외하고, 결제 확정 API로 대체했습니다.

실제 결제 호출은 트랜잭션 외부에서 수행된다고 가정
본 시스템은 결제 성공 이후 상태 변경만 담당
미구현 / 제약사항
외부 결제 시스템(PG) 연동 미구현
결제 대기(PENDING) 자동 만료 스케줄러 미구현
대기열 사용자 알림 기능 미구현
분산 환경에서의 동시성 처리 미고려 (단일 인스턴스 기준)

## AI 활용 범위

AI 도구를 활용하여 다음 작업을 수행했습니다:

요구사항 해석 및 설계 검토
코드 초안 생성
테스트 케이스 아이디어 도출

단, 생성된 코드는 그대로 사용하지 않고
직접 실행 및 검증을 통해 수정 및 보완했습니다.

특히 다음은 직접 설계했습니다:

수강 신청 상태 전이 구조
정원 관리 정책
대기열(waitlist) 처리 방식
동시성 처리 전략

### API 목록 및 예시
사용자

POST /users
GET /users/{id}

강의
POST /courses
PATCH /courses/{id}/open
PATCH /courses/{id}/close
GET /courses

수강 신청
POST /enrollments
PATCH /enrollments/{id}/confirm
PATCH /enrollments/{id}/cancel
GET /enrollments/me
GET /enrollments/courses/{courseId}/students

데이터 모델 설명(ERD로 대체 예정)
User
id
email
name
role (STUDENT / INSTRUCTOR)

Course
id
title
description
price
capacity
confirmedCount
status (DRAFT / OPEN / CLOSED)

Enrollment
id
user
course
status
createdAt
confirmedAt

### 테스트 실행 방법
```bash
./gradlew test
```
모든 테스트 케이스 통과 확인
주요 비즈니스 로직 검증:
중복 신청 방지
정원 초과 처리
대기열 생성 및 승격
수강 취소 정책