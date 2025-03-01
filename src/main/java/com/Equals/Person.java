package com.Equals;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author xiehang
 * @date 2024/6/27 18:08
 */
@Getter
@Setter
public class Person {
    private int id;
    private String name;
    private int age;

    public Person() {
    }

    public Person(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id && age == person.age && Objects.equals(name, person.name);
    }
}
