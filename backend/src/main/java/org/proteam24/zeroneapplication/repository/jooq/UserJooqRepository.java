package org.proteam24.zeroneapplication.repository.jooq;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.proteam24.zeroneapplication.entity.PostEntity;
import org.proteam24.zeroneapplication.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static org.jooq.impl.DSL.field;

@Repository
public class UserJooqRepository extends JooqRepository {

    private final EntityManager entityManager;

    public UserJooqRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<UserEntity> getCountTotal(Long searcherId, String firstName, String lastName, String country, String city) {
        Condition condition  = getCondition(searcherId, firstName, lastName, country, city);

        org.jooq.Query jooqQuery = DSL.select()
                .from(getTableName(entityManager, UserEntity.class))
                .where(condition)
                ;
        return execQuery(entityManager, jooqQuery, UserEntity.class);
    }

    public List<UserEntity> getUsersByMultiField(Long searcherId, String firstName, String lastName, String country, String city, Integer itemPerPage, Integer offset) {
        Condition condition  = getCondition(searcherId, firstName, lastName, country, city);

        org.jooq.Query jooqQuery = DSL.select()
                .from(getTableName(entityManager, UserEntity.class))
                .where(condition)
                .orderBy(field("last_name"), field("first_name"), field("id"))
                .limit(itemPerPage)
                .offset( offset)
                ;
        return execQuery(entityManager, jooqQuery, UserEntity.class);
    }

    private Condition getCondition(Long searcherId, String firstName, String lastName, String country, String city) {

        return makeLikeCondition("first_name", firstName)
                .and(makeLikeCondition("last_name", lastName))
                .and(makeLikeCondition("country", country))
                .and(makeLikeCondition("city", city))
                .and(field("deleted").eq(Boolean.FALSE))
                .and(field("id").ne(searcherId))
                ;
    }
}
