package com.github.papayankey.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.papayankey.exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CustomerController.class})
class CustomerControllerTest {
    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateCustomer() throws Exception {
        Customer newCustomer = Customer.builder().firstName("Michael").lastName("Jordan").build();

        Customer savedCustomer = new Customer(1, "Michael", "Jordan");
        when(customerService.createCustomer(newCustomer)).thenReturn(savedCustomer);

        mockMvc.perform(
                        post("/customers")
                                .content(objectMapper.writeValueAsString(newCustomer))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("Michael")));
    }

    @Nested
    @DisplayName("should get all customers")
    class ShouldRetrieveCustomers {
        @Test
        @DisplayName("as empty list given no customer added")
        void shouldReturnEmptyList() throws Exception {
            List<Customer> customers = List.of();
            when(customerService.getCustomers()).thenReturn(customers);

            mockMvc.perform(
                            get("/customers")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(0)));
        }

        @Test
        @DisplayName("as list of three customers")
        void shouldReturnThreeCustomers() throws Exception {
            List<Customer> customers = List.of(
                    Customer.builder().Id(1).firstName("Martin").lastName("Luther").build(),
                    Customer.builder().Id(2).firstName("Dominic").lastName("Yankey").build(),
                    Customer.builder().Id(3).firstName("Jennifer").lastName("Wright").build()
            );
            when(customerService.getCustomers()).thenReturn(customers);

            mockMvc.perform(
                            get("/customers")
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(3)))
                    .andExpect(jsonPath("$[2].lastName", is("Wright")));
        }
    }

    @Nested
    @DisplayName("should get a customer")
    class ShouldGetCustomer {
        @Test
        @DisplayName("given that id exist")
        void shouldReturnACustomer() throws Exception {
            Customer customer = new Customer(1, "Ola", "Rotimi");
            when(customerService.getCustomer(anyInt())).thenReturn(customer);

            mockMvc.perform(
                            get("/customers/{id}", 1)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)));
        }

        @Test
        @DisplayName("given that id does not exist then throw exception")
        void shouldThrowExceptionIfCustomerDoesNotExist() throws Exception {
            Integer customerId = 20;
            when(customerService.getCustomer(anyInt())).thenThrow(new CustomerNotFoundException(customerId));

            mockMvc.perform(
                            get("/customers/{id}", customerId)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(mvResult -> {
                        assertTrue(mvResult.getResolvedException() instanceof CustomerNotFoundException);
                    })
                    .andExpect(jsonPath("$.message", is(String.format("Customer with id %d does not exist", customerId))));
        }
    }

    @Nested
    @DisplayName("should update customer")
    class ShouldUpdateCustomer {
        @Test
        @DisplayName("given firstname and lastname")
        void shouldUpdateCustomerGivenFirstNameAndLastName() throws Exception {
            Integer customerId = 1;
            Customer customer = Customer.builder().firstName("Rebecca").lastName("Yankey").build();
            when(customerService.updateCustomer(customerId, customer)).thenReturn(
                    String.format("Customer with id %d update successful", customerId));

            mockMvc.perform(
                            put("/customers/{id}", customerId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(customer))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("Customer with id 1 update successful")));
        }

        @Test
        @DisplayName("given only firstname")
        void shouldUpdateCustomerGivenFirstName() throws Exception {
            Integer customerId = 9;
            Customer customer = Customer.builder().firstName("Ola").build();

            when(customerService.updateCustomer(customerId, customer)).thenReturn(
                    String.format("Customer with id %d update successful", customerId));

            mockMvc.perform(
                            put("/customers/{id}", customerId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(customer))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("Customer with id 9 update successful")));
        }

        @Test
        @DisplayName("given only lastname")
        void shouldUpdateCustomerGivenLastName() throws Exception {
            Integer customerId = 15;
            Customer customer = Customer.builder().lastName("Rotimi").build();

            when(customerService.updateCustomer(customerId, customer)).thenReturn(
                    String.format("Customer with id %d update successful", customerId));

            mockMvc.perform(
                            put("/customers/{id}", customerId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(customer))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", is("Customer with id 15 update successful")));
        }

        @Test
        @DisplayName("given that id does not exist then throw exception")
        void shouldThrowExceptionWhenIdDoesNotExist() throws Exception {
            Integer customerId = 1;
            Customer customer = Customer.builder().firstName("Godwin").build();
            when(customerService.updateCustomer(customerId, customer)).thenThrow(new CustomerNotFoundException(customerId));

            mockMvc.perform(
                            put("/customers/{id}", customerId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(customer))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", is(String.format("Customer with id %d does not exist", customerId))));
        }
    }
}