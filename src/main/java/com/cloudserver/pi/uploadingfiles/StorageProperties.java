package com.cloudserver.pi.uploadingfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    
    private String location ="C:/Users/felix/Documents/server_files";


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
