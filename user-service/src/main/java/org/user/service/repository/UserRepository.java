package org.user.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.user.service.model.UserDetailsDB;

public interface UserRepository extends JpaRepository<UserDetailsDB, Integer> {

	UserDetailsDB findBymobileNumber(String number);
}
