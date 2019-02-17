package todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TodoRepository todoRepository;

    @After
    public void cleanUp() {
        this.todoRepository.deleteAll();
    }

    @Test
    public void shouldSaveATodoAndReadItBack() {
        this.entityManager.persist(new Todo("a todo"));

        Todo readBack = this.todoRepository.findByTitle("a todo");

        assertThat(readBack.getTitle()).isEqualTo("a todo");
        assertThat(readBack.getUrl()).isInstanceOf(String.class);
        assertThat(Integer.valueOf(readBack.getUrl())).isGreaterThan(0);
    }

    @Test
    public void shouldBeAbleToDeleteATodo() {
        this.entityManager.persist(new Todo("a todo"));

        this.todoRepository.deleteAll();

        List<Todo> allTodos = this.todoRepository.findAll();

        assertThat(allTodos).isEmpty();
    }
}
