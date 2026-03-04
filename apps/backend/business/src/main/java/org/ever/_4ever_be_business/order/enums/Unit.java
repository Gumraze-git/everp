package org.ever._4ever_be_business.order.enums;

public enum Unit {
    // 개수
    EA,             // Each: 개
    DOZEN,          // 12개 묶음
    PACK,           // 팩(임의 묶음)
    BOX,            // 박스(카톤)
    CASE,           // 케이스(박스 상위/동급 포장)
    SET,            // 세트
    PAIR,           // 켤레
    ROLL,           // 롤
    SHEET,          // 장
    PALLET,         // 팔레트
    BUNDLE,         // 번들
    BAG,            // 가방/포대

    // 무게
    KG,             // 킬로그램
    G,              // 그램
    MG,             // 밀리그램
    TON,            // 톤(미터톤)
    LB,             // 파운드
    OZ,             // 온스(무게)

    // 부피
    L,              // 리터
    ML,             // 밀리리터
    CL,             // 센틸리터
    M3,             // 세제곱미터
    CM3,            // 세제곱센티미터(= mL)
    GAL,            // 갤런(US)
    QT,             // 쿼트(US)
    PT,             // 파인트(US)
    FLOZ,           // 액량 온스(US, fluid ounce)

    // 길이
    M,              // 미터
    CM,             // 센티미터
    MM,             // 밀리미터
    KM,             // 킬로미터
    INCH,           // 인치
    FT,             // 피트
    YD,             // 야드

    // 면적(Area)
    M2,             // 제곱미터
    CM2,            // 제곱센티미터
    MM2,            // 제곱밀리미터
    KM2,            // 제곱킬로미터
    FT2,            // 제곱피트
    YD2,            // 제곱야드

    // 시간(Time) — 프로젝트/용역 과금 등에 유용
    SEC,            // 초
    MIN,            // 분
    HOUR,           // 시간
    DAY             // 일
}