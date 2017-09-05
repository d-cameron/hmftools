/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.MetaRecord;
import org.jooq.Field;
import org.jooq.Identity;
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
public class Meta extends TableImpl<MetaRecord> {

    private static final long serialVersionUID = 2099102002;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.meta</code>
     */
    public static final Meta META = new Meta();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MetaRecord> getRecordType() {
        return MetaRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.meta.meta_id</code>.
     */
    public final TableField<MetaRecord, Integer> META_ID = createField("meta_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.meta.species_id</code>.
     */
    public final TableField<MetaRecord, UInteger> SPECIES_ID = createField("species_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.meta.meta_key</code>.
     */
    public final TableField<MetaRecord, String> META_KEY = createField("meta_key", org.jooq.impl.SQLDataType.VARCHAR.length(40).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.meta.meta_value</code>.
     */
    public final TableField<MetaRecord, String> META_VALUE = createField("meta_value", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.meta</code> table reference
     */
    public Meta() {
        this("meta", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.meta</code> table reference
     */
    public Meta(String alias) {
        this(alias, META);
    }

    private Meta(String alias, Table<MetaRecord> aliased) {
        this(alias, aliased, null);
    }

    private Meta(String alias, Table<MetaRecord> aliased, Field<?>[] parameters) {
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
    public Identity<MetaRecord, Integer> getIdentity() {
        return Keys.IDENTITY_META;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MetaRecord> getPrimaryKey() {
        return Keys.KEY_META_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MetaRecord>> getKeys() {
        return Arrays.<UniqueKey<MetaRecord>>asList(Keys.KEY_META_PRIMARY, Keys.KEY_META_SPECIES_KEY_VALUE_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Meta as(String alias) {
        return new Meta(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Meta rename(String name) {
        return new Meta(name, null);
    }
}