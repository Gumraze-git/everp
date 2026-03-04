package org.ever._4ever_be_auth.common.exception.view;

import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class ErrorMessageResolver {

    private final Map<ErrorCode, ErrorTemplate> templates = new EnumMap<>(ErrorCode.class);
    private final ErrorTemplate defaultTemplate;

    public ErrorMessageResolver() {
        defaultTemplate = new ErrorTemplate(
                "문제가 발생했어요",
                "요청을 처리하던 중 문제가 발생했습니다.",
                "홈으로 이동",
                "/"
        );

        templates.put(
                ErrorCode.USER_NOT_FOUND,
                new ErrorTemplate(
                        "계정을 찾을 수 없어요",
                        "입력하신 이메일과 일치하는 계정을 찾을 수 없습니다. 정보를 다시 확인해 주세요.",
                        "재설정 메일 보내기",
                        "/password/reset"
                )
        );

        templates.put(
                ErrorCode.INVALID_TOKEN,
                new ErrorTemplate(
                        "재설정 링크가 유효하지 않아요",
                        "비밀번호 재설정 링크가 잘못됐거나 변조되었을 수 있습니다. 다시 요청해 주세요.",
                        "재설정 링크 다시 받기",
                        "/password/reset"
                )
        );

        templates.put(
                ErrorCode.TOKEN_EXPIRED,
                new ErrorTemplate(
                        "재설정 링크가 만료됐어요",
                        "보내드린 비밀번호 재설정 링크가 만료되었습니다. 새 링크를 요청해 주세요.",
                        "재설정 링크 다시 받기",
                        "/password/reset"
                )
        );

        templates.put(
                ErrorCode.INVALID_PASSWORD,
                new ErrorTemplate(
                        "비밀번호 정책을 확인해 주세요",
                        "설정하려는 비밀번호가 보안 정책을 충족하지 않습니다.",
                        "다시 입력하기",
                        "/password/reset/confirm"
                )
        );

        templates.put(
                ErrorCode.RESOURCE_NOT_FOUND,
                new ErrorTemplate(
                        "페이지를 찾을 수 없어요",
                        "요청하신 페이지가 존재하지 않거나 이동되었을 수 있습니다.",
                        "홈으로 이동",
                        "/"
                )
        );
    }

    public ErrorViewModel resolve(ErrorCode errorCode, String detail) {
        ErrorTemplate template = templates.getOrDefault(errorCode, defaultTemplate);
        return new ErrorViewModel(
                errorCode.name(),
                template.title(),
                template.message(),
                template.actionLabel(),
                template.actionUrl(),
                detail
        );
    }

    private record ErrorTemplate(
            String title,
            String message,
            String actionLabel,
            String actionUrl
    ) {
    }
}
