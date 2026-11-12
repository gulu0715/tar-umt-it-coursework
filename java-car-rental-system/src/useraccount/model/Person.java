package useraccount.model;

public abstract class Person {
    private String id;

    protected Person(String id) {
        this.id = id;
    }

    public String getId() { 
        return id; 
    }

    public void setId(String id) { 
        this.id = id; 
    }
}
