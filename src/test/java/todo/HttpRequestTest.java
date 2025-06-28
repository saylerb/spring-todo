package todo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

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
        // Test CORS preflight request
        URI url = URI.create("http://localhost:" + port + "/todos/hello");
        ResponseEntity<String> response = restTemplate.exchange(
            RequestEntity.options(url)
                .header(HttpHeaders.ORIGIN, "http://www.someotherurl.com")
                .header("Access-Control-Request-Method", "GET")
                .build(),
            String.class
        );

        // Verify the server responds with appropriate CORS headers
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("Access-Control-Allow-Origin")).isEqualTo("*");
        assertThat(response.getHeaders().getFirst("Access-Control-Allow-Methods")).contains("GET");
    }
}