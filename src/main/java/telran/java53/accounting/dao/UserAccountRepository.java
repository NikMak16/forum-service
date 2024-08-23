package telran.java53.accounting.dao;

import java.util.stream.Stream;


import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java53.accounting.model.User;


public interface UserAccountRepository extends MongoRepository<User, String> {
	Stream<User> getAllBy();//is it needed to get findById working?
}	
