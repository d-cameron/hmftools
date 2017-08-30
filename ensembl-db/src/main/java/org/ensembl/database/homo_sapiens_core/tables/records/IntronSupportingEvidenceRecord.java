/*
 * This file is generated by jOOQ.
*/
package org.ensembl.database.homo_sapiens_core.tables.records;


import java.math.BigDecimal;

import javax.annotation.Generated;

import org.ensembl.database.homo_sapiens_core.enums.IntronSupportingEvidenceScoreType;
import org.ensembl.database.homo_sapiens_core.tables.IntronSupportingEvidence;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;
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
public class IntronSupportingEvidenceRecord extends UpdatableRecordImpl<IntronSupportingEvidenceRecord> implements Record10<UInteger, UShort, UInteger, UInteger, UInteger, Byte, String, BigDecimal, IntronSupportingEvidenceScoreType, Byte> {

    private static final long serialVersionUID = -771224864;

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.intron_supporting_evidence_id</code>.
     */
    public void setIntronSupportingEvidenceId(UInteger value) {
        set(0, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.intron_supporting_evidence_id</code>.
     */
    public UInteger getIntronSupportingEvidenceId() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.analysis_id</code>.
     */
    public void setAnalysisId(UShort value) {
        set(1, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.analysis_id</code>.
     */
    public UShort getAnalysisId() {
        return (UShort) get(1);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_id</code>.
     */
    public void setSeqRegionId(UInteger value) {
        set(2, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_id</code>.
     */
    public UInteger getSeqRegionId() {
        return (UInteger) get(2);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_start</code>.
     */
    public void setSeqRegionStart(UInteger value) {
        set(3, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_start</code>.
     */
    public UInteger getSeqRegionStart() {
        return (UInteger) get(3);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_end</code>.
     */
    public void setSeqRegionEnd(UInteger value) {
        set(4, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_end</code>.
     */
    public UInteger getSeqRegionEnd() {
        return (UInteger) get(4);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_strand</code>.
     */
    public void setSeqRegionStrand(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.seq_region_strand</code>.
     */
    public Byte getSeqRegionStrand() {
        return (Byte) get(5);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.hit_name</code>.
     */
    public void setHitName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.hit_name</code>.
     */
    public String getHitName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.score</code>.
     */
    public void setScore(BigDecimal value) {
        set(7, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.score</code>.
     */
    public BigDecimal getScore() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.score_type</code>.
     */
    public void setScoreType(IntronSupportingEvidenceScoreType value) {
        set(8, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.score_type</code>.
     */
    public IntronSupportingEvidenceScoreType getScoreType() {
        return (IntronSupportingEvidenceScoreType) get(8);
    }

    /**
     * Setter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.is_splice_canonical</code>.
     */
    public void setIsSpliceCanonical(Byte value) {
        set(9, value);
    }

    /**
     * Getter for <code>homo_sapiens_core_89_37.intron_supporting_evidence.is_splice_canonical</code>.
     */
    public Byte getIsSpliceCanonical() {
        return (Byte) get(9);
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
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<UInteger, UShort, UInteger, UInteger, UInteger, Byte, String, BigDecimal, IntronSupportingEvidenceScoreType, Byte> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<UInteger, UShort, UInteger, UInteger, UInteger, Byte, String, BigDecimal, IntronSupportingEvidenceScoreType, Byte> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.INTRON_SUPPORTING_EVIDENCE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UShort> field2() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.ANALYSIS_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field3() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SEQ_REGION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field4() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SEQ_REGION_START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field5() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SEQ_REGION_END;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SEQ_REGION_STRAND;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.HIT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field8() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SCORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<IntronSupportingEvidenceScoreType> field9() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.SCORE_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field10() {
        return IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE.IS_SPLICE_CANONICAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getIntronSupportingEvidenceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UShort value2() {
        return getAnalysisId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value3() {
        return getSeqRegionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value4() {
        return getSeqRegionStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value5() {
        return getSeqRegionEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getSeqRegionStrand();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getHitName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value8() {
        return getScore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceScoreType value9() {
        return getScoreType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value10() {
        return getIsSpliceCanonical();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value1(UInteger value) {
        setIntronSupportingEvidenceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value2(UShort value) {
        setAnalysisId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value3(UInteger value) {
        setSeqRegionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value4(UInteger value) {
        setSeqRegionStart(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value5(UInteger value) {
        setSeqRegionEnd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value6(Byte value) {
        setSeqRegionStrand(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value7(String value) {
        setHitName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value8(BigDecimal value) {
        setScore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value9(IntronSupportingEvidenceScoreType value) {
        setScoreType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord value10(Byte value) {
        setIsSpliceCanonical(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IntronSupportingEvidenceRecord values(UInteger value1, UShort value2, UInteger value3, UInteger value4, UInteger value5, Byte value6, String value7, BigDecimal value8, IntronSupportingEvidenceScoreType value9, Byte value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached IntronSupportingEvidenceRecord
     */
    public IntronSupportingEvidenceRecord() {
        super(IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE);
    }

    /**
     * Create a detached, initialised IntronSupportingEvidenceRecord
     */
    public IntronSupportingEvidenceRecord(UInteger intronSupportingEvidenceId, UShort analysisId, UInteger seqRegionId, UInteger seqRegionStart, UInteger seqRegionEnd, Byte seqRegionStrand, String hitName, BigDecimal score, IntronSupportingEvidenceScoreType scoreType, Byte isSpliceCanonical) {
        super(IntronSupportingEvidence.INTRON_SUPPORTING_EVIDENCE);

        set(0, intronSupportingEvidenceId);
        set(1, analysisId);
        set(2, seqRegionId);
        set(3, seqRegionStart);
        set(4, seqRegionEnd);
        set(5, seqRegionStrand);
        set(6, hitName);
        set(7, score);
        set(8, scoreType);
        set(9, isSpliceCanonical);
    }
}