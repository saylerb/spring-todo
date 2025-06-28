package todo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
public class ApplicationTest {

    @Autowired
    private TodoController controller;

    @Test
    public void shouldLoadTheApplicationContext() {
        assertThat(controller).isNotNull();
    }
}
