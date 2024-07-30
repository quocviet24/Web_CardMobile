package com.nishikatakagi.ProductDigital.service_impl;

import com.nishikatakagi.ProductDigital.model.Publisher;
import com.nishikatakagi.ProductDigital.model.User;
import com.nishikatakagi.ProductDigital.repository.PublisherRepository;
import com.nishikatakagi.ProductDigital.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    PublisherRepository publisherRepo;

    @Override
    public List<Publisher> getAllPublisher() {
        List<Publisher> listPublisher = new ArrayList<>();
        listPublisher = publisherRepo.findAll();
        return listPublisher;
    }

    @Override
    public Publisher findPublisherById(int id) {
        try {
            Publisher publisher = publisherRepo.findById(id).get();
            return publisher;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public Publisher findPublisherByName(String name) {
        try {
            Publisher publisher = publisherRepo.findByName(name);
            return publisher;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void savePublisher(Publisher publisher) {
        publisherRepo.save(publisher);
    }

    @Override
    public void inactivePublisher(Publisher publisher, User user) {
        Date date = new Date();
        publisher.setDeletedBy(user.getId());
        publisher.setIsDeleted(true);
        publisher.setDeletedDate(date);
        publisherRepo.save(publisher);
    }

    @Override
    public void activePublisher(Publisher publisher) {
        Date date = new Date();
        publisher.setDeletedBy(null);
        publisher.setIsDeleted(false);
        publisher.setDeletedDate(null);
        publisherRepo.save(publisher);
    }

    @Override
    public List<Publisher> getAllPublisherActive() {
        List<Publisher> listPublisher = new ArrayList<>();
        listPublisher = publisherRepo.findAll();
        List<Publisher> listPublisherActive = new ArrayList<>();

        for (Publisher publisher : listPublisher) {
            if(!publisher.getIsDeleted()){
                listPublisherActive.add(publisher);
            }
        }
        return listPublisherActive;
    }

    @Override
    public List<Publisher> getAllPublisherDeactive() {
        List<Publisher> listPublisher = new ArrayList<>();
        listPublisher = publisherRepo.findAll();
        List<Publisher> listPublisherDeActive = new ArrayList<>();

        for (Publisher publisher : listPublisher) {
            if(publisher.getIsDeleted()){
                listPublisherDeActive.add(publisher);
            }
        }
        return listPublisherDeActive;
    }
}
