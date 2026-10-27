package kz.iitu.grigoriev.studentapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudentControllerTest {
    @Autowired MockMvc mvc;

    private static final String STUDENT = """
        {"name":"Yanis","surname":"Grigoriev","groupCode":"IT22303","studentNumber":"37038"}
        """;

    @Test void aboutShowsIndividualInformation() throws Exception {
        mvc.perform(get("/api/about"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Yanis"))
            .andExpect(jsonPath("$.studentId").value("37038"));
    }

    @Test void createAndReadStudents() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON).content(STUDENT))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.surname").value("Grigoriev"));
        mvc.perform(get("/api/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test void missingStudentReturns404() throws Exception {
        mvc.perform(get("/api/students/999999"))
            .andExpect(status().isNotFound());
    }

    @Test void deleteStudent() throws Exception {
        String response = mvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = new com.fasterxml.jackson.databind.ObjectMapper().readTree(response).get("id").asLong();
        mvc.perform(delete("/api/students/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/students/{id}", id)).andExpect(status().isNotFound());
    }

    @Test void updateStudent() throws Exception {
        String first = mvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        long id = new com.fasterxml.jackson.databind.ObjectMapper().readTree(first).get("id").asLong();
        mvc.perform(put("/api/students/{id}", id).contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"Yanis\",\"surname\":\"Grigoriev\",\"groupCode\":\"IT22303\",\"studentNumber\":\"37038\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Yanis"));
        mvc.perform(get("/api/students/{id}", id)).andExpect(status().isOk());
    }

    @Test void duplicateStudentNumberReturns409() throws Exception {
        mvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
            .andExpect(status().isCreated());
        mvc.perform(post("/api/students")
            .contentType(MediaType.APPLICATION_JSON).content(STUDENT))
            .andExpect(status().isConflict());
    }

    @Test void invalidStudentReturns400() throws Exception {
        mvc.perform(post("/api/students").contentType(MediaType.APPLICATION_JSON)
            .content("{\"name\":\"\",\"surname\":\"Grigoriev\",\"groupCode\":\"IT22303\",\"studentNumber\":\"37038\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test void demoFailureOnlyWhenExplicitlyEnabled() {
        org.junit.jupiter.api.Assertions.assertNotEquals("1", System.getenv("DEMO_FAIL_PIPELINE"),
            "Intentional failure for Jenkins CI demonstration");
    }
}
