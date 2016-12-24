package sample;


public class SettingsProfile {
    private int id;
    private String profileName;

    SettingsProfile(int id, String name) {
        this.id = id;
        this.profileName = name;
    }

    int getId() {
        return this.id;
    }

    String getProfileName() {
        return this.profileName;
    }
}
