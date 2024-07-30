package com.nishikatakagi.ProductDigital.service;

import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.model.User;

import java.util.List;

public interface PublisherService {

    public List<Publisher> getAllPublisher();

    public Publisher findPublisherById(int id);

    public Publisher findPublisherByName(String name);

    public void savePublisher(Publisher publisher);

    public void inactivePublisher(Publisher publisher, User user);

    public void activePublisher(Publisher publisher);

    public List<Publisher> getAllPublisherActive();

    public List<Publisher> getAllPublisherDeactive();
}
