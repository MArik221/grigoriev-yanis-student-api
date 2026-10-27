package kz.iitu.grigoriev.studentapi;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class StudentController {
    private final StudentRepository repository;

    @Value("${app.version:1.0-37038}")
    private String version;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/about")
    public Map<String, String> about() {
        return Map.of("name", "Yanis", "surname", "Grigoriev", "group", "IT22303", "studentId", "37038", "version", version);
    }

    @PostMapping("/api/students")
    @ResponseStatus(HttpStatus.CREATED)
    public Student create(@Valid @RequestBody StudentRequest request) {
        if (repository.existsByStudentNumber(request.studentNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student number already exists");
        }
        return repository.save(new Student(request.name(), request.surname(), request.groupCode(), request.studentNumber()));
    }

    @GetMapping("/api/students")
    public List<Student> all() { return repository.findAll(); }

    @GetMapping("/api/students/{id}")
    public Student one(@PathVariable Long id) { return findOr404(id); }

    @DeleteMapping("/api/students/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Student student = findOr404(id);
        repository.delete(student);
    }

    private Student findOr404(Long id) {
        return repository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found: " + id));
    }
}
