## 프로젝트 개요

ClassFlow는 수강 신청부터 결제 확정, 취소까지의 흐름을 관리하는 시스템입니다.

본 프로젝트는 단순 CRUD 구현이 아닌, 수강 신청 과정에서 발생하는 비즈니스 규칙(정원 관리, 상태 전이, 결제 흐름 등)을 중심으로 설계되었습니다.
특히 상태 기반 흐름과 트랜잭션을 통해 실제 서비스 환경에서 발생할 수 있는 문제를 해결하는 것을 목표로 합니다.

---

## 기술 스택

* Backend: Spring Boot (Java)
* ORM: JPA (Hibernate)
* Database: MySQL
* Build Tool: Gradle

---

## 실행 방법

### 1. MySQL 실행 및 DB 생성

```sql
CREATE DATABASE classflow;
```

### 2. application.yml 설정

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/classflow
    username: root
    password: yourpassword
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```

---

## 요구사항 해석 및 가정

### 수강 신청 흐름

* 수강 신청 시 `Enrollment`는 `PENDING` 상태로 생성됩니다.
* 결제 완료 시 `CONFIRMED` 상태로 변경되며, 이 시점에서 수강이 확정됩니다.

### 정원 관리 정책

* 강의 정원은 `CONFIRMED` 상태 기준으로 계산합니다.
* `PENDING` 상태는 정원을 차지하지 않습니다.

### PENDING 상태 유지 정책

* `PENDING` 상태는 30분 동안 유효합니다.
* 유효 시간이 지난 이후에는 결제 확정이 불가능합니다.
* 별도의 만료 상태 없이, 결제 시점 검증으로 처리합니다.

### 중복 신청 정책

* 동일 사용자는 동일 강의에 대해 하나의 활성 신청만 가질 수 있습니다.
* `PENDING`, `CONFIRMED` 상태가 존재하면 추가 신청은 불가능합니다.
* `CANCELLED` 이후에는 재신청이 가능합니다.

### 수강 취소 정책

* 수강 취소는 `CONFIRMED` 상태에서만 가능합니다.
* 결제 완료 시점 기준 7일 이내에만 취소가 가능합니다.

### 강의 상태 정책

* `DRAFT`: 신청 불가
* `OPEN`: 신청 가능
* `CLOSED`: 신청 불가

---

## 설계 결정과 이유

### 상태 기반 설계

수강 신청 흐름을 다음과 같은 상태 전이로 모델링하였습니다.

PENDING → CONFIRMED → CANCELLED

각 상태별로 비즈니스 규칙을 분리하여 흐름 중심 설계를 유지했습니다.

---

### 정원 체크 시점

정원 체크는 신청 시점이 아닌 결제 확정 시점(CONFIRMED)에 수행합니다.

* 결제하지 않은 사용자가 좌석을 점유하는 문제 방지
* 실제 서비스 환경과 유사한 정책 반영

---

### 동시성 처리

동시에 여러 사용자가 마지막 자리를 결제하는 상황을 고려하였습니다.

* 트랜잭션 내에서 정원 확인 및 상태 변경을 함께 수행
* 정원 초과를 방지하고 데이터 일관성을 유지

---

### 데이터베이스 선택 (MySQL)

MySQL을 사용하여 실제 서비스 환경과 유사한 트랜잭션 및 락 기반 동시성 처리를 고려했습니다.

---

### 도메인 단순화

핵심 비즈니스 로직에 집중하기 위해 다음과 같이 단순한 도메인 구조를 사용했습니다.

* Class
* User
* Enrollment

---

## 미구현 / 제약사항

* 외부 결제 시스템은 구현하지 않고 상태 변경으로 대체했습니다.
* PENDING 상태 자동 만료는 별도 스케줄러 없이 결제 시점 검증으로 처리했습니다.
* 대기열(waitlist) 기능은 구현하지 않았습니다.

---

## AI 활용 범위

* 요구사항 해석 및 설계 구조 검토
* 상태 전이 및 비즈니스 로직 정리
* 테스트 케이스 아이디어 보조
* README 초안 작성

최종 설계 및 구현, 검증은 직접 수행하였습니다.

---

## API 목록 및 예시

### 1. 강의 생성

POST /classes

### 2. 강의 목록 조회

GET /classes

### 3. 강의 상세 조회

GET /classes/{classId}

### 4. 수강 신청

POST /enrollments

### 5. 결제 확정

POST /enrollments/{id}/confirm

### 6. 수강 취소

POST /enrollments/{id}/cancel

### 7. 내 수강 신청 목록 조회

GET /enrollments/me

---

## 데이터 모델 설명

### Class

* id
* title
* description
* price
* capacity
* status (DRAFT, OPEN, CLOSED)

### User

* id
* email
* name

### Enrollment

* id
* user_id
* class_id
* status (PENDING, CONFIRMED, CANCELLED)
* created_at

---

## 테스트 실행 방법

```bash
./gradlew test
```

테스트는 다음 시나리오를 포함합니다.

* 수강 신청 생성
* 결제 확정 시 정원 초과 검증
* 중복 신청 방지
* 취소 가능 기간 검증
