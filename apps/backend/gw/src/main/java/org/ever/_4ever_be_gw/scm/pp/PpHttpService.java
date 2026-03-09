package org.ever._4ever_be_gw.scm.pp;

import java.net.URI;
import java.util.List;
import java.util.function.Function;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriBuilder;

public interface PpHttpService {

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardQuotationsToProduction(String userId, Integer size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardProductionInProgress(String userId, Integer size);

    ResponseEntity<Object> get(String operation, String path, Object... uriVariables);

    ResponseEntity<Object> get(String operation, Function<UriBuilder, URI> uriFunction);

    ResponseEntity<Object> postWithoutBody(String operation, String path, Object... uriVariables);

    ResponseEntity<Object> postWithoutBody(String operation, Function<UriBuilder, URI> uriFunction);

    ResponseEntity<Object> post(String operation, String path, Object body, Object... uriVariables);

    ResponseEntity<Object> post(String operation, Function<UriBuilder, URI> uriFunction, Object body);

    ResponseEntity<Object> patch(String operation, String path, Object body, Object... uriVariables);
}
