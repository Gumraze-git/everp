//package org.ever._4ever_be_gw.mockdata.scmpp.impl;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
//import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
//import org.ever._4ever_be_gw.scm.mm.dto.SupplierCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.service.MmService;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MmServiceImpl implements MmService {
//
//    private final WebClientProvider webClientProvider;
//
//    @Override
//    public Mono<SupplierCreateRequestDto> createSupplier(SupplierCreateRequestDto requestDto) {
//        WebClient scmPpClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);
//
//        return scmPpClient.post()
//                .uri("/mm/suppliers")
//                .bodyValue(requestDto)
//                .retrieve()
//                .bodyToMono(SupplierCreateRequestDto.class)
//                .doOnSuccess(response ->
//                        log.info("[INFO] 공급사 등록 및 담당자 생성 성공. supplier:{}, manager: {}", response.getSupplierInfo().getSupplierName(), response.getManagerInfo().getManagerName())
//                )
//                .onErrorResume(WebClientResponseException.class, ex -> {
//                    log.error("[ERROR] SCM_PP 서버 오류 - status: {}, body: {}",
//                            ex.getStatusCode(), ex.getResponseBodyAsString());
//                    return Mono.error(new RuntimeException("공급사 등록 및 담당자 생성 중 오류가 발생했습니다. : " + ex.getMessage()));
//                });
//    }
//}
