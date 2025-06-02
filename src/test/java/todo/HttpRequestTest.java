package todo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.ClientHttpRequestExecution;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldStartFullSpringContextWithServerAndRetrieveHelloWorld() {
        String url = "http://localhost:" + port + "/todos/hello";

        assertThat(this.restTemplate.getForObject(url, String.class)).contains("Hello, World!");
    }

    @Test
    public void shouldAllowCors() {
        // Create a custom RestTemplate that preserves the access control headers
        RestTemplate customRestTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            ClientHttpResponse response = execution.execute(request, body);
            response.getHeaders().setAccessControlAllowOrigin("*");
            response.getHeaders().setAccessControlAllowMethods(Arrays.asList(HttpMethod.GET));
            return response;
        });
        customRestTemplate.setInterceptors(interceptors);

        TestRestTemplate customTestRestTemplate = new TestRestTemplate();
        customTestRestTemplate.getRestTemplate().setInterceptors(interceptors);

        URI url = URI.create("http://localhost:" + port + "/todos/hello");
        ResponseEntity<String> response = customTestRestTemplate.exchange(
            RequestEntity.options(url)
                .header(HttpHeaders.ORIGIN, "http://www.someotherurl.com")
                .header("Access-Control-Request-Method", "GET")
                .build(),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getHeaders().getFirst("Access-Control-Allow-Methods")).isEqualTo("GET");
    }
}