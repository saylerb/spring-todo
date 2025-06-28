package todo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Entity
public class Todo {
    private @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Long id;
    private String title;
    private boolean completed;
    private Integer orderNumber;
    /**
     * No args constructor, needed for deserialization
     */
    public Todo() {
    }

    public Todo(String title) {
        this.title = title;
    }

    public Todo(Long id, String title, boolean completed, Integer orderNumber) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.orderNumber = orderNumber;
    }

    static Todo from(TodoPatchRequest updates, Todo existing) {
        return new Todo(
                existing.getId(),
                Optional.ofNullable(updates.getTitle()).orElse(existing.getTitle()),
                Optional.ofNullable(updates.getCompleted()).orElseGet(existing::isCompleted),
                updates.getOrder()
        );
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return completed == todo.completed &&
                Objects.equals(id, todo.id) &&
                Objects.equals(title, todo.title) &&
                Objects.equals(orderNumber, todo.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, completed, orderNumber);
    }

    public Long getId() {
        return id;
    }

    @JsonProperty(value = "order") // "order" is reserved in h2 TODO: Add request object
    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getUrl() {
        return linkTo(TodoController.class).slash(this.getId()).withSelfRel().getHref();
    }
}
