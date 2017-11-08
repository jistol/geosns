package io.github.jistol.geosns.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Code {
    wrongToken("E001", "wrong token", "token값이 잘못 됫을 경우 반환합니다."),
    recursiveError("E002", "recursive error", "재귀 호출중 오류"),
    denyFileAccessUser("E003", "deny access file by logined user", "접근이 허용되지 않은 사용자입니다."),
    denyFileAccessIp("E004", "deny access file on you ip address", "접근이 허용되지 않은 IP입니다."),
    denyFileAccessTimeout("E005", "deny access file by timeout", "접근가능 시간이 지났습니다."),

    etcError("E999", "error by etc...", "기타오류"),

    kakaoSingupError("K001", "kakao error - can't sign up app", "카카오 로그인중 앱 연결 오류"),
    kakaoEtcError("K999", "kakao error - etc", "카카오 로그인 기타 오류");


    private final String code;
    private final String message;
    private final String description;
}
