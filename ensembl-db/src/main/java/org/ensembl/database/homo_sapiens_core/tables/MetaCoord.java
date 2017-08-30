/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.HomoSapiensCore_89_37;
import org.ensembl.database.homo_sapiens_core.Keys;
import org.ensembl.database.homo_sapiens_core.tables.records.MetaCoordRecord;
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
public class MetaCoord extends TableImpl<MetaCoordRecord> {

    private static final long serialVersionUID = 690297196;

    /**
     * The reference instance of <code>homo_sapiens_core_89_37.meta_coord</code>
     */
    public static final MetaCoord META_COORD = new MetaCoord();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MetaCoordRecord> getRecordType() {
        return MetaCoordRecord.class;
    }

    /**
     * The column <code>homo_sapiens_core_89_37.meta_coord.table_name</code>.
     */
    public final TableField<MetaCoordRecord, String> TABLE_NAME = createField("table_name", org.jooq.impl.SQLDataType.VARCHAR.length(40).nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.meta_coord.coord_system_id</code>.
     */
    public final TableField<MetaCoordRecord, UInteger> COORD_SYSTEM_ID = createField("coord_system_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>homo_sapiens_core_89_37.meta_coord.max_length</code>.
     */
    public final TableField<MetaCoordRecord, Integer> MAX_LENGTH = createField("max_length", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * Create a <code>homo_sapiens_core_89_37.meta_coord</code> table reference
     */
    public MetaCoord() {
        this("meta_coord", null);
    }

    /**
     * Create an aliased <code>homo_sapiens_core_89_37.meta_coord</code> table reference
     */
    public MetaCoord(String alias) {
        this(alias, META_COORD);
    }

    private MetaCoord(String alias, Table<MetaCoordRecord> aliased) {
        this(alias, aliased, null);
    }

    private MetaCoord(String alias, Table<MetaCoordRecord> aliased, Field<?>[] parameters) {
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
    public List<UniqueKey<MetaCoordRecord>> getKeys() {
        return Arrays.<UniqueKey<MetaCoordRecord>>asList(Keys.KEY_META_COORD_CS_TABLE_NAME_IDX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaCoord as(String alias) {
        return new MetaCoord(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MetaCoord rename(String name) {
        return new MetaCoord(name, null);
    }
}