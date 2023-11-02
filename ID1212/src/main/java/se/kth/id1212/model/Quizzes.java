package se.kth.id1212.model;

public class Quizzes {
    private Integer id;
    private String subject;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public class Questions {
        private Integer id;
        private String question;
        private String answer;
        private String[] options;

        public void setId(Integer id) {
            this.id = id;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public void setOptions(String[] options) {
            this.options = options;
        }

        public Integer getId() {
            return id;
        }

        public String getQuestion() {
            return question;
        }

        public String getAnswer() {
            return answer;
        }

        public String[] getOptions() {
            return options;
        }
    }

    public class Selector {
        private Integer id;
        private Integer quizId;
        private Integer questionId;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public void setQuizId(Integer quizId) {
            this.quizId = quizId;
        }

        public void setQuestionId(Integer questionId) {
            this.questionId = questionId;
        }

        public Integer getQuizId() {
            return quizId;
        }

        public Integer getQuestionId() {
            return questionId;
        }
    }
}
