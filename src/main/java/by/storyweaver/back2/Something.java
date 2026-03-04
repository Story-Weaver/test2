package by.storyweaver.back2;

import lombok.Data;

@Data
public class Something {
    private String name;
    private int cors;
    private int ram;
    private int rom;
    private String password;

    public boolean isValid() {
        return !(name == null || name.isEmpty() || cors <= 0 || ram <= 0 || rom <= 0 || password == null
                || password.isEmpty());
    }
}