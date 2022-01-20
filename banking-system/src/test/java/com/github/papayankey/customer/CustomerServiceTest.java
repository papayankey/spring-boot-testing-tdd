package com.github.papayankey.customer;

import com.github.papayankey.exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Autowired
    @InjectMocks
    private CustomerService customerService;

    @Test
    @DisplayName("should add a new customer")
    void shouldCreateCustomer() {
        Customer customer = Customer.builder().Id(1).firstName("John").lastName("Doe").build();

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        assertThat(customerService.createCustomer(customer)).returns(customer, Function.identity());
        assertThat(customerService.createCustomer(customer)).returns(1, Customer::getId);
        assertThat(customerService.createCustomer(customer)).returns("John", Customer::getFirstName);
    }

    @Nested
    @DisplayName("should get all customers")
    class shouldRetrieveCustomers {
        @Test
        @DisplayName("as empty list given no customer added")
        void shouldReturnEmptyList() {
            when(customerRepository.findAll()).thenReturn(new ArrayList<>());

            List<Customer> customers = customerService.getCustomers();

            assertThat(customers.size()).isZero();
        }

        @Test
        @DisplayName("as list of two customers")
        void shouldReturnTwoCustomers() {
            List<Customer> customers = List.of(
                    Customer.builder().Id(1).firstName("Mary").lastName("Blidge").build(),
                    Customer.builder().Id(2).firstName("Michael").lastName("Jackson").build()
            );

            when(customerRepository.findAll()).thenReturn(customers);

            List<Customer> customerList = customerService.getCustomers();

            assertThat(customerList.size()).isEqualTo(2);
            assertThat(customerList).isEqualTo(customers);
            assertThat(customerList.get(1)).returns("Michael", Customer::getFirstName);
        }
    }

    @Nested
    @DisplayName("should get customer")
    class shouldRetrieveCustomer {
        @Test
        @DisplayName("given that id exist")
        void shouldReturnCustomerIfExist() {
            Customer customer = Customer.builder().Id(2).firstName("Rebecca").lastName("Attuah").build();

            Optional<Customer> optionalCustomer = Optional.of(customer);
            when(customerRepository.findById(2)).thenReturn(optionalCustomer);

            assertThat(customerService.getCustomer(2)).isEqualTo(customer);
            assertThat(customerService.getCustomer(2)).returns(2, Customer::getId);
//        assertThat(customerService.getCustomer(2)).matches(Predicate.isEqual(customer));
        }

        @Test
        @DisplayName("given that id does not exist then throw exception")
        void shouldThrowExceptionIfCustomerDoesNotExist() {
            CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
                customerService.getCustomer(10);
            });

            assertThat(exception).isInstanceOf(CustomerNotFoundException.class);
            assertThat(exception.getMessage()).isEqualTo("Customer with id 10 does not exist");
        }
    }

    @Nested
    @DisplayName("should update customer")
    class shouldUpdateCustomer {
        @Captor
        ArgumentCaptor<String> stringCaptor;

        @Captor
        ArgumentCaptor<Integer> integerCaptor;

        @Test
        @DisplayName("given firstname and lastname")
        void shouldUpdateCustomerGivenFirstNameAndLastName() {
            int customerId = 3;
            Customer customer = Customer.builder().firstName("Dominic").lastName("Yankey").build();

            when(customerRepository.updateCustomer(customerId, customer.getFirstName(), customer.getLastName())).thenReturn(1);

            customerService.updateCustomer(customerId, customer);

            verify(customerRepository).updateCustomer(integerCaptor.capture(), stringCaptor.capture(), stringCaptor.capture());

            assertThat(integerCaptor.getValue()).isEqualTo(customerId);
            assertThat(stringCaptor.getAllValues()).containsExactly("Dominic", "Yankey");
        }

        @Test
        @DisplayName("given only firstname")
        void shouldUpdateCustomerGivenFirstName() {
            int customerId = 1;
            Customer customer = Customer.builder().firstName("James").build();

            when(customerRepository.updateCustomerByFirstName(customerId, customer.getFirstName())).thenReturn(1);

            customerService.updateCustomer(customerId, customer);

            verify(customerRepository).updateCustomerByFirstName(integerCaptor.capture(), stringCaptor.capture());

            assertThat(integerCaptor.getValue()).isEqualTo(customerId);
            assertThat(stringCaptor.getValue()).isEqualTo("James");
        }

        @Test
        @DisplayName("given only lastname")
        void shouldUpdateCustomerGivenLastName() {
            int customerId = 8;
            Customer customer = Customer.builder().lastName("Luther").build();

            when(customerRepository.updateCustomerByLastName(customerId, customer.getLastName())).thenReturn(1);

            customerService.updateCustomer(customerId, customer);

            verify(customerRepository).updateCustomerByLastName(integerCaptor.capture(), stringCaptor.capture());

            assertThat(integerCaptor.getValue()).isEqualTo(customerId);
            assertThat(stringCaptor.getValue()).isEqualTo("Luther");
        }

        @Test
        @DisplayName("given that id does not exist then throw exception")
        void shouldThrowExceptionWhenIdDoesNotExist() {
            int customerId = 30;
            Customer customer = Customer.builder().build();

            CustomerNotFoundException exception = assertThrows(CustomerNotFoundException.class, () -> {
                customerService.updateCustomer(customerId, customer);
            });

            assertThat(exception).isInstanceOf(CustomerNotFoundException.class);
            assertThat(exception.getMessage()).isEqualTo("Customer with id 30 does not exist");
        }
    }
}