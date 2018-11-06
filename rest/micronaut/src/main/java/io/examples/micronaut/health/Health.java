package io.examples.micronaut.health;

/**
 * @author Gary Cheng
 */
public class Health {
    private String version;
    private String status;

    public Health() {
    }

    public Health(String version, String status) {
        this.version = version;
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
