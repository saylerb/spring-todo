package todo;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static todo.WebLayerTest.API_ROOT;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class MockMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @After
    public void cleanUp() {
        todoRepository.deleteAll();
    }

    @Test
    public void shouldStartFullSpringContextWithoutServerAndRetrieveMessage() throws Exception {
        this.mockMvc.perform(get("/"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Hello, World!")));
    }

    @Test
    public void shouldAllowCors() throws Exception {
        this.mockMvc.perform(options("/")
            .header("Access-Control-Request-Method", "GET")
            .header("Origin", "www.somethingelse.com"))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldBeAbleToGetAnEmptyListOfTodos() throws Exception {
        this.mockMvc.perform(
            get(API_ROOT))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("[]"));
    }

    @Test
    public void shouldBeAbleToAddATodo() throws Exception {
        this.mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("test todo"));
    }

    @Test
    public void shouldBeAbleToDeleteAllExistingTodos() throws Exception {
        this.mockMvc.perform(
            delete(API_ROOT))
            .andDo(print())
            .andExpect(status().isOk());
    }

    @Test
    public void shouldSetUpANewTodoAsInitiallyNotCompleted() throws Exception {
        this.mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.completed").value("false"));
    }

    @Test
    public void shouldSetUpANewTodoWithAUniqueUrl() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MvcResult result = this.mockMvc.perform(
            post(API_ROOT)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"title\": \"test todo\" }".getBytes())
                .characterEncoding("utf-8"))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

        Todo createdTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);

        String todoURL = createdTodo.getUrl();

        this.mockMvc.perform(
            get(API_ROOT + "/" + todoURL))
            .andDo(print())
            .andExpect(jsonPath("$.url").value(todoURL));
    }
}
