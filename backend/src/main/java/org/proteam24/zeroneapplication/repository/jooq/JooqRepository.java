package org.proteam24.zeroneapplication.repository.jooq;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

@Repository
public class JooqRepository {

    static List nativeQuery(EntityManager em, org.jooq.Query query) {
        // Extract the SQL statement from the jOOQ query:
        Query result = em.createNativeQuery(query.getSQL());

        // Extract the bind values from the jOOQ query:
        List<Object> values = query.getBindValues();
        for (int i = 0; i < values.size(); i++) {
            result.setParameter(i + 1, values.get(i));
        }
        return result.getResultList();
    }

    public <T> List execQuery (EntityManager em, org.jooq.Query jooqQuery, Class<T> entityClass) {
        // Extract the SQL statement from the jOOQ query:
        Query result = em.createNativeQuery(jooqQuery.getSQL(), entityClass);

        // Extract the bind values from the jOOQ query:
        List<Object> values = jooqQuery.getBindValues();
        for (int i = 0; i < values.size(); i++) {
            result.setParameter(i + 1, values.get(i));
        }

        // There's an unsafe cast here, but we can be sure that we'll get the right type from JPA
        return result.getResultList();
    }

    /**
     * Returns the table name for a given entity type in the {@link EntityManager}.
     *
     * @param em          entity manager
     * @param entityClass target class
     * @return table name for entity
     */
    /* Resource: https://stackoverflow.com/a/26420307/16644196 */
    public <T> String getTableName(EntityManager em, Class<T> entityClass) {
        /*
         * Check if the specified class is present in the metamodel.
         * Throws IllegalArgumentException if not.
         */
        Metamodel meta = em.getMetamodel();
        EntityType<T> entityType = meta.entity(entityClass);

        //Check whether @Table annotation is present on the class.
        Table t = entityClass.getAnnotation(Table.class);

        return (t == null)
                ? entityType.getName().toUpperCase()
                : t.name();
    }

    public Condition makeLikeCondition(String field, String text) {
        Condition result = DSL.falseCondition();
        if (text.isBlank()) {
            return DSL.trueCondition();
        } else if (!text.contains("_")) {
            return DSL.field(field).like("%" + text + "%");
        } else {
            String[] tags = text.split("_");
            for (String tag : tags) {
                result = result.or(makeLikeCondition(field, tag));
            }
        }
        return result;
    }

    public Condition makeMoreLessTimeCondition(String field, String dateFrom, String dateTo) {
        return dateFrom.isBlank() ? DSL.trueCondition() : DSL.field(field).gt(getLocalDateTime(dateFrom))
                .and(dateTo.isBlank() ? DSL.trueCondition() : DSL.field(field).lt(getLocalDateTime(dateTo)));
    }

    public LocalDateTime getLocalDateTime(String unixTime) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(unixTime)),
                TimeZone.getDefault().toZoneId());
    }

}
