/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.MappingSetRecord;
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
public class MappingSet extends TableImpl<MappingSetRecord> {

    private static final long serialVersionUID = 1634301860;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.mapping_set</code>
     */
    public static final MappingSet MAPPING_SET = new MappingSet();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MappingSetRecord> getRecordType() {
        return MappingSetRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.mapping_set.mapping_set_id</code>.
     */
    public final TableField<MappingSetRecord, UInteger> MAPPING_SET_ID = createField("mapping_set_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.mapping_set.internal_schema_build</code>.
     */
    public final TableField<MappingSetRecord, String> INTERNAL_SCHEMA_BUILD = createField("internal_schema_build", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.mapping_set.external_schema_build</code>.
     */
    public final TableField<MappingSetRecord, String> EXTERNAL_SCHEMA_BUILD = createField("external_schema_build", org.jooq.impl.SQLDataType.VARCHAR.length(20).nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.mapping_set</code> table reference
     */
    public MappingSet() {
        this("mapping_set", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.mapping_set</code> table reference
     */
    public MappingSet(String alias) {
        this(alias, MAPPING_SET);
    }

    private MappingSet(String alias, Table<MappingSetRecord> aliased) {
        this(alias, aliased, null);
    }

    private MappingSet(String alias, Table<MappingSetRecord> aliased, Field<?>[] parameters) {
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
    public UniqueKey<MappingSetRecord> getPrimaryKey() {
        return Keys.KEY_MAPPING_SET_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MappingSetRecord>> getKeys() {
        return Arrays.<UniqueKey<MappingSetRecord>>asList(Keys.KEY_MAPPING_SET_PRIMARY, Keys.KEY_MAPPING_SET_MAPPING_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MappingSet as(String alias) {
        return new MappingSet(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MappingSet rename(String name) {
        return new MappingSet(name, null);
    }
}