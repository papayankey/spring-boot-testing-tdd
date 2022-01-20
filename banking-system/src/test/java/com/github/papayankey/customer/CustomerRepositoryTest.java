package com.github.papayankey.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Nested
    @DisplayName("should update customer")
    class UpdateCustomer {
        @Test
        @DisplayName("given firstname and last")
        void shouldUpdateCustomerGivenFirstNameAndLastName() {
            Customer customer = Customer.builder().firstName("Michael").lastName("Park").build();
            int customerId = (int) entityManager.persistAndGetId(customer);
            entityManager.clear();

            String firstName = "Mikel";
            String lastName = "Parker";
            customerRepository.updateCustomer(customerId, firstName, lastName);

            Customer updatedCustomer = entityManager.find(Customer.class, customerId);

            assertThat(updatedCustomer).isNotNull();
            assertThat(updatedCustomer.getFirstName()).isEqualTo(firstName);
            assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
        }

        @Test
        @DisplayName("given firstname")
        void shouldUpdateCustomerGivenFirstName() {
            Customer customer = Customer.builder().firstName("Sylvester").lastName("Stallone").build();
            int customerId = (int) entityManager.persistAndGetId(customer);
            entityManager.clear();

            String firstName = "Chester";
            customerRepository.updateCustomerByFirstName(customerId, firstName);

            Customer updatedCustomer = entityManager.find(Customer.class, customerId);

            assertThat(updatedCustomer).isNotNull();
            assertThat(updatedCustomer.getFirstName()).isEqualTo(firstName);
        }

        @Test
        @DisplayName("given lastname")
        void shouldUpdateCustomerGivenLastName() {
            Customer customer = Customer.builder().firstName("Sylvester").lastName("Stallone").build();
            int customerId = (int) entityManager.persistAndGetId(customer);
            entityManager.clear();

            String lastName = "Waterfall";
            customerRepository.updateCustomerByLastName(customerId, lastName);

            Customer updatedCustomer = entityManager.find(Customer.class, customerId);

            assertThat(updatedCustomer).isNotNull();
            assertThat(updatedCustomer.getLastName()).isEqualTo(lastName);
        }
    }
}