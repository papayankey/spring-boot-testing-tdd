package com.github.papayankey.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Transactional
    @Modifying
    @Query(value = "" +
            "UPDATE customers " +
            "SET first_name = :firstName, last_name = :lastName " +
            "WHERE id = :id", nativeQuery = true)
    Integer updateCustomer(@Param("id") Integer id, @Param("firstName") String firstName, @Param("lastName") String lastName);

    @Transactional
    @Modifying
    @Query(value = "" +
            "UPDATE customers " +
            "SET first_name = :firstName " +
            "WHERE id = :id", nativeQuery = true)
    Integer updateCustomerByFirstName(@Param("id") Integer id, @Param("firstName") String firstName);

    @Transactional
    @Modifying
    @Query(value = "" +
            "UPDATE customers " +
            "SET last_name = :lastName " +
            "WHERE id = :id", nativeQuery = true)
    Integer updateCustomerByLastName(@Param("id") Integer id, @Param("lastName") String lastName);
}
