package telran.java53.accounting.dao;

import java.util.stream.Stream;


import org.springframework.data.mongodb.repository.MongoRepository;

import telran.java53.accounting.model.UserAccount;


public interface UserAccountRepository extends MongoRepository<UserAccount, String> {
	Stream<UserAccount> getAllBy();//is it needed to get findById working?
}	
