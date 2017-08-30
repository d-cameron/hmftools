/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.MarkerSynonym;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;
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
public class MarkerSynonymRecord extends UpdatableRecordImpl<MarkerSynonymRecord> implements Record4<UInteger, UInteger, String, String> {

    private static final long serialVersionUID = 751049378;

    /**
     * Setter for <code>homo_sapiens_core_89_37.marker_synonym.marker_synonym_id</code>.
     */
    public void setMarkerSynonymId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.marker_synonym.marker_synonym_id</code>.
     */
    public UInteger getMarkerSynonymId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.marker_synonym.marker_id</code>.
     */
    public void setMarkerId(UInteger value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.marker_synonym.marker_id</code>.
     */
    public UInteger getMarkerId() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.marker_synonym.source</code>.
     */
    public void setSource(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.marker_synonym.source</code>.
     */
    public String getSource() {
        return (String) get(2);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.marker_synonym.name</code>.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.marker_synonym.name</code>.
     */
    public String getName() {
        return (String) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<UInteger, UInteger, String, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<UInteger, UInteger, String, String> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return MarkerSynonym.MARKER_SYNONYM.MARKER_SYNONYM_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return MarkerSynonym.MARKER_SYNONYM.MARKER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return MarkerSynonym.MARKER_SYNONYM.SOURCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return MarkerSynonym.MARKER_SYNONYM.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getMarkerSynonymId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value2() {
        return getMarkerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MarkerSynonymRecord value1(UInteger value) {
        setMarkerSynonymId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MarkerSynonymRecord value2(UInteger value) {
        setMarkerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MarkerSynonymRecord value3(String value) {
        setSource(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MarkerSynonymRecord value4(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MarkerSynonymRecord values(UInteger value1, UInteger value2, String value3, String value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MarkerSynonymRecord
     */
    public MarkerSynonymRecord() {
        super(MarkerSynonym.MARKER_SYNONYM);
    }

    /**
     * Create a detached, initialised MarkerSynonymRecord
     */
    public MarkerSynonymRecord(UInteger markerSynonymId, UInteger markerId, String source, String name) {
        super(MarkerSynonym.MARKER_SYNONYM);

        set(0, markerSynonymId);
        set(1, markerId);
        set(2, source);
        set(3, name);
    }
}