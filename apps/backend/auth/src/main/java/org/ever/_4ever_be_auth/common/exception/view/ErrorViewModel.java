package org.ever._4ever_be_auth.common.exception.view;

public record ErrorViewModel(
        String code,
        String title,
        String message,
        String actionLabel,
        String actionUrl,
        String detail
) {
}
