package todo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Todo {
    private @Id @GeneratedValue Long id;
    private final String title;

    public Todo(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }
}
