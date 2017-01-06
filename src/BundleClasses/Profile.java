package BundleClasses;

/**
 * Bundle class to store a necessary data of a profile
 */
public class Profile {
    private String profileName;
    private int profileId;

    public Profile(int id, String name) {
        this.profileId = id;
        this.profileName = name;
    }

    public String getProfileName() {
        return this.profileName;
    }

    public int getProfileId() {
        return this.profileId;
    }
}
