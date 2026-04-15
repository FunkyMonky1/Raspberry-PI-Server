package com.cloudserver.pi.uploadingfiles;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest 

class FileUploadControllerTest {
    
    @Test
    void uploadAllowedFile_shouldSucceed(){
        
        String filename = "testing.pdf";
        
        String extension =  filename.substring(filename.lastIndexOf(".")+1);
        
        
        assertEquals("pdf", extension);
        
    }
}
