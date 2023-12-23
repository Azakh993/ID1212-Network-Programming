package se.kth.id1212.springquiz.model;


import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "QUESTIONS")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "TEXT", nullable = false)
    private String text;

    @Column(name = "OPTIONS", nullable = false)
    private String options;

    @Column(name = "ANSWER", nullable = false)
    private String answer;

    @OneToMany(mappedBy = "question")
    private List< Selector > selectors;


    public Question() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}