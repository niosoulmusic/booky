package com.riccio.bookmarks.springdata;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepository extends PagingAndSortingRepository<Employee,Integer> {

        Long countDistinctByNameAndIncome(@Param("name") String name,@Param("income") long income);

        List<Employee> findByNameLikeOrderByIncome(@Param("name")String name);

        List <Employee> findByNameOrIncome(@Param("name") String name,@Param("income" ) Long income);
}
