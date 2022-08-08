package org.proteam24.zeroneapplication.repository;

import org.proteam24.zeroneapplication.hash.OnLineDialog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OnLineDialogRepository extends CrudRepository<OnLineDialog, Long> {
}
