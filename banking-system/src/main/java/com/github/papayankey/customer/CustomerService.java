package com.github.papayankey.customer;

import com.github.papayankey.exceptions.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomer(Integer id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return optionalCustomer.orElseThrow(() -> {
            throw new CustomerNotFoundException(id);
        });
    }

    public String updateCustomer(Integer id, Customer customer) {
        String firstName = customer.getFirstName();
        String lastName = customer.getLastName();

        Integer count = 0;
        if (firstName != null && lastName != null) {
            count = customerRepository.updateCustomer(id, firstName, lastName);
        } else if (firstName != null) {
            count = customerRepository.updateCustomerByFirstName(id, firstName);
        } else if (lastName != null) {
            count = customerRepository.updateCustomerByLastName(id, lastName);
        }

        if (count != 1) {
            throw new CustomerNotFoundException(id);
        }

        return String.format("Customer with id %d update successful", id);
    }

    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }
}
