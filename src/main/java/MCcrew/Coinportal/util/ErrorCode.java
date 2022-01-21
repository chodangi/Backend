package MCcrew.Coinportal.util;

import lombok.Getter;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /** 1000 : 요청 성공 */
    SUCCESS(true, 1000, "요청에 성공했습니다."),

    /** 2000 : 요청 오류*/
    DATABASE_ERROR(false, 2000, "데이터베이스를 불러올 수 없습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private ErrorCode(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}