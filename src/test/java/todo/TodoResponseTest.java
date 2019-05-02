package todo;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TodoResponseTest {

    @Test
    public void shouldSetAUrlOnTodo() {
        Todo todo = new Todo(1L, "Clean kitchen", false, 1);
        String url = "/some/link/to/todo";

        TodoResponse expected = new TodoResponse(1L, "Clean kitchen", false, 1, url);

        assertThat(TodoResponse.from(todo, url)).isEqualToComparingFieldByField(expected);
    }
}