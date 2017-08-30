/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.tables.RepeatConsensus;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
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
public class RepeatConsensusRecord extends UpdatableRecordImpl<RepeatConsensusRecord> implements Record5<UInteger, String, String, String, String> {

    private static final long serialVersionUID = -500081724;

    /**
     * Setter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_consensus_id</code>.
     */
    public void setRepeatConsensusId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_consensus_id</code>.
     */
    public UInteger getRepeatConsensusId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_name</code>.
     */
    public void setRepeatName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_name</code>.
     */
    public String getRepeatName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_class</code>.
     */
    public void setRepeatClass(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_class</code>.
     */
    public String getRepeatClass() {
        return (String) get(2);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_type</code>.
     */
    public void setRepeatType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_type</code>.
     */
    public String getRepeatType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_consensus</code>.
     */
    public void setRepeatConsensus(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.repeat_consensus.repeat_consensus</code>.
     */
    public String getRepeatConsensus() {
        return (String) get(4);
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
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return RepeatConsensus.REPEAT_CONSENSUS.REPEAT_CONSENSUS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return RepeatConsensus.REPEAT_CONSENSUS.REPEAT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return RepeatConsensus.REPEAT_CONSENSUS.REPEAT_CLASS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return RepeatConsensus.REPEAT_CONSENSUS.REPEAT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return RepeatConsensus.REPEAT_CONSENSUS.REPEAT_CONSENSUS_;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getRepeatConsensusId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getRepeatName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getRepeatClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getRepeatType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getRepeatConsensus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord value1(UInteger value) {
        setRepeatConsensusId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord value2(String value) {
        setRepeatName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord value3(String value) {
        setRepeatClass(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord value4(String value) {
        setRepeatType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord value5(String value) {
        setRepeatConsensus(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatConsensusRecord values(UInteger value1, String value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RepeatConsensusRecord
     */
    public RepeatConsensusRecord() {
        super(RepeatConsensus.REPEAT_CONSENSUS);
    }

    /**
     * Create a detached, initialised RepeatConsensusRecord
     */
    public RepeatConsensusRecord(UInteger repeatConsensusId, String repeatName, String repeatClass, String repeatType, String repeatConsensus) {
        super(RepeatConsensus.REPEAT_CONSENSUS);

        set(0, repeatConsensusId);
        set(1, repeatName);
        set(2, repeatClass);
        set(3, repeatType);
        set(4, repeatConsensus);
    }
}