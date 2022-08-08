package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.hash.ClientHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientHashRepository extends CrudRepository<ClientHash, Long> {

}
