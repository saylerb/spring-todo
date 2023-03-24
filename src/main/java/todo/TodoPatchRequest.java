package todo;

public class TodoPatchRequest {
    private String title;
    private Boolean completed;
    private Integer order;


    public TodoPatchRequest() {
    }

    public TodoPatchRequest(String title, Boolean completed, Integer order) {
        this.title = title;
        this.completed = completed;
        this.order = order;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
