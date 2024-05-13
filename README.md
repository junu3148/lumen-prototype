비디오 AI 서비스
비디오 AI 서비스는 고해상도 비디오 업스케일링을 제공하며, 사용자의 비디오 처리 이력을 관리합니다. 본 서비스는 Spring Boot를 사용하여 구현되었으며, OpenJDK 17, Spring Data JPA, PostgreSQL 등의 기술이 사용되었습니다.

주요 기능
비디오 업스케일링: AI 기술을 이용해 낮은 해상도의 비디오를 고해상도로 변환합니다.
이력 관리: 사용자의 비디오 처리 이력을 저장하고 조회합니다.
사용 기술
백엔드: OpenJDK 17, Spring Boot, Spring Data JPA
데이터베이스: PostgreSQL
도구: Gradle, GitHub, Docker, FFmpeg
API 엔드포인트
업스케일링
엔드포인트: POST /api/upscaling
설명: 사용자가 제공한 비디오 파일과 설정값을 사용하여 AI 모델을 통해 비디오를 업스케일링하고, 결과물을 저장 후 메타데이터와 함께 반환합니다.
이력 리스트 조회
엔드포인트: POST /api/history-list
설명: 사용자의 고유 정보를 바탕으로 처리된 썸네일과 메타데이터의 리스트를 조회합니다.
개별 이력 조회
엔드포인트: POST /api/history
설명: 특정 작업의 원본 파일과 변환된 파일의 URL과 메타데이터를 반환합니다.
파일 접근
엔드포인트: GET /files/{mediaType}/{filename:.+}
설명: 정의된 URL을 통해 이미지 및 비디오 파일을 접근하여 반환합니다.
