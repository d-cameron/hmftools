/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.MiscAttribRecord;
import org.jooq.Field;
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
public class MiscAttrib extends TableImpl<MiscAttribRecord> {

    private static final long serialVersionUID = 1418736283;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.misc_attrib</code>
     */
    public static final MiscAttrib MISC_ATTRIB = new MiscAttrib();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MiscAttribRecord> getRecordType() {
        return MiscAttribRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.misc_attrib.misc_feature_id</code>.
     */
    public final TableField<MiscAttribRecord, UInteger> MISC_FEATURE_ID = createField("misc_feature_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGERUNSIGNED)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_attrib.attrib_type_id</code>.
     */
    public final TableField<MiscAttribRecord, UShort> ATTRIB_TYPE_ID = createField("attrib_type_id", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.SMALLINTUNSIGNED)), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.misc_attrib.value</code>.
     */
    public final TableField<MiscAttribRecord, String> VALUE = createField("value", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.misc_attrib</code> table reference
     */
    public MiscAttrib() {
        this("misc_attrib", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.misc_attrib</code> table reference
     */
    public MiscAttrib(String alias) {
        this(alias, MISC_ATTRIB);
    }

    private MiscAttrib(String alias, Table<MiscAttribRecord> aliased) {
        this(alias, aliased, null);
    }

    private MiscAttrib(String alias, Table<MiscAttribRecord> aliased, Field<?>[] parameters) {
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
    public List<UniqueKey<MiscAttribRecord>> getKeys() {
        return Arrays.<UniqueKey<MiscAttribRecord>>asList(Keys.KEY_MISC_ATTRIB_MISC_ATTRIBX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MiscAttrib as(String alias) {
        return new MiscAttrib(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MiscAttrib rename(String name) {
        return new MiscAttrib(name, null);
    }
}
