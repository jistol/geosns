package io.github.jistol.geosns.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Code {
    wrongToken("E001", "wrong token", "token값이 잘못 됫을 경우 반환합니다."),
    recursiveError("E002", "recursive error", "재귀 호출중 오류"),

    kakaoSingupError("K001", "kakao error - can't sign up app", "카카오 로그인중 앱 연결 오류"),
    kakaoEtcError("K999", "kakao error - etc", "카카오 로그인 기타 오류");


    private final String code;
    private final String message;
    private final String description;
}
