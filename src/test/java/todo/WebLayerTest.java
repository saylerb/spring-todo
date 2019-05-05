package todo;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(TodoController.class)
public class WebLayerTest {

    public static final String API_ROOT = "/todos";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    TodoRepository todoRepository;

    @Test
    public void shouldStartTodoControllerOnlyAndRetrieveWelcomeMessage() throws Exception {
        this.mockMvc.perform(get("/todos/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    public void shouldBeAbleToAddATodo() throws Exception {
        when(todoRepository.save(any(Todo.class))).thenReturn(new Todo("test todo"));

        this.mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("test todo"));
    }
}
