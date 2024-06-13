# 2024-02-20
### 리팩토리 진행
<table>
  <tr>
    <th>클래스</th><th>메서드</th><th>변경 내용</th>
  </tr>
  <tr>
    <td>AdminPostController</td><td>getPostList</td><td>게시글 리스트 요청 시 Http 요청 메서드 Post -> Get 변경<br/>기존 json 객체는 @RequestParam 이용 쿼리스트링으로 변경</td>
  </tr>
  <tr>
    <td>AdminCategoryService</td><td>updateCategorySequence</td><td>카테고리 순서 변경 시 updateCategoryValid 및 updateCategorySequence 커스텀 쿼리문 추가하여 검증 및 업데이트 진행으로 변경</td>
  </tr>
  <tr>
    <td>AdminPostService</td><td>postAddTags</td><td>포스트에 태그 추가하는 로직 변경 및 접근 제어 private로 설정</td>
  </tr>
  <tr>
    <td>AdminPostService</td><td>createPost</td><td>게시글 리스트 호출 로직 변경, 포스트와 댓글을 가져오는 로직과 포스트 연관 태그를 조회하는 쿼리문 분리하여 쿼리 실행 최소화 진행</td>
  </tr>
</table>

---

# 2024-06-07
### 대용량 트래픽 처리

### 개요

블로그 프로젝트를 진행하며 JPA 및 QueryDSL을 사용하여 코드 내에서 데이터 처리에 대한 기본적인 최적화를 진행하였으나 다수의 사용자를 가정할 시 코드 상의 최적화로는 부족함을 느껴 대용량 트래픽 처리에 대한 방법을 구상하였다.

### 대용량 처리 환경 구성

- **부하 테스트 도구 : Apache JMeter**

  오픈 소스 부하 테스트 도구로 사용이 간편한 GUI를 지원하여 선정하였다.

- **캐시 서버 : Redis**

  캐시 사용을 위한 다양한 기능을 지원하며 spring data에서 Redis를 정식으로 지원하므로 선정하였다.

- **서버 컨테이너 : docker**

  여러 서버를 사용할 예정이므로 서버를 통합 관리하기 위해 docker를 사용하여 서버를 구동하기 위해 선정



### 대용량 처리 구현 프로세스 구상도

1. 게시글 샘플 데이터 1만개 생성 (테스트 확장성을 위해 엑셀 함수 또는 SQL 스크립트로 작성하여 용이하게 테스트 수 조절 가능하도록 구성, 게시물 제목 및 내용은 30개의 샘플 데이터를 AI를 이용하여 생성, 이후 게시글에 랜덤 매칭)
2. JMeter 설치 후 테스트 스크립트 작성, 실제 환경을 시뮬레이션하여 게시글 조회 랭크에 따른 파레토 분포의 누적 분포 함수를 적용하여  테스트 요청 데이터를 생성 후 실제 환경을 모방하도록 스크립트 구성
3. 기존 프로젝트의 부하 테스트 진행. 서버 로드율 60%구간에서 10회 이상의 평균 TPS를 기록
4. 도커 설치하여 Redis 빌드 (추후 mySQL 서버도 통합 진행)
5. 프로젝트에 Spring Data Redis 의존성 추가 후 Redis를 이용하여 조회수 상위 20% 게시물에 캐시 적용
6. JMeter로 동일하게 TPS 측정.
7. 메모리 사용률과 TPS 속도를 확인하며 캐시 적용 비율을 조정
    1. 7번 테스트 후 인기 게시물에 캐시 적용 방법으로는 목표치 실현 불가능 시 6번에서새로운 캐시 적용 방법 고려
8. 목표치 달성 시 게시물별 용량 또는 게시물 수 증량하여 현재 운영 환경상의 한계치 측정 진행
9. 추후에 참조할 수 있도록 구현 시 트러블 또는 아이디어 정리 진행

### 구현 프로세스 진척 상황

- [x]  **샘플 데이터 생성 (24.06.07)**
- [x]  **JMeter 환경 구성 ((24.06.07)**
- [x]  **기존 프로젝트 테스트 진행(24.06.08)**
    - ***idle 상태 10% 포함 cpu 부하 70% 구간은 초당 500요청으로 확인***
- [x]  **docker 환경 구성 (24.06.08)**
    - ***바이오스 가상화 활성화 후 설치 진행***
- [x]  **Redis 빌드 및 기본 테스트(24.06.10)**
    - ***Redis 빌드 후 단순 요청에 캐시 적용 후 한계치 테스트 진행***
    - ***캐시 적용 시 서버 리소스 사용량이 윈도우 기본 리소스를 초과하여 레지스트리에서 최대 리소스 값으로 수정 (65534)***
    - ***Jmeter 요청에 필요한 리소스가 서버 리소스를 초과하는 문제 발생, JMeter를 노트북에 설치 후 서버와 마스터를 분리, 서버에서 인바운드 설정 후 로컬 네트워크 영역으로 마스터에서 요청이 들어올 수 있도록 세팅***
    - ***마스터의 cpu인 intel Core 155H 로는 테스트 시 마스터의 리소스가 서버보다 빠르게 소모되어 마스터-슬레이브 패턴 고려 필요하나 서버 로드율 70% 기준에서는 100%에 미달될 것으로 예상되어 현재 환경에서 Redis 테스트 세팅을 마무리하는 것으로 결정***
- [x]  **Redis 캐시 적용(24.06.13)**
    - ***각 비즈니스 도메인에 RedisService 작성으로 구상 및 상수명 규칙 설정***
    - ***PostRedisService 작성,  게시물 조회 시 캐시에 해당 게시물id 조회수 증가 코드 추가***
    - ***매 시 정각 상위 20% 조회수의 게시물에 캐시 적용 후 조회수 초기화 스케줄링 메서드 작성***
    - ***캐시 적용 시 이미 캐시된 게시물은 보존, 캐시에 없는 게시물은 DB 조회후 캐시 적용 후 이 외의 캐시는 삭제  로직 추가***
    - ***Redis 직렬화/역직렬화 시 Java 8 이후 추가된 LocalDateTime은 직렬화 지원 불가하여 jsr310 의존성 추가하여 ObjectMapper configuration 클래스 생성 및  Redis templete에 직렬화/역직렬화 코드 추가***
    - ***캐시 적용 스케줄링 메서드 동작 시 캐싱되지 않은 게시글을 찾을 때 id를 set으로 묶어 배치처리하여 select 한번에 조회 될 수 있도록 최적화***
    - ***게시물 업데이트 또는 삭제 시 해당 게시물의 캐시 삭제***
- [ ]  **목표치 달성 및 한계치 측정**
- [ ]  **대용량 처리 총 정리 진행**