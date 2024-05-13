# 비디오 AI


## 사용 기술

### Spring boot Server

Open JDK 17, Spring boot, Gradle, Spring data JPA, Postgresql 

GitHub, Docker, FFmpeg

## AI 기능

### Upscaling
- **엔드포인트**: `POST /api/Upscaling`
- **설명**: 비디오 파일과 설정값으로 AI모델과 통신하여 비디오를 Upscaling하고 메타정보를 저장 후 반환합니다.


## A공통 영역

### History List
- **엔드포인트**: `Post /api/history-list`
- **설명**: 유저 정보로 작업했던 썸네일과 메타데이터의 리스트를 조회합니다.

### History
- **엔드포인트**: `Post /api/history`
- **설명**: 작업 내용으로 원본 파일과 변환본 파일의 URL스트링 경로와 메타데이터를 반환합니다.

### File 접근
- **엔드포인트**: `Get /files/{mediaType}/{filename:.}`
- **설명**: 접근가능한 URL을 통해 img, video 타입별로 조회해서 반환합니다.
