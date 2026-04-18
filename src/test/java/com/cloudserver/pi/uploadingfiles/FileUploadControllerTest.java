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
@AutoConfigureMockMvc
class FileUploadControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(username= "felix", roles ="USER")
    void uploadAllowedFile_shouldSucceed() throws Exception{
        //Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "test content".getBytes()
        );
        //Act
        var result = mockMvc.perform(multipart("/")
                .file(file)
                .param("category", "MATH"));
        
        
        //Assert
        result.andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message",
                        "You successfully uploaded test.pdf!"));
        
        
        
        
        
        
        
        
        
    }
}
