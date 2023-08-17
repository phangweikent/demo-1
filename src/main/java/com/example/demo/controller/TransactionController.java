package com.example.demo.controller;

import com.example.demo.model.Customer;
import com.example.demo.model.Transaction;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class TransactionController
{
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        try {
            List<Transaction> Transactions = new ArrayList<>(transactionRepository.findAll());
            if (Transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") long id) {
        Optional<Transaction> TransactionData = transactionRepository.findById(id);

        return TransactionData.map(transaction -> new ResponseEntity<>(transaction, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/transactions/customer/{id}")
    public ResponseEntity<List<Transaction>> getAllTransactionsByCustomerId(@PathVariable("customerId") long customerId) {
        try {
            List<Transaction> Transactions = new ArrayList<>(transactionRepository.findByCustomerId(customerId));
            if (Transactions.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(Transactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(@RequestBody Transaction transaction) {
        try {
            Customer customer = validateCustomer(transaction);
            Transaction _transaction = transactionRepository
                    .save(new Transaction(customer.getId(), customer.getName(), transaction.getAmount()));
            return new ResponseEntity<>(_transaction, HttpStatus.CREATED);
        }
        catch (InvalidCustomerException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable("id") long id, @RequestBody Transaction transaction) {
        try {
            Optional<Transaction> transactionData = transactionRepository.findById(id);

            if (transactionData.isPresent()) {
                Transaction _transaction = transactionData.get();
                //Update customerId or name
                if(transaction.getCustomerId() > 0 || transaction.getName() != null) {
                    Customer customer = validateCustomer(transaction);
                    _transaction.setCustomerId(customer.getId());
                    _transaction.setName(customer.getName());
                }
                _transaction.setAmount(transaction.getAmount() == null ? _transaction.getAmount(): transaction.getAmount());
                return new ResponseEntity<>(transactionRepository.save(_transaction), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        catch (InvalidCustomerException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<HttpStatus> deleteTransaction(@PathVariable("id") long id) {
        try {
            transactionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/transactions")
    public ResponseEntity<HttpStatus> deleteAllTransactions() {
        try {
            transactionRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Customer validateCustomer(Transaction transaction) throws InvalidCustomerException {
        long customerId = transaction.getCustomerId();
        String name = transaction.getName();

        //If both customerId and name is null, validation failed and return null
        if(customerId <= 0 && name == null) throw new InvalidCustomerException("Customer info missing.");

        Customer customer;
        if(customerId > 0) {
            //Validate using CustomerId
            customer = getCustomerById(customerId);

            //If customer not found, throw exception
            if(customer == null) throw new InvalidCustomerException("Customer not found.");

            //Directly return customer record if name is null
            if(name == null ) return customer;

            //validate against customer found
            if(!name.equals(customer.getName())) {
                throw new InvalidCustomerException("Customer record not match.");
            }
        } else {
            //Validate using name
            customer = getCustomerByName(name);

            //If customer not found, throw exception
            if(customer == null) {
                throw new InvalidCustomerException("Customer not found.");
            }
        }
        return customer;
    }

    private Customer getCustomerById(long customerId) {
        Optional<Customer> customerData = customerRepository.findById(customerId);
        return customerData.orElse(null);
    }

    private Customer getCustomerByName(String name) {
        Optional<Customer> customerData = customerRepository.findByName(name);
        return customerData.orElse(null);
    }
}

class InvalidCustomerException extends Exception
{
    public InvalidCustomerException (String str)
    {
        super(str);
    }
}
