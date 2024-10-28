package com.rnd.flink;

import java.io.Serializable;

public class InputData implements Serializable{
    private static final long serialVersionUID = -687991492884005033L;
    private Long timestamp;
    private String name;
    private Integer score;
    private Integer age;
    private String gender;
    private String Id;
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getScore() {
        return score;
    }
    public void setScore(Integer score) {
        this.score = score;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getId() {
        return Id;
    }
    public void setId(String id) {
        Id = id;
    }

    

    public static class Builder {
        private Long timestamp;
        private String name;
        private Integer score;
        private Integer age;
        private String gender;
        private String Id;

        public Builder(String id){
            Id = id;
        }

        public Builder atTimestamp(long ts){
            this.timestamp = ts;
            return this;
        }


        public Builder withName(String name){
            this.name = name;
            return this;
        }


        public Builder withScore(int score){
            this.score = score;
            return this;
        }


        public Builder withAge(int age){
            this.age = age;
            return this;
        }


        public Builder withGender(String gender){
            this.gender = gender;
            return this;
        }

        public InputData build() {
            InputData inputData = new InputData();
            inputData.Id = this.Id;
            inputData.name = this.name;
            inputData.gender = this.gender;
            inputData.score = this.score;
            inputData.timestamp = this.timestamp;
            inputData.age = this.age;
            return inputData;
        }

    }



    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((score == null) ? 0 : score.hashCode());
        result = prime * result + ((age == null) ? 0 : age.hashCode());
        result = prime * result + ((gender == null) ? 0 : gender.hashCode());
        result = prime * result + ((Id == null) ? 0 : Id.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InputData other = (InputData) obj;
        if (timestamp == null) {
            if (other.timestamp != null)
                return false;
        } else if (!timestamp.equals(other.timestamp))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (score == null) {
            if (other.score != null)
                return false;
        } else if (!score.equals(other.score))
            return false;
        if (age == null) {
            if (other.age != null)
                return false;
        } else if (!age.equals(other.age))
            return false;
        if (gender == null) {
            if (other.gender != null)
                return false;
        } else if (!gender.equals(other.gender))
            return false;
        if (Id == null) {
            if (other.Id != null)
                return false;
        } else if (!Id.equals(other.Id))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "InputData [timestamp=" + timestamp + ", name=" + name + ", score=" + score + ", age=" + age
                + ", gender=" + gender + ", Id=" + Id + "]";
    }
    
}
