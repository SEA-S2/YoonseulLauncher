# 제3자 소프트웨어 및 원저작물 고지

윤슬런처는 아래의 오픈소스 소프트웨어, 글꼴 및 원저작물을 사용하거나 함께 배포합니다. 각 구성요소는 해당 원저작자가 정한 라이선스 조건을 따릅니다.

이 문서에서 **동글랜드(Dongleland)**라는 명칭은 원본 프로젝트와 원저작물의 출처를 표시하기 위한 것입니다. 이러한 출처·저작권 표기를 윤슬로 바꾸지 않습니다.

---

## 1. 원본 프로젝트 고지

윤슬런처의 일부 구조·코드 또는 기능은 다음 프로젝트를 기반으로 하거나 참고했습니다.

### Dongleland Launcher

- 프로젝트명: Dongleland Launcher(동글랜드 런처)
- 원저작자: Garamisme 및 해당 프로젝트 기여자
- 원본 저장소: https://github.com/Garamisme/dongleland_Launcher
- 라이선스: 원본 저장소의 `LICENSE` 파일 참조

Dongleland Launcher의 프로젝트명, 저작자명, 저작권 표시 및 라이선스 표시는 원저작물에 관한 고지이므로 유지합니다. 윤슬런처라는 명칭은 수정·통합된 별도 배포물의 이름을 뜻하며 원본 프로젝트의 저작권을 대체하지 않습니다.

원본 프로젝트의 이용 약관은 원본 프로젝트 자체에 관한 문서입니다. 윤슬런처 사용자에게 적용되는 약관은 이 저장소의 `TERMS.md`를 따릅니다.

---

## 2. 런처와 함께 사용되거나 배포되는 구성요소

### minecraft-launcher-lib

- 저작자: JakobDev
- 라이선스: **BSD 2-Clause “Simplified” License**
- 출처: https://codeberg.org/JakobDev/minecraft-launcher-lib
- 용도: Minecraft 버전/Fabric 설치, Microsoft 계정 인증 체인, 실행 명령 생성

### pywebview

- 저작자: Roman Sirokov
- 라이선스: **BSD 3-Clause License**
- 출처: https://github.com/r0x0r/pywebview
- 용도: 런처 UI(WebView2) 창

### Requests

- 저작자: Kenneth Reitz 및 기여자
- 라이선스: **Apache License 2.0**
- 출처: https://github.com/psf/requests
- 용도: HTTP 통신

### certifi

- 저작자: Kenneth Reitz 및 기여자
- 라이선스: **Mozilla Public License 2.0 (MPL-2.0)**
- 출처: https://github.com/certifi/python-certifi
- 용도: TLS 인증서 번들 및 SSL 검증

### ASM (OW2)

- 저작자: OW2 Consortium
- 저작권 표시: Copyright (c) 2000-2011 INRIA, France Telecom
- 라이선스: **BSD 3-Clause License**
- 출처: https://asm.ow2.io/
- 용도: 코어 모드의 바이트코드 처리
- 비고: 실제 배포 버전과 포함 방식은 기린월드 코어의 빌드 구성을 따릅니다. 원본의 패키지명과 저작권 표시는 원 라이선스 조건에 따라 보존합니다.

### skinview3d

- Copyright (c) 2014-2018 Kent Rasmussen
- Copyright (c) 2017-2022 Haowei Wen, Sean Boult and contributors
- 라이선스: **MIT License**
- 출처: https://github.com/bs-community/skinview3d
- 용도: 스킨 3D 미리보기

### three.js

- Copyright © 2010-2026 three.js authors
- 라이선스: **MIT License**
- 출처: https://github.com/mrdoob/three.js
- 비고: `skinview3d` 번들에 포함될 수 있습니다.

### Lucide

- 저작권: Cole Bemis(Feather, 2013-2022), Lucide Contributors(2022-) 및 각 기여자
- 라이선스: **ISC License**(일부 Feather 구성요소는 MIT License)
- 출처: https://lucide.dev/license
- 용도: UI 아이콘

### Pretendard

- Copyright (c) 2021, Kil Hyung-jin, with Reserved Font Name Pretendard
- 라이선스: **SIL Open Font License 1.1**
- 출처: https://github.com/orioncactus/pretendard

### Pixelify Sans

- 라이선스: **SIL Open Font License 1.1**
- 출처: https://fonts.google.com/specimen/Pixelify+Sans

---

## 3. 빌드 도구

### PyInstaller

- 라이선스: **GPL 2.0 with a special exception**
- 출처: https://github.com/pyinstaller/pyinstaller
- 비고: 부트로더 예외 조항에 따라 PyInstaller로 패키징한 결과물에 GPL이 자동으로 적용되는 것은 아닙니다.

---

## 4. 외부 서비스

윤슬런처는 사용자의 요청을 처리하기 위해 다음 서비스의 공개 API 또는 엔드포인트를 이용할 수 있습니다.

| 서비스 | 용도 |
|---|---|
| Microsoft / Xbox Live 인증 | 계정 로그인 및 사용자 인증 |
| Mojang / Minecraft API | 게임 파일·에셋·Java 런타임 다운로드와 프로필 조회 |
| Modrinth API | 모드·셰이더팩 검색 및 다운로드 |
| Fabric | 모드 로더 설치 |
| GitHub | 윤슬런처 및 기린월드 코어의 배포·업데이트 확인 |

각 서비스와 콘텐츠의 저작권·상표권은 해당 소유자에게 있습니다.

---

## 5. 상표 및 비제휴 고지

- 윤슬런처는 Mojang Studios 또는 Microsoft의 공식 제품이 아니며, 이들로부터 승인·후원·제휴받지 않았습니다.
- NOT AN OFFICIAL MINECRAFT PRODUCT.
- NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.
- Minecraft는 Mojang Synergies AB의 상표입니다.
- Modrinth, Fabric, GitHub 및 그 밖의 명칭과 상표는 각 소유자의 자산입니다.
- 윤슬런처를 사용하려면 정품 Minecraft: Java Edition 계정이 필요합니다.

---

## 6. 라이선스 원문과 누락 사항

각 구성요소의 전체 라이선스 조건은 해당 프로젝트의 원문과 윤슬런처 배포물에 포함된 라이선스 파일을 따릅니다. 실제 배포물에 다른 라이브러리·글꼴·코드가 추가되면 해당 고지도 이 문서에 추가해야 합니다.

윤슬런처 자체의 라이선스는 저장소 루트의 `LICENSE` 파일을 참고하세요. 누락이나 오류는 https://github.com/SEA-S2/Yoonseul_Launcher/issues 에 알려주세요.
