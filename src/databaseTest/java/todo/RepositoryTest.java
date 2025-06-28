package todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ActiveProfiles(profiles = "test")
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @AfterEach
    public void cleanUp() {
        this.todoRepository.deleteAll();
    }

    @Test
    public void shouldSaveATodoAndReadItBack() {
        this.entityManager.persist(new Todo("a todo"));

        Todo readBack = this.todoRepository.findByTitle("a todo");

        assertThat(readBack.getTitle()).isEqualTo("a todo");
    }

    @Test
    public void shouldBeAbleToDeleteATodo() {
        this.entityManager.persist(new Todo("a todo"));

        this.todoRepository.deleteAll();

        List<Todo> allTodos = this.todoRepository.findAll();

        assertThat(allTodos).isEmpty();
    }
}
