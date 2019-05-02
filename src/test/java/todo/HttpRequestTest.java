package todo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

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
        ResponseEntity<String> response = this.restTemplate.exchange(
            RequestEntity.get(
                restTemplate
                    .getRestTemplate()
                    .getUriTemplateHandler()
                    .expand("/todos/hello"))
                .header(HttpHeaders.ORIGIN, "http://www.someotherurl.com").build(),
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Hello, World!");
        assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo("*");
    }
}
