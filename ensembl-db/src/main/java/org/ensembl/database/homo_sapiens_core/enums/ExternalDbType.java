/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.enums;


import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.EnumType;
import org.jooq.Schema;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public enum ExternalDbType implements EnumType {

    ARRAY("ARRAY"),

    ALT_TRANS("ALT_TRANS"),

    ALT_GENE("ALT_GENE"),

    MISC("MISC"),

    LIT("LIT"),

    PRIMARY_DB_SYNONYM("PRIMARY_DB_SYNONYM"),

    ENSEMBL("ENSEMBL");

    private final String literal;

    private ExternalDbType(String literal) {
        this.literal = literal;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "external_db_type";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLiteral() {
        return literal;
    }
}
