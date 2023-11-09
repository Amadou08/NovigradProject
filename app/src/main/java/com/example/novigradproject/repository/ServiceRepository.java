package com.example.novigradproject.repository;

import com.example.novigradproject.model.user.Person;

public interface ServiceRepository {
    void createService(Person person);

    void modifyService(Person person);

    void deleteService(Person person);
}
