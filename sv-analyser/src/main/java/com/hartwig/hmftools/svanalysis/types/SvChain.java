package com.hartwig.hmftools.svanalysis.types;

import static java.lang.Math.abs;

import static com.hartwig.hmftools.svanalysis.analysis.SvUtilities.calcConsistency;

import java.util.List;

import com.google.common.collect.Lists;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SvChain {

    private int mId;
    private List<SvVarData> mSvList;

    // has an entry for each SV, indicating which end of the SV links to the preceding SV / link
    private List<Boolean> mSvStartIsStartLink;

    private List<SvLinkedPair> mLinkedPairs;
    private int mLength;
    private boolean mIsClosedLoop;
    private boolean mIsValid;
    private boolean mHasReplicatedSVs;

    private String mStartFinishChromosome;
    private String mStartFinishArm;

    private int mConsistencyCount;

    private static final Logger LOGGER = LogManager.getLogger(SvChain.class);

    public SvChain(int chainId)
    {
        mId = chainId;
        mSvList = Lists.newArrayList();
        mLinkedPairs = Lists.newArrayList();
        mSvStartIsStartLink = Lists.newArrayList();
        mLength = 0;
        mIsClosedLoop = false;
        mIsValid = true;
        mStartFinishChromosome = "";
        mStartFinishArm = "";
        mConsistencyCount = 0;
        mHasReplicatedSVs = false;
    }

    public SvChain(final SvChain other)
    {
        mId = other.getId();
        mSvList = Lists.newArrayList();
        mLinkedPairs = Lists.newArrayList();
        mSvStartIsStartLink = Lists.newArrayList();
        mLength = 0;
        mIsClosedLoop = false;
        mIsValid = true;
        mHasReplicatedSVs = false;

        for(final SvLinkedPair pair : other.getLinkedPairs())
        {
            addLink(pair, false);
        }
    }

    public int getId() { return mId; }
    public void setId(int id) { mId = id; }

    public boolean isValid() { return mIsValid; }

    public int getSvCount() { return mSvList.size(); }
    public List<SvVarData> getSvList() { return mSvList; }

    public int getLinkCount() { return mLinkedPairs.size(); }
    public List<SvLinkedPair> getLinkedPairs() { return mLinkedPairs; }

    public void addLink(final SvLinkedPair pair, boolean addToStart)
    {
        if(mLinkedPairs.contains(pair))
            return;

        if(mLinkedPairs.isEmpty() || !addToStart)
            mLinkedPairs.add(pair);
        else
            mLinkedPairs.add(0, pair);

        final SvVarData first = pair.first();
        final SvVarData second = pair.second();

        boolean containsFirst = mSvList.contains(first);
        boolean containsSecond = mSvList.contains(second);

        if(containsFirst || containsSecond)
            mHasReplicatedSVs = true;

        if(mSvList.isEmpty())
        {
            mSvList.add(first);
            mSvStartIsStartLink.add(!pair.firstLinkOnStart());
            mSvList.add(second);
            mSvStartIsStartLink.add(pair.secondLinkOnStart());
            setStartFinishStatus();
            return;
        }

        int lastIndex = mSvList.size() - 1;

        if(containsFirst && containsSecond)
        {
            // check that these SVs are at the start and end, otherwise the new link is invalid
            if((mSvList.get(0) == first && mSvList.get(lastIndex) != second) || (mSvList.get(0) == second && mSvList.get(lastIndex) != first))
            {
                mIsValid = false;
                return;
            }

            // no need to add an SV twice (ie to both start and end)
            mIsClosedLoop = true;
        }
        else
        {
            if (addToStart)
            {
                if (mSvList.get(0) == first || mSvList.get(1) == first)
                {
                    // second SV is the new one here
                    mSvList.add(0, second);

                    // whichever end is not linked in this pair is the one exposed at the start of the chain
                    mSvStartIsStartLink.add(0, !pair.secondLinkOnStart());
                }
                else
                {
                    mSvList.add(0, first);
                    mSvStartIsStartLink.add(0, !pair.firstLinkOnStart());
                }
            }
            else
            {
                if (mSvList.get(lastIndex-1) == first || mSvList.get(lastIndex) == first)
                {
                    mSvList.add(second);
                    mSvStartIsStartLink.add(pair.secondLinkOnStart());
                }
                else
                {
                    mSvList.add(first);
                    mSvStartIsStartLink.add(pair.firstLinkOnStart());
                }
            }
        }

        setIsValid();
        setStartFinishStatus();

        mConsistencyCount = calcConsistency(mSvList);
    }

    public SvVarData getFirstSV() { return mSvList.isEmpty() ? null : mSvList.get(0); }
    public SvVarData getLastSV() { return mSvList.isEmpty() ? null : mSvList.get(mSvList.size()-1); }

    public SvLinkedPair getFirstLinkedPair() { return mLinkedPairs.isEmpty() ? null : mLinkedPairs.get(0); }
    public SvLinkedPair getLastLinkedPair() { return mLinkedPairs.isEmpty() ? null : mLinkedPairs.get(mLinkedPairs.size()-1); }

    public boolean firstLinkOpenOnStart() { return mSvStartIsStartLink.isEmpty() ? false : mSvStartIsStartLink.get(0); }

    public boolean lastLinkOpenOnStart()
    {
        if(mSvStartIsStartLink.isEmpty())
            return false;

        // the array of booleans refers to the the link to the preceding SV,
        // so if it's linked on the start backwards, the end must be open and vice versa
        return !mSvStartIsStartLink.get(mSvList.size()-1);
    }

    public boolean isClosedLoop() { return mIsClosedLoop; }
    public boolean hasReplicatedSVs() { return mHasReplicatedSVs; }

    private void setStartFinishStatus()
    {
        final String startArm = getFirstSV().arm(firstLinkOpenOnStart());
        final String startChr = getFirstSV().chromosome(firstLinkOpenOnStart());

        if(getLastSV().arm(lastLinkOpenOnStart()).equals(startArm) && getLastSV().chromosome(lastLinkOpenOnStart()).equals(startChr))
        {
            mStartFinishArm = startArm;
            mStartFinishChromosome = startChr;
        }
        else
        {
            mStartFinishArm = "";
            mStartFinishChromosome = "";
        }
    }

    public int getConsistencyCount() { return mConsistencyCount; }
    public boolean isConsistent() { return mConsistencyCount == 0; }

    public String startFinishChromosome() { return mStartFinishChromosome; }
    public String startFinishArm() { return mStartFinishArm; }
    public boolean openOnSameArm() { return !mStartFinishArm.isEmpty() && !mStartFinishChromosome.isEmpty(); }
    public String openChromosome(boolean useStart) { return useStart ? getFirstSV().chromosome(firstLinkOpenOnStart()) : getLastSV().chromosome(lastLinkOpenOnStart()); }
    public String openArm(boolean useStart) { return useStart ? getFirstSV().arm(firstLinkOpenOnStart()) : getLastSV().arm(lastLinkOpenOnStart()); }

    public boolean canAddLinkedPairToStart(final SvLinkedPair pair)
    {
        if(mLinkedPairs.contains(pair))
            return false;

        if(pair.first() == getFirstSV() && pair.firstUnlinkedOnStart() != firstLinkOpenOnStart())
            return true;
        else if(pair.second() == getFirstSV() && pair.secondUnlinkedOnStart() != firstLinkOpenOnStart())
            return true;
        else
            return false;
    }

    public boolean canAddLinkedPairToEnd(final SvLinkedPair pair)
    {
        if(mLinkedPairs.contains(pair))
            return false;

        if(pair.first() == getLastSV() && pair.firstUnlinkedOnStart() != lastLinkOpenOnStart())
            return true;
        else if(pair.second() == getLastSV() && pair.secondUnlinkedOnStart() != lastLinkOpenOnStart())
            return true;
        else
            return false;
    }

    public boolean canAddLinkedPair(final SvLinkedPair pair)
    {
        return canAddLinkedPairToStart(pair) || canAddLinkedPairToEnd(pair);
    }

    public boolean linkWouldCloseChain(final SvLinkedPair pair)
    {
        return canAddLinkedPairToStart(pair) && canAddLinkedPairToEnd(pair);
    }

    public boolean isIdentical(final SvChain other)
    {
        // true if both closed loops and containing the same linked pairs
        // or if not closed, having the same end points
        if(mLinkedPairs.size() != other.getLinkedPairs().size())
            return false;

        List<SvLinkedPair> otherLinkedPairs = Lists.newArrayList();
        otherLinkedPairs.addAll(other.getLinkedPairs());

        for(final SvLinkedPair pair : mLinkedPairs)
        {
            boolean matched = false;
            for(int i = 0; i < otherLinkedPairs.size(); ++i)
            {
                final SvLinkedPair otherPair = otherLinkedPairs.get(i);
                if(pair.matches(otherPair, true))
                {
                    otherLinkedPairs.remove(i); // reduce each time to make subsequent matches faster
                    matched = true;
                    break;
                }
            }

            if(!matched)
                return false;
        }

        if(isClosedLoop() && other.isClosedLoop())
            return true;

        if(!getFirstSV().equals(other.getFirstSV(), true) || firstLinkOpenOnStart() != other.firstLinkOpenOnStart())
            return false;

        if(!getLastSV().equals(other.getLastSV(), true) || lastLinkOpenOnStart() != other.lastLinkOpenOnStart())
            return false;

        return true;
    }

    public boolean hasLinks(final List<SvLinkedPair> linkedPairs)
    {
        for(final SvLinkedPair linkedPair : linkedPairs)
        {
            if(!mLinkedPairs.contains(linkedPair))
                return false;
        }

        return true;
    }

    public void setIsValid()
    {
        if(mSvList.isEmpty() || mLinkedPairs.isEmpty())
        {
            mIsValid = false;
            return;
        }

        // check for duplicate breakends
        for(int i = 0; i < mLinkedPairs.size(); ++i)
        {
            final SvLinkedPair lp1 = mLinkedPairs.get(i);

            for(int j = i + 1; j< mLinkedPairs.size(); ++j)
            {
                final SvLinkedPair lp2 = mLinkedPairs.get(j);

                if(lp1.hasLinkClash(lp2))
                {
                    mIsValid = false;
                    return;
                }
            }
        }

        mIsValid = true;
    }

    public void logLinks()
    {
        for(int i = 0; i < mLinkedPairs.size(); ++i)
        {
            final SvLinkedPair pair = mLinkedPairs.get(i);

            LOGGER.debug("chain({}) {}: pair({}) {} {} len={}",
                    mId, i, pair.toString(), pair.linkType(), pair.isInferred() ? "inferred" : "assembly", pair.length());
        }
    }

    public int getLength() { return mLength; }

    public void recalcLength()
    {
        // defined as the TI and DB lengths plus the variant lengths for those linked at both end
        mLength = 0;

        for(int i = 0; i < mLinkedPairs.size(); ++i)
        {
            final SvLinkedPair pair = mLinkedPairs.get(i);

            mLength += abs(pair.length());

            if(i > 0 && i < mLinkedPairs.size())
            {
                mLength += pair.first().length();
            }
        }
    }

    public int getUniqueSvCount()
    {
        int count = 0;

        for(final SvVarData var : mSvList)
        {
            if(!var.isReplicatedSv())
                ++count;
        }

        return count;
    }

    public int getSvIndex(final SvVarData var)
    {
        for(int index = 0; index < mSvList.size(); ++index)
        {
            if(mSvList.get(index) == var)
                return index;
        }

        return -1;
    }

    public final String getSvIndices(final SvVarData var)
    {
        String varIndices = "";
        for(int index = 0; index < mSvList.size(); ++index)
        {
            if(!mSvList.get(index).equals(var, true))
                continue;

            if(!varIndices.isEmpty())
                varIndices += ";";

            varIndices += index;
        }

        return varIndices;
    }

    public int getSvIndex(final SvVarData var, boolean matchStart)
    {
        for(int index = 0; index < mLinkedPairs.size(); ++index)
        {
            final SvLinkedPair pair = mLinkedPairs.get(index);

            if((pair.first().equals(var) && pair.firstLinkOnStart() != matchStart)
            || (pair.second().equals(var) && pair.secondLinkOnStart() != matchStart))
            {
                return index;
            }
        }

        return -1;
    }

    public boolean hasLinkedPair(final SvLinkedPair pair)
    {
        return mLinkedPairs.contains(pair);
    }

    public boolean hasLinkClash(final SvLinkedPair pair)
    {
        for(SvLinkedPair linkedPair : mLinkedPairs)
        {
            if (linkedPair.hasLinkClash(pair))
                return true;
        }

        return false;
    }

}
