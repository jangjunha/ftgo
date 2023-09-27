package me.jangjunha.ftgo.restaurant_service.web;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class HealthController {

    private final EntityManager em;

    @Autowired
    public HealthController(EntityManager em) {
        this.em = em;
    }

    @RequestMapping(path = "/ping/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void health(@RequestParam(required = false, defaultValue = "false") boolean include_db) {
        if (include_db) {
            Query q = em.createNativeQuery("SELECT 1");
            q.getSingleResult();
        }
    }
}
