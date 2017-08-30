/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.MiscSetRecord;
import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.UInteger;
import org.jooq.types.UShort;


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
public class MiscSet extends TableImpl<MiscSetRecord> {

    private static final long serialVersionUID = -332175370;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.misc_set</code>
     */
    public static final MiscSet MISC_SET = new MiscSet();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MiscSetRecord> getRecordType() {
        return MiscSetRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.misc_set.misc_set_id</code>.
     */
    public final TableField<MiscSetRecord, UShort> MISC_SET_ID = createField("misc_set_id", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_set.code</code>.
     */
    public final TableField<MiscSetRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR.length(25).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_set.name</code>.
     */
    public final TableField<MiscSetRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_set.description</code>.
     */
    public final TableField<MiscSetRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_set.max_length</code>.
     */
    public final TableField<MiscSetRecord, UInteger> MAX_LENGTH = createField("max_length", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.misc_set</code> table reference
     */
    public MiscSet() {
        this("misc_set", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.misc_set</code> table reference
     */
    public MiscSet(String alias) {
        this(alias, MISC_SET);
    }

    private MiscSet(String alias, Table<MiscSetRecord> aliased) {
        this(alias, aliased, null);
    }

    private MiscSet(String alias, Table<MiscSetRecord> aliased, Field<?>[] parameters) {
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
    public Identity<MiscSetRecord, UShort> getIdentity() {
        return Keys.IDENTITY_MISC_SET;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MiscSetRecord> getPrimaryKey() {
        return Keys.KEY_MISC_SET_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MiscSetRecord>> getKeys() {
        return Arrays.<UniqueKey<MiscSetRecord>>asList(Keys.KEY_MISC_SET_PRIMARY, Keys.KEY_MISC_SET_CODE_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MiscSet as(String alias) {
        return new MiscSet(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MiscSet rename(String name) {
        return new MiscSet(name, null);
    }
}