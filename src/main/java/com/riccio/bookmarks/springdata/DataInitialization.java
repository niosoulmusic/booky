/*
package com.riccio.bookmarks.springdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

@Component
public class DataInitialization implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    EntityManager em;

    @Autowired
    EmployeeRepository employeeRepository;

    private static volatile boolean initialized = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (initialized) {
            return;
        } else {
            initialized = true;
        }

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
        for (Employee e : employeeRepository.findByNameLikeOrderByIncome("Wa")){
            System.out.println(" " +e.toString());
        }
        System.out.println("end find by employees name like");



    }
}
*/
