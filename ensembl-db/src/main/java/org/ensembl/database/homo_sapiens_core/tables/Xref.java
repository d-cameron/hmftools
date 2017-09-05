/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.enums.XrefInfoType;
import org.ensembl.database.homo_sapiens_core.tables.records.XrefRecord;
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
public class Xref extends TableImpl<XrefRecord> {

    private static final long serialVersionUID = -808191066;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.xref</code>
     */
    public static final Xref XREF = new Xref();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<XrefRecord> getRecordType() {
        return XrefRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.xref.xref_id</code>.
     */
    public final TableField<XrefRecord, UInteger> XREF_ID = createField("xref_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.external_db_id</code>.
     */
    public final TableField<XrefRecord, UInteger> EXTERNAL_DB_ID = createField("external_db_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.dbprimary_acc</code>.
     */
    public final TableField<XrefRecord, String> DBPRIMARY_ACC = createField("dbprimary_acc", org.jooq.impl.SQLDataType.VARCHAR.length(512).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.display_label</code>.
     */
    public final TableField<XrefRecord, String> DISPLAY_LABEL = createField("display_label", org.jooq.impl.SQLDataType.VARCHAR.length(512).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.version</code>.
     */
    public final TableField<XrefRecord, String> VERSION = createField("version", org.jooq.impl.SQLDataType.VARCHAR.length(10), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.description</code>.
     */
    public final TableField<XrefRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.info_type</code>.
     */
    public final TableField<XrefRecord, XrefInfoType> INFO_TYPE = createField("info_type", org.jooq.util.mysql.MySQLDataType.VARCHAR.asEnumDataType(org.ensembl.database.homo_sapiens_core.enums.XrefInfoType.class), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.xref.info_text</code>.
     */
    public final TableField<XrefRecord, String> INFO_TEXT = createField("info_text", org.jooq.impl.SQLDataType.VARCHAR.length(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.xref</code> table reference
     */
    public Xref() {
        this("xref", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.xref</code> table reference
     */
    public Xref(String alias) {
        this(alias, XREF);
    }

    private Xref(String alias, Table<XrefRecord> aliased) {
        this(alias, aliased, null);
    }

    private Xref(String alias, Table<XrefRecord> aliased, Field<?>[] parameters) {
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
    public Identity<XrefRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_XREF;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<XrefRecord> getPrimaryKey() {
        return Keys.KEY_XREF_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<XrefRecord>> getKeys() {
        return Arrays.<UniqueKey<XrefRecord>>asList(Keys.KEY_XREF_PRIMARY, Keys.KEY_XREF_ID_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Xref as(String alias) {
        return new Xref(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Xref rename(String name) {
        return new Xref(name, null);
    }
}