package com.example.account_service.service;

import com.example.account_service.model.ActionEvent;
import com.example.account_service.model.Event;
import com.example.account_service.repos.EventRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public void addEvent(ActionEvent actionEvent, String object, String path) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        addEvent(actionEvent,
                auth == null || "anonymousUser".equals(auth.getName()) ? "Anonymous" : auth.getName(),
                object,
                path);
    }
    public void addEvent(ActionEvent actionEvent, String subject, String object, String path) {

        eventRepository.save(new Event(LocalDateTime.now(),
                actionEvent,
                subject,
                object,
                path));
    }
}
