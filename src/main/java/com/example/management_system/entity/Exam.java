package com.example.management_system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    //考试开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    //持续时间长度
    private int length=2;
    //教室
    private int classroom;
    //统一考试编号(唯一)
    @Column(unique = true)
    private int number;
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP",
            updatable = false,
            insertable = false)
    private LocalDateTime insertTime;

    public Exam(String name, LocalDateTime startTime, int classroom, int number) {
        this.name = name;
        this.startTime = startTime;
        this.classroom = classroom;
        this.number = number;
    }
}
