package todo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TodoResponseTest {
    @Test
    public void shouldSetAUrlOnTodo() {
        Todo todo = new Todo(1L, "Clean kitchen", false, 1);

        TodoResponse expected = new TodoResponse(1L, "Clean kitchen", false, 1, "/todos/1");

        assertThat(TodoResponse.from(todo)).isEqualToComparingFieldByField(expected);
    }
}