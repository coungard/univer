package com.coungard.univer.filter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestIdFilterTest {

  private final RequestIdFilter filter = new RequestIdFilter();

  @Test
  void shouldGenerateRequestIdWhenClientDidNotSendIt() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(new MockHttpServletRequest(), response, (req, res) -> { });

    assertThat(UUID.fromString(response.getHeader(RequestIdFilter.HEADER))).isNotNull();
  }

  @Test
  void shouldReuseRequestIdSentByClient() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(RequestIdFilter.HEADER, "mobile-42.a_b");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, (req, res) -> { });

    assertThat(response.getHeader(RequestIdFilter.HEADER)).isEqualTo("mobile-42.a_b");
  }

  @Test
  void shouldReplaceRequestIdWithUnsafeCharactersOrTooLong() throws Exception {
    for (String unsafe : new String[] {"id\nподделанная строка лога", "x".repeat(65), ""}) {
      MockHttpServletRequest request = new MockHttpServletRequest();
      request.addHeader(RequestIdFilter.HEADER, unsafe);
      MockHttpServletResponse response = new MockHttpServletResponse();

      filter.doFilter(request, response, (req, res) -> { });

      assertThat(response.getHeader(RequestIdFilter.HEADER)).isNotEqualTo(unsafe);
      assertThat(UUID.fromString(response.getHeader(RequestIdFilter.HEADER))).isNotNull();
    }
  }

  @Test
  void shouldPutRequestIdIntoMdcOnlyWhileRequestIsProcessed() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(RequestIdFilter.HEADER, "req-1");
    AtomicReference<String> insideChain = new AtomicReference<>();

    filter.doFilter(request, new MockHttpServletResponse(),
        (req, res) -> insideChain.set(MDC.get(RequestIdFilter.MDC_KEY)));

    assertThat(insideChain.get()).isEqualTo("req-1");
    assertThat(MDC.get(RequestIdFilter.MDC_KEY)).isNull();
  }
}
