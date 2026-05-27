package com.cloudserver.pi.uploadingfiles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest 
//Spring injects a fake http client
@AutoConfigureMockMvc
class FileUploadControllerTest {
    
    //this is a fake Http client , simulates a browser sending requests to the Server
    @Autowired
    private MockMvc mockMvc;
    
    
    @Test
    @DisplayName("Successful upload with a  valid file")
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
                .param("category", "MATH")
                .with(csrf()));


        //Assert
        //after a successful upload, get a status which is a redirect like 301, 302 etc.
        result.andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message",
                        "You successfully uploaded test.pdf!"));
    }

    @Test
    @DisplayName("Upload fails when a blocked extension is provided")
    @WithMockUser(username = "felix", roles = "USER")
    void uploadBlockedFile_shouldBeRejected() throws Exception {
        // Arrange, create a file with a blocked extension
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "virus.exe",
                "application/octet-stream",
                "malicious content".getBytes()
        );

        // Act, tries to upload the blocked file
        var result = mockMvc.perform(multipart("/")
                .file(file)
                .param("category", "MATH")
                .with(csrf()));

        // Assert, should redirect back with error message
        result.andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message",
                        "File type not allowed! Allowed types: [pdf, png, jpg, jpeg, txt, docx, xlsx, pptx, zip]"));
    }

    @Test
    @DisplayName("Unauthenticated request to homepage redirects to login")
    @WithAnonymousUser
    void unauthenticatedAccess_shouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

}
