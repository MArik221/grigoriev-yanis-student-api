package kz.iitu.grigoriev.studentapi;

import jakarta.persistence.*;

@Entity
@Table(name = "students", uniqueConstraints = @UniqueConstraint(columnNames = "student_number"))
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(name = "group_code", nullable = false)
    private String groupCode;
    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    protected Student() { }
    public Student(String name, String surname, String groupCode, String studentNumber) {
        this.name = name;
        this.surname = surname;
        this.groupCode = groupCode;
        this.studentNumber = studentNumber;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }
    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
}
