package org.proteam24.zeroneapplication.config.naming;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class EntityReplaceNamingStrategy extends CamelCaseToUnderscoresNamingStrategy {

    private static final String ENTITY_PART = "Entit(y|ies)";

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return super.toPhysicalTableName(Identifier.toIdentifier(name.getText().replaceFirst(ENTITY_PART,
                        "")),
                jdbcEnvironment);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return super.toPhysicalTableName(Identifier.toIdentifier(name.getText().replaceFirst(ENTITY_PART,
                    "")),
                jdbcEnvironment);
    }
}
