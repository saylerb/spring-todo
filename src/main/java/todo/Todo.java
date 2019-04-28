package todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Todo {
    private @Id @GeneratedValue Long id;
    private String title;
    private boolean completed;

    public Todo() {
        // No args constructor, needed for deserialization
    }

    public Todo(String title) {
        this.title = title;
    }

    public Todo(Long id, String title, boolean completed) {
        this.id = id;
        this.title = title;
        this.completed = completed;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isCompleted() {
        return this.completed;
    }

    @JsonProperty(access = Access.READ_ONLY)
    public String getUrl() {
        return String.format("http://localhost:8080/todos/%s", this.id);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return completed == todo.completed &&
            Objects.equals(id, todo.id) &&
            Objects.equals(title, todo.title);
    }

    @Override public int hashCode() {
        return Objects.hash(id, title, completed);
    }

    public Long getId() {
        return id;
    }
}
