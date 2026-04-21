package com.cloudserver.pi.uploadingfiles;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest 
//Spring injects a fake http client
@AutoConfigureMockMvc
class FileUploadControllerTest {
    
    //this is a fake Http client , simulates a browser sending requests to the Server
    @Autowired
    private MockMvc mockMvc;
    
    
    @Test
    // simulates a logged in User without it the test would fail
    @WithMockUser(username= "felix", roles ="USER")
    void uploadAllowedFile_shouldSucceed() throws Exception{
        //Arrange
        
        //fake uploaded file 
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        //Act
        
        //fake Post request to "/", 
        var result = mockMvc.perform(multipart("/")
                .file(file)
                .param("category", "MATH"));
        
        
        //Assert
        //after a successful upload, get a status which is a redirect like 301, 302 etc.
        result.andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message",
                        "You successfully uploaded test.pdf!"));
        
        
        
        
        
        
        
        
        
    }
}
