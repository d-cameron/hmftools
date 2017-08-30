/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.ExternalSynonymRecord;
import org.jooq.Field;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;


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
public class ExternalSynonym extends TableImpl<ExternalSynonymRecord> {

    private static final long serialVersionUID = 1535556133;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.external_synonym</code>
     */
    public static final ExternalSynonym EXTERNAL_SYNONYM = new ExternalSynonym();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ExternalSynonymRecord> getRecordType() {
        return ExternalSynonymRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.external_synonym.xref_id</code>.
     */
    public final TableField<ExternalSynonymRecord, UInteger> XREF_ID = createField("xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.external_synonym.synonym</code>.
     */
    public final TableField<ExternalSynonymRecord, String> SYNONYM = createField("synonym", org.jooq.impl.SQLDataType.VARCHAR.length(100).nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.external_synonym</code> table reference
     */
    public ExternalSynonym() {
        this("external_synonym", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.external_synonym</code> table reference
     */
    public ExternalSynonym(String alias) {
        this(alias, EXTERNAL_SYNONYM);
    }

    private ExternalSynonym(String alias, Table<ExternalSynonymRecord> aliased) {
        this(alias, aliased, null);
    }

    private ExternalSynonym(String alias, Table<ExternalSynonymRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return HomoSapiensCore_89_37.HOMO_SAPIENS_CORE_89_37;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ExternalSynonymRecord> getPrimaryKey() {
        return Keys.KEY_EXTERNAL_SYNONYM_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ExternalSynonymRecord>> getKeys() {
        return Arrays.<UniqueKey<ExternalSynonymRecord>>asList(Keys.KEY_EXTERNAL_SYNONYM_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExternalSynonym as(String alias) {
        return new ExternalSynonym(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ExternalSynonym rename(String name) {
        return new ExternalSynonym(name, null);
    }
}