package io.github.jistol.geosns.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static io.github.jistol.geosns.util.Cast.entry;
import static io.github.jistol.geosns.util.Cast.map;

@RequiredArgsConstructor
public enum Code {
    success("0000", "success", "성공"),
    wrongToken("E001", "wrong token", "token값이 잘못 됫을 경우 반환합니다."),
    recursiveError("E002", "recursive error", "재귀 호출중 오류"),
    denyFileAccessUser("E003", "deny access file by logined user", "접근이 허용되지 않은 사용자입니다."),
    denyFileAccessIp("E004", "deny access file on you ip address", "접근이 허용되지 않은 IP입니다."),
    denyFileAccessTimeout("E005", "deny access file by timeout", "접근가능 시간이 지났습니다."),
    requiredLogin("E006", "required user login", "로그인이 필요한 서비스입니다."),

    postViewFail("P001", "POST does not exist or you do not have permission to view it.", "존재하지 않는 POST이거나 보기 권한이 없음."),

    etcError("E999", "error by etc...", "기타오류"),

    kakaoSingupError("K001", "kakao error - can't sign up app", "카카오 로그인중 앱 연결 오류"),
    kakaoEtcError("K999", "kakao error - etc", "카카오 로그인 기타 오류");


    public final String code;
    public final String message;
    public final String description;



    public Map<String, Object> toMap() {
        return map(entry("code", code), entry("msg", message));
    }
}
