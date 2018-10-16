package com.riccio.bookmarks.config;

import com.riccio.bookmarks.model.Bookmark;
import com.riccio.bookmarks.service.BookmarkService;
import com.riccio.bookmarks.service.UserService;
import com.riccio.bookmarks.springdata.Building;
import com.riccio.bookmarks.springdata.Employee;
import com.riccio.bookmarks.springdata.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Arrays;

@Component
public class DataInitialization implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(DataInitialization.class);
    @Autowired
    UserService userService;

    @Autowired
    BookmarkService bookmarkService;

    @Autowired
    EntityManager em;

    @Autowired
    EmployeeRepository employeeRepository;

    private static volatile boolean initialized = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //It may happen that this is executed more than once
        if (initialized) {
            return;
        } else {
            initialized = true;
        }

        bookmarkService.addBookmark(new Bookmark("Riccio Gege", "http://riccio.com"));
        bookmarkService.addBookmark(new Bookmark("Another website", "http://another.com"));

        userService.addUser(new User("admin","password",
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_ADMIN"),
                        new SimpleGrantedAuthority("ROLE_USER")
                )));
        userService.addUser(new User("user","password",
                Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER")
                )));

        /*test spring data methods*/
        Building headquarter = new Building(null,"My Virtual Company HeadQuarter","Some Street 123","20100","Milan");
        Building subsidiary = new Building(null,"Just a small flat","Another street 456","20100","Milan");
        em.persist(headquarter);
        em.persist(subsidiary);
        Employee ceo = new Employee(null,"Max","Catanzaro",10000L,headquarter);
        em.persist(ceo);
        Employee manager = new Employee(null,"Luke","Manhattan",70000L,headquarter);
        em.persist(manager);
        Employee emp = new Employee(null,"Wahid","Rome",20000L,subsidiary);
        em.persist(emp);

        System.out.println("starting employees list");
        TypedQuery<Employee> query = em.createQuery("select e from Employee e",Employee.class);
        for (Employee e : query.getResultList()){
            System.out.println(" " + e.toString());
        }
        System.out.println("end employees list");

        System.out.println("starting find by employees name or income");
        for (Employee e : employeeRepository.findByNameOrIncome("Max",20000L)){
            System.out.println(" " +e.toString());
        }
        System.out.println("end find by employees name or income");

        System.out.println("starting find by employees name like");
        for (Employee e : employeeRepository.findByNameLikeOrderByIncome("Wa%")){
            System.out.println(" " +e.toString());
        }
        System.out.println("end find by employees name like");


    }
}