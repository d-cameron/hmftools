package com.hartwig.hmftools.svanalysis.analysis;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.round;

import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.BND;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DEL;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.DUP;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.INS;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.INV;
import static com.hartwig.hmftools.common.variant.structural.StructuralVariantType.SGL;
import static com.hartwig.hmftools.svanalysis.analysis.SvClusteringMethods.isConsistentCluster;
import static com.hartwig.hmftools.svanalysis.analysis.SvUtilities.calcTypeCount;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.RESOLVED_TYPE_COMPLEX_CHAIN;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.RESOLVED_TYPE_RECIPROCAL_TRANS;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.RESOLVED_TYPE_SIMPLE_CHAIN;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.RESOLVED_TYPE_SIMPLE_INS;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.RESOLVED_TYPE_SIMPLE_SV;
import static com.hartwig.hmftools.svanalysis.types.SvCluster.findCluster;
import static com.hartwig.hmftools.svanalysis.analysis.SvUtilities.copyNumbersEqual;
import static com.hartwig.hmftools.svanalysis.types.SvLinkedPair.LINK_TYPE_SGL;
import static com.hartwig.hmftools.svanalysis.types.SvVarData.SVI_END;
import static com.hartwig.hmftools.svanalysis.types.SvVarData.SVI_START;
import static com.hartwig.hmftools.svanalysis.types.SvVarData.findVariantById;
import static com.hartwig.hmftools.svanalysis.types.SvLinkedPair.ASSEMBLY_MATCH_INFER_ONLY;
import static com.hartwig.hmftools.svanalysis.types.SvVarData.isStart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.hartwig.hmftools.svanalysis.types.SvArmGroup;
import com.hartwig.hmftools.svanalysis.types.SvBreakend;
import com.hartwig.hmftools.svanalysis.types.SvCNData;
import com.hartwig.hmftools.svanalysis.types.SvChain;
import com.hartwig.hmftools.svanalysis.types.SvCluster;
import com.hartwig.hmftools.svanalysis.types.SvVarData;
import com.hartwig.hmftools.svanalysis.types.SvLinkedPair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClusterAnalyser {

    final SvClusteringConfig mConfig;
    final SvUtilities mUtils;
    SvClusteringMethods mClusteringMethods;

    String mSampleId;
    List<SvCluster> mClusters;
    private ChainFinder mChainFinder;
    private LinkFinder mLinkFinder;

    public static int SMALL_CLUSTER_SIZE = 3;

    private static final Logger LOGGER = LogManager.getLogger(ClusterAnalyser.class);

    public ClusterAnalyser(final SvClusteringConfig config, final SvUtilities utils, SvClusteringMethods clusteringMethods)
    {
        mConfig = config;
        mUtils = utils;
        mClusteringMethods = clusteringMethods;
        mClusters = null;
        mSampleId = "";
        mLinkFinder = new LinkFinder(mConfig, mUtils, mClusteringMethods);
        mChainFinder = new ChainFinder(mUtils);
        mChainFinder.setLogVerbose(mConfig.LogVerbose);
    }

    public void setClusterData(final String sampleId, List<SvCluster> clusters)
    {
        mSampleId = sampleId;
        mClusters = clusters;
    }

    public void findSimpleCompleteChains()
    {
        // for small clusters, try to find a full chain through all SVs
        for(SvCluster cluster : mClusters)
        {
            if(cluster.getCount() == 1 && cluster.isSimpleSVs())
            {
                setClusterResolvedState(cluster);
                continue;
            }

            // skip more complicated clusters for now
            if(cluster.getCount() > SMALL_CLUSTER_SIZE || !cluster.isConsistent() || cluster.hasVariedCopyNumber())
                continue;

            // first establish links between SVs (eg TIs and DBs)
            mLinkFinder.findLinkedPairs(mSampleId, cluster);

            // then look for fully-linked clusters, ie chains involving all SVs
            findChains(cluster);

            cacheFinalLinkedPairs(cluster);

            setClusterResolvedState(cluster);

            if(isConsistentCluster(cluster))
            {
                LOGGER.debug("sample({}) cluster({}) simple and consistent with {} SVs", mSampleId, cluster.getId(), cluster.getCount());
            }
        }
    }

    public void findLinksAndChains()
    {
        for(SvCluster cluster : mClusters)
        {
            cluster.setUniqueBreakends();

            if(cluster.isSimpleSingleSV())
                continue;

            if(cluster.isFullyChained())
                continue;

            if(cluster.getUniqueSvCount() < 2)
            {
                setClusterArmBoundaries(cluster);
                continue;
            }

            // first establish links between SVs (eg TIs and DBs)
            mLinkFinder.findLinkedPairs(mSampleId, cluster);

            // then look for fully-linked clusters, ie chains involving all SVs
            findChains(cluster);

            setClusterArmBoundaries(cluster);

            cacheFinalLinkedPairs(cluster);

            setClusterResolvedState(cluster);

            cluster.logDetails();
        }

        // now look at merging unresolved & inconsistent clusters where they share the same chromosomal arms
        List<SvCluster> mergedClusters = mergeInconsistentClusters();

        if(!mergedClusters.isEmpty())
        {
            for(SvCluster cluster : mergedClusters)
            {
                cluster.setDesc(cluster.getClusterTypesAsString());

                // need to be careful replicating already replicated SVs..
                // especially those already in linked chains
                // may be only replicate stand-alone SVs at this point which have now been clustered
                replicateMergedClusterSVs(cluster);

                // repeat the search for inferred links now that additional SVs have been merged in but only on unlinked SVs
                List<SvLinkedPair> newLinkedPairs = mLinkFinder.createInferredLinkedPairs(cluster, cluster.getUnlinkedSVs(), true);

                cluster.getInferredLinkedPairs().addAll(newLinkedPairs);

                // createCopyNumberSegments(sampleId, cluster);

                findChains(cluster);
            }

            // any clusters which were merged to resolve a collection of them, but
            // which did not lead to any longer chains, are now de-merged
            demergeClusters(mergedClusters);

            for(SvCluster cluster : mergedClusters)
            {
                cacheFinalLinkedPairs(cluster);

                setClusterResolvedState(cluster);
            }
        }

        for(SvCluster cluster : mClusters)
        {
            if(cluster.hasSubClusters()) // these haven't been logged
                cluster.logDetails();

            mLinkFinder.resolveTransitiveSVs(mSampleId, cluster);
            // cacheFinalLinkedPairs(cluster);

            // reportChainFeatures(cluster);
        }
    }

    private void findChains(SvCluster cluster)
    {
        mChainFinder.initialise(mSampleId, cluster);
        // mChainFinder.setRequireFullChains(true);

        boolean hasFullChain = mChainFinder.formClusterChains();

        if(!hasFullChain)
            return;

        cluster.setIsFullyChained(true);

        // remove any inferred link which isn't in the full chain
        final SvChain fullChain = cluster.getChains().get(0);

        List<SvLinkedPair> inferredLinkedPairs = cluster.getInferredLinkedPairs();
        inferredLinkedPairs.clear();

        for(SvLinkedPair pair : fullChain.getLinkedPairs())
        {
            if(pair.isInferred())
                inferredLinkedPairs.add(pair);
        }
    }

    private void replicateMergedClusterSVs(SvCluster cluster)
    {
        // first remove any replication previously performed before clusters were merged
        if(!cluster.hasSubClusters())
            return;

        if(!cluster.hasVariedCopyNumber())
            return;

        int minCopyNumber = cluster.getMinCopyNumber();

        for(SvCluster subCluster : cluster.getSubClusters())
        {
            if(subCluster.hasReplicatedSVs())
                continue;

            // for now to difficult to consider the impact on these
            if(!subCluster.getChains().isEmpty() || !subCluster.getLinkedPairs().isEmpty())
                continue;

            int clusterCount = subCluster.getCount();

            for(int i = 0; i < clusterCount; ++i)
            {
                SvVarData var = subCluster.getSVs().get(i);
                int calcCopyNumber = var.impliedCopyNumber(true);

                if(calcCopyNumber <= minCopyNumber)
                    continue;

                int svMultiple = calcCopyNumber / minCopyNumber;

                LOGGER.debug("cluster({}) replicating SV({}) {} times, copyNumChg({} vs min={})",
                        cluster.getId(), var.posId(), svMultiple, calcCopyNumber, minCopyNumber);

                var.setReplicatedCount(svMultiple);

                // add to the parent cluster only for now
                for(int j = 1; j < svMultiple; ++j)
                {
                    SvVarData newVar = new SvVarData(var);
                    cluster.addVariant(newVar);
                }
            }

        }

        // cluster.removeReplicatedSvs();
    }

    private void setClusterResolvedState(SvCluster cluster)
    {
        if(cluster.isResolved())
            return;

        boolean logData = false;

        if (cluster.isSimpleSVs())
        {
            if(cluster.getCount() == 1)
            {
                cluster.setResolved(true, RESOLVED_TYPE_SIMPLE_SV);
                return;
            }

            if(calcTypeCount(cluster.getSVs(), DEL) + calcTypeCount(cluster.getSVs(), DUP) == 2)
            {
                if(mClusteringMethods.markDelDupPairTypes(cluster, logData, mSampleId))
                    return;
            }

            cluster.setResolved(true, RESOLVED_TYPE_SIMPLE_SV);
            return;
        }

        // next simple reciprocal inversions and translocations
        if (cluster.getCount() == 2 && cluster.isConsistent())
        {
            if(calcTypeCount(cluster.getSVs(), BND) == 2)
            {
                mClusteringMethods.markBndPairTypes(cluster, logData, mSampleId);
            }
            else if(calcTypeCount(cluster.getSVs(), INV) == 2)
            {
                mClusteringMethods.markInversionPairTypes(cluster, logData, mSampleId);
            }
            else if(calcTypeCount(cluster.getSVs(), SGL) == 2)
            {
                if(cluster.getLinkedPairs().size() == 1 && cluster.getLinkedPairs().get(0).linkType() == LINK_TYPE_SGL)
                {
                    cluster.setResolved(true, RESOLVED_TYPE_SIMPLE_INS);
                }
            }

            return;
        }

        // next clusters with which start and end on the same arm, have the same start and end orientation
        // and the same start and end copy number
        if(isConsistentCluster(cluster) || cluster.isFullyChained())
        {
            boolean isResolved = cluster.isConsistent();

            if(!cluster.hasReplicatedSVs())
                cluster.setResolved(isResolved, RESOLVED_TYPE_SIMPLE_CHAIN);
            else
                cluster.setResolved(isResolved, RESOLVED_TYPE_COMPLEX_CHAIN);

            return;
        }

        if(cluster.hasLinkingLineElements())
        {
            // skip further classification for now
            cluster.setResolved(false, "Line");
            return;
        }

        // cluster remains largely unresolved..
        if(!cluster.getChains().isEmpty())
        {
            if(!cluster.hasReplicatedSVs())
                cluster.setResolved(false, "SimplePartialChain");
            else
                cluster.setResolved(false, "ComplexPartialChain");
        }
    }

    private void setClusterArmBoundaries(SvCluster cluster)
    {
        // for each arm group within the cluster, find the bounding SV breakends
        // excluding any which are part of a continuous chain (ends excluded)
        final List<SvVarData> unlinkedSVs = cluster.getUnlinkedSVs();
        final List<SvChain> chains = cluster.getChains();

        for(final SvArmGroup armGroup : cluster.getArmGroups())
        {
            armGroup.setBreakend(null, true);
            armGroup.setBreakend(null, false);

            SvVarData startVar = null;
            SvVarData endVar = null;
            long startPosition = -1;
            long endPosition = 0;

            for(final SvVarData var : armGroup.getSVs())
            {
                if(var.isReplicatedSv())
                    continue;

                boolean checkStart = false;
                boolean checkEnd = false;

                if(unlinkedSVs.contains(var))
                {
                    if(var.type() == BND)
                    {
                        if(var.chromosome(true).equals(armGroup.chromosome()))
                            checkStart = true;
                        else if(var.chromosome(false).equals(armGroup.chromosome()))
                            checkEnd = true;
                    }
                    else
                    {
                        checkStart = true;
                        checkEnd = true;
                    }
                }
                else
                {
                    // check chain ends for a match with this SV
                    // translocations are skipped if their open end is on another arm
                    for(final SvChain chain : chains)
                    {
                        if(chain.getFirstSV().equals(var)
                        && (!var.isTranslocation() || var.chromosome(chain.firstLinkOpenOnStart()).equals(armGroup.chromosome())))
                        {
                            if(chain.firstLinkOpenOnStart())
                                checkStart = true;
                            else
                                checkEnd = true;
                        }

                        if(chain.getLastSV().equals(var)
                        && (!var.isTranslocation() || var.chromosome(chain.lastLinkOpenOnStart()).equals(armGroup.chromosome())))
                        {
                            if(chain.lastLinkOpenOnStart())
                                checkStart = true;
                            else
                                checkEnd = true;
                        }
                    }
                }

                for(int be = SVI_START; be <= SVI_END; ++be)
                {
                    if((!checkStart && be == SVI_START) || (!checkEnd && be == SVI_END))
                        continue;

                    boolean useStart = isStart(be);

                    long position = var.position(useStart);

                    if(startPosition < 0 || position < startPosition)
                    {
                        startVar = var;
                        startPosition = position;
                    }

                    if(position > endPosition)
                    {
                        endVar = var;
                        endPosition = position;
                    }
                }
            }

            if(startVar != null)
            {
                boolean useStart = startVar.type() == BND ? startVar.chromosome(true).equals(armGroup.chromosome()) : true;
                armGroup.setBreakend(new SvBreakend(startVar, useStart), true);
            }

            if(endVar != null)
            {
                boolean useStart = endVar.type() == BND ? endVar.chromosome(true).equals(armGroup.chromosome()) : false;
                armGroup.setBreakend(new SvBreakend(endVar, useStart), false);
            }

            if(cluster.getCount() > 1)
            {
                LOGGER.debug("cluster({}) arm({}) consistent({}) start({}) end({}) posBoundaries({} -> {})",
                        cluster.getId(), armGroup.id(), armGroup.isConsistent(),
                        armGroup.getBreakend(true) != null ? armGroup.getBreakend(true).toString() : "null",
                        armGroup.getBreakend(false) != null ? armGroup.getBreakend(false).toString() : "null",
                        armGroup.posStart(), armGroup.posEnd());
            }
        }
    }

    private List<SvCluster> mergeInconsistentClusters()
    {
        // it's possible that to resolve arms and more complex arrangements, clusters not merged
        // by proximity of overlaps must be put together to solve inconsistencies (ie loose ends)
        List<SvCluster> mergedClusters = Lists.newArrayList();

        // merge any cluster which is itself not consistent and has breakends on the same arm as another
        int index1 = 0;
        while(index1 < mClusters.size())
        {
            SvCluster cluster1 = mClusters.get(index1);

            boolean isConsistent1 = isConsistentCluster(cluster1);
            boolean hasLongDelDup1 = mClusteringMethods.clusterHasLongDelDup(cluster1);

            if(isConsistent1 && !hasLongDelDup1)
            {
                ++index1;
                continue;
            }

            boolean cluster1Merged = false;
            SvCluster newCluster = null;

            int index2 = index1 + 1;
            while(index2 < mClusters.size())
            {
                SvCluster cluster2 = mClusters.get(index2);

                boolean isConsistent2 = isConsistentCluster(cluster2);
                boolean hasLongDelDup2 = mClusteringMethods.clusterHasLongDelDup(cluster2);

                if(isConsistent2 && !hasLongDelDup2)
                {
                    ++index2;
                    continue;
                }

                boolean foundConnection = !isConsistent1 && !isConsistent2 && canMergeClustersOnOverlaps(cluster1, cluster2);

                if(!foundConnection && hasLongDelDup1)
                    foundConnection = mClusteringMethods.canMergeClustersOnLongDelDups(cluster1, cluster2);

                if(!foundConnection && hasLongDelDup2)
                    foundConnection = mClusteringMethods.canMergeClustersOnLongDelDups(cluster2, cluster1);

                if(!foundConnection)
                {
                    ++index2;
                    continue;
                }

                boolean cluster2Merged = false;

                if(cluster1.hasSubClusters())
                {
                    LOGGER.debug("cluster({} svs={}) merges in cluster({} svs={})",
                            cluster1.getId(), cluster1.getCount(), cluster2.getId(), cluster2.getCount());

                    cluster2Merged = true;
                    cluster1.addSubCluster(cluster2);
                    setClusterArmBoundaries(cluster1);
                }
                else
                {
                    cluster1Merged = true;
                    cluster2Merged = true;

                    newCluster = new SvCluster(mClusteringMethods.getNextClusterId());

                    LOGGER.debug("new cluster({}) from merge of cluster({} svs={}) and cluster({} svs={})",
                            newCluster.getId(), cluster1.getId(), cluster1.getCount(), cluster2.getId(), cluster2.getCount());

                    newCluster.addSubCluster(cluster1);
                    newCluster.addSubCluster(cluster2);
                    mergedClusters.add(newCluster);
                    setClusterArmBoundaries(newCluster);
                }

                if(cluster2Merged)
                    mClusters.remove(index2);
                else
                    ++index2;

                if(cluster1Merged)
                    break;
            }

            if(cluster1Merged && newCluster != null)
            {
                // cluster has been replaced with a commbined one
                mClusters.remove(index1);
                mClusters.add(index1, newCluster);
            }
            else
            {
                ++index1;
            }
        }

        return mergedClusters;
    }

    private boolean canMergeClustersOnOverlaps(SvCluster cluster1, SvCluster cluster2)
    {
        // checks for overlapping breakends in inconsistent matching arms
        final List<SvArmGroup> armGroups1 = cluster1.getArmGroups();
        final List<SvArmGroup> armGroups2 = cluster2.getArmGroups();

        for (SvArmGroup armGroup1 : armGroups1)
        {
            if(armGroup1.isConsistent())
                continue;

            for (SvArmGroup armGroup2 : armGroups2)
            {
                if(armGroup2.isConsistent())
                    continue;

                if(!armGroup1.matches(armGroup2))
                    continue;

                // for now merge any inconsistent arm
                LOGGER.debug("inconsistent cluster({}) and cluster({}) linked on chrArm({})",
                        cluster1.getId(), cluster2.getId(), armGroup1.id());

                return true;
            }
        }

        return false;
    }

    private void demergeClusters(List<SvCluster> mergedClusters)
    {
        // de-merge any clusters which didn't form longer chains
        int clusterCount = mergedClusters.size();

        for(int i = 0; i < clusterCount; ++i)
        {
            SvCluster cluster = mergedClusters.get(i);

            if ( cluster.isFullyChained())
                continue;

            int mainChainCount = cluster.getMaxChainCount();
            int maxSubClusterChainCount = 0;

            for(final SvCluster subCluster : cluster.getSubClusters())
            {
                maxSubClusterChainCount = max(maxSubClusterChainCount, subCluster.getMaxChainCount());
            }

            if(mainChainCount > maxSubClusterChainCount)
                continue;

            // add the original clusters back in
            for(final SvCluster subCluster : cluster.getSubClusters())
            {
                mClusters.add(subCluster);
            }

            if(mainChainCount > 0)
            {
                LOGGER.debug("removed cluster({}) since maxChainCount({}) less than subclusters({}) maxSubClusterChainCount({})",
                        cluster.getId(), mainChainCount, cluster.getSubClusters().size(), maxSubClusterChainCount);
            }

            mClusters.remove(cluster);
        }
    }

    public void reportChainFeatures(final SvCluster cluster)
    {
        for (final SvChain chain : cluster.getChains())
        {
            if(chain.getLinkedPairs().size() <= 1)
                continue;

            findChainRepeatedSegments(cluster, chain);

            findChainTranslocationTIs(cluster, chain);
        }
    }

    private void findChainRepeatedSegments(final SvCluster cluster, final SvChain chain)
    {
        if(!chain.hasReplicatedSVs())
            return;

        List<SvVarData> replicatedSVs = Lists.newArrayList();

        final List<SvVarData> svList = chain.getSvList();

        for (int i = 0; i < svList.size(); ++i)
        {
            final SvVarData var1 = svList.get(i);

            if(replicatedSVs.contains(var1))
                continue;

            for (int j = i + 1; j < svList.size(); ++j)
            {
                final SvVarData var2 = svList.get(j);

                if (!var1.equals(var2, true))
                    continue;

                replicatedSVs.add(var1);

                // look for repeated sections forwards or backwards from this point
                List<SvVarData> forwardRepeats = getRepeatedSvSequence(svList, i, j, true);

                boolean forwardSequence = false;

                if(!forwardRepeats.isEmpty())
                {
                    forwardSequence = true;
                    replicatedSVs.addAll(forwardRepeats);
                }
                else
                {
                    forwardSequence = false;
                    forwardRepeats = getRepeatedSvSequence(svList, i, j, false);
                }

                if(!forwardRepeats.isEmpty())
                {
                    replicatedSVs.addAll(forwardRepeats);

                    forwardRepeats.set(0, var1);

                    String svIds = var1.id();
                    for(int k = 1; k < forwardRepeats.size(); ++k)
                        svIds += ";" + forwardRepeats.get(k).id();

                    LOGGER.info("sample({}) cluster({}) chain({}) {} sequence of {} SVs starting at index({}:{}) SV({})",
                            mSampleId, cluster.getId(), chain.getId(), forwardSequence ? "forward" : "reverse",
                            forwardRepeats.size(), i, j, var1.id());

                    // ClusterId,ChainId,SequenceCount,VarIds,MatchDirection
                    LOGGER.info("CF_REPEAT_SEQ: {},{},{},{},{},{}",
                            mSampleId, cluster.getId(), chain.getId(), forwardRepeats.size(), svIds, forwardSequence);

                    break;
                }

                // no sequence found
            }
        }
    }

    private List<SvVarData> getRepeatedSvSequence(final List<SvVarData> svList, int firstIndex, int secondIndex, boolean walkForwards)
    {
        // walk forward from these 2 start points comparing SVs
        List<SvVarData> sequence = Lists.newArrayList();

        int i = firstIndex;
        int j = secondIndex + 1;

        if(walkForwards)
            ++i;
        else
            --i;

        while(i < secondIndex && i >= 0 && j < svList.size())
        {
            final SvVarData var1 = svList.get(i);
            final SvVarData var2 = svList.get(j);

            if(!var1.equals(var2, true))
                break;

            sequence.add(var1);

            ++j;

            if(walkForwards)
                ++i;
            else
                --i;
        }

        return sequence;
    }

    private void findChainTranslocationTIs(final SvCluster cluster, final SvChain chain)
    {
        final List<SvLinkedPair> linkedPairs = chain.getLinkedPairs();

        int svCount = chain.getSvCount();
        int uniqueSvCount = chain.getUniqueSvCount();

        boolean hasReplication = uniqueSvCount < svCount;

        final List<SvVarData> svList = chain.getSvList();

        for(int i = 0; i < linkedPairs.size(); ++i)
        {
            final SvLinkedPair pair = linkedPairs.get(i);
            final SvVarData svBack = svList.get(i);

            if(svList.size() == i+1)
                break; // chain loops back to same SV (whether closed or not

            final SvVarData svForward = svList.get(i+1);

            // find out-and-back or out-and-on translocations showing evidence of foreign fragment insertions
            if(svForward.type() == BND && i < linkedPairs.size() - 1 && i < svList.size() - 2)
            {
                final SvLinkedPair nextPair = linkedPairs.get(i+1);
                final SvVarData nextSvForward = svList.get(i+2);

                if(nextSvForward.type() == BND)
                {
                    boolean svForwardLinkedOnStart = pair.getLinkedOnStart(svForward);
                    final String startChromosome = svForward.chromosome(svForwardLinkedOnStart);
                    // final String linkChromosome = svForward.chromosome(!svForwardLinkedOnStart);

                    boolean nextSvForwardLinkedOnStart = nextPair.getLinkedOnStart(nextSvForward);
                    final String endChromosome = nextSvForward.chromosome(!nextSvForwardLinkedOnStart);

                    boolean outAndBack = startChromosome.equals(endChromosome);

                    nextPair.setInfo("TransTI");

                    // SampleId, ClusterId,ChainId,SvId1,SvId2,IsOutAndBack,TILength,IsAssembly,ChainLinks,HasReplication
                    LOGGER.info("CF_TRANS_TI: {},{},{},{},{},{},{},{},{},{}",
                            mSampleId, cluster.getId(), chain.getId(), svForward.id(), nextSvForward.id(),
                            outAndBack, nextPair.length(), !nextPair.isInferred(), chain.getLinkCount(), hasReplication);
                }
            }
        }
    }

    public void markFoldbacks()
    {
        for(final Map.Entry<String, List<SvBreakend>> entry : mClusteringMethods.getChrBreakendMap().entrySet())
        {
            List<SvBreakend> breakendList = entry.getValue();

            for(int i = 1; i < breakendList.size(); ++i)
            {
                final SvBreakend breakend = breakendList.get(i);
                final SvBreakend prevBreakend = breakendList.get(i - 1);

                checkFoldbackBreakends(breakend, prevBreakend);
            }
        }
    }

    private void checkFoldbackBreakends(SvBreakend be1, SvBreakend be2)
    {
        // consecutive breakends, same orientation, same var or part of a chain
        if(be1.orientation() != be2.orientation())
            return;

        final SvVarData var1 = be1.getSV();
        final SvVarData var2 = be2.getSV();

        if(var1.type() == INS || var2.type() == INS)
            return;

        // skip unclustered DELs & DUPs, reciprocal INV or reciprocal BNDs
        final SvCluster cluster1 = findCluster(var1, mClusters);

        if(cluster1.isSimpleSVs() || (cluster1.getCount() == 2 && cluster1.isConsistent()))
            return;

        final SvCluster cluster2 = findCluster(var2, mClusters);

        if(cluster2.isSimpleSVs() || (cluster2.getCount() == 2 && cluster2.isConsistent()))
            return;

        if(!var1.equals(var2))
        {
            // must be same cluster and part of the same chain
            if(cluster1 != cluster2)
                return;

            if(var1.getReplicatedCount() != var2.getReplicatedCount())
                return;

            final SvChain chain1 = cluster1.findChain(var1);
            final SvChain chain2 = cluster2.findChain(var1);

            if(chain1 == null || chain2 == null || chain1 != chain2)
                return;
        }

        boolean v1Start = be1.usesStart();
        boolean v2Start = be2.usesStart();

        // check copy numbers match
        double cn1 = 0;
        double cn2 = 0;
        if((be1.orientation() == 1 && be1.position() < be2.position()) || (be1.orientation() == -1 && be1.position() > be2.position()))
        {
            // be1 is facing away from be2, so need to take its copy number on the break side
            cn1 = var1.copyNumber(v1Start) - var1.copyNumberChange(v1Start);
            cn2 = var2.copyNumber(v2Start);
        }
        else
        {
            cn2 = var2.copyNumber(v2Start) - var2.copyNumberChange(v2Start);
            cn1 = var1.copyNumber(v1Start);
        }

        if(!copyNumbersEqual(cn1, cn2))
            return;

        int length = (int)abs(be1.position() - be2.position());

        // if either variant already has foldback info set, favour
        // a) simple inversions then
        // b) shortest length

        boolean skipFoldback = false;
        if(!var1.getFoldbackLink(v1Start).isEmpty() && !var1.equals(var2) && var1.getFoldbackLen(v1Start) < length)
        {
            skipFoldback = true;
        }
        else if(!var2.getFoldbackLink(v2Start).isEmpty() && !var1.equals(var2) && var2.getFoldbackLen(v2Start) < length)
        {
            skipFoldback = true;
        }

        if(skipFoldback)
            return;

        if(!var1.getFoldbackLink(v1Start).isEmpty())
            clearFoldbackInfo(var1.getFoldbackLink(v1Start), var1.id(), cluster1, v1Start);

        if(!var2.getFoldbackLink(v2Start).isEmpty())
            clearFoldbackInfo(var2.getFoldbackLink(v2Start), var2.id(), cluster2, v2Start);

        var1.setFoldbackLink(v1Start, var2.id(), length);
        var2.setFoldbackLink(v2Start, var1.id(), length);

        if(var1.equals(var2))
        {
            LOGGER.debug(String.format("cluster(%s) foldback inversion SV(%s) length(%d) copyNumber(%.3f)",
                    cluster1.getId(), var1.posId(), length, cn1));
        }
        else
        {
            LOGGER.debug(String.format("cluster(%s) foldback be1(%s) be2(%s) length(%d) copyNumber(%.3f)",
                    cluster1.getId(), be1.toString(), be2.toString(), length, cn1));
        }
    }

    private void clearFoldbackInfo(final String varId, final String matchVarId, SvCluster cluster, boolean useStart)
    {
        SvVarData var = findVariantById(varId, cluster.getSVs());

        if(var == null)
            return;

        if(var.getFoldbackLink(true).equals(matchVarId))
            var.setFoldbackLink(true, "", -1);
        else
            var.setFoldbackLink(false, "", -1);
    }

    private void createCopyNumberSegments(final String sampleId, final SvCluster cluster)
    {
        int cnId = 0;
        final List<SvVarData> clusterSVs = cluster.getSVs();

        Map<String, List<SvCNData>> chrCNDataMap = new HashMap();

        for(Map.Entry<String, List<SvBreakend>> entry : mClusteringMethods.getChrBreakendMap().entrySet())
        {
            final String chromosome = entry.getKey();

            List<SvBreakend> breakendList = entry.getValue();

            List<SvCNData> copyNumberData = Lists.newArrayList();

            for (int i = 0; i < breakendList.size(); ++i)
            {
                final SvBreakend breakend = breakendList.get(i);
                final SvVarData var = breakend.getSV();

                double copyNumber = var.copyNumber(breakend.usesStart());
                double copyNumberChange = var.copyNumberChange(breakend.usesStart());
                double prevCopyNumber = copyNumber - copyNumberChange;

                int adjCopyNumber = (int)round(copyNumber/2 - 1);

                //LOGGER.debug(String.format("sample(%s) chr(%s) seg %d: copyNumber(%d %.2f prev=%.2f chg=%.2f)",
                //        sampleId, chromosome, i, adjCopyNumber, copyNumber, prevCopyNumber, copyNumberChange));

                if(clusterSVs.contains(var))
                {
                    SvCNData cnData = new SvCNData(cnId++, chromosome, breakend.position(), 0, "", "", 0, 0, 0, adjCopyNumber, "");
                    copyNumberData.add(cnData);
                }
            }

            chrCNDataMap.put(chromosome, copyNumberData);
        }

        cluster.setChrCNData(chrCNDataMap);
    }

    private void cacheFinalLinkedPairs(SvCluster cluster)
    {
        List<SvLinkedPair> linkedPairs;

        if(cluster.isFullyChained())
        {
            linkedPairs = cluster.getChains().get(0).getLinkedPairs();
        }
        else
        {
            linkedPairs = Lists.newArrayList();

            // add all chained links
            for(final SvChain chain : cluster.getChains())
            {
                linkedPairs.addAll(chain.getLinkedPairs());
            }

            // any any unchained assembly links
            for(final SvLinkedPair pair : cluster.getAssemblyLinkedPairs())
            {
                if(!linkedPairs.contains(pair))
                    linkedPairs.add(pair);
            }

            // finally add any other potential inferred links which don't clash with existing links
            for(final SvLinkedPair pair : cluster.getInferredLinkedPairs())
            {
                if(linkedPairs.contains(pair))
                    continue;

                boolean hasClash = false;
                for(final SvLinkedPair existingLink : linkedPairs)
                {
                    if (existingLink.hasLinkClash(pair))
                    {
                        hasClash = true;
                        break;
                    }
                }

                if(!hasClash)
                    linkedPairs.add(pair);
            }

            // mark the resultant set of inferred links
            for (SvLinkedPair pair : linkedPairs)
            {
                if(pair.isInferred())
                {
                    pair.first().setAssemblyMatchType(ASSEMBLY_MATCH_INFER_ONLY, pair.firstLinkOnStart());
                    pair.second().setAssemblyMatchType(ASSEMBLY_MATCH_INFER_ONLY, pair.secondLinkOnStart());
                }
            }
        }

        if(!linkedPairs.isEmpty())
        {
            LOGGER.debug("cluster({}: {} count={}) has {} linked pairs",
                    cluster.getId(), cluster.getDesc(), cluster.getUniqueSvCount(), linkedPairs.size());
        }

        cluster.setLinkedPairs(linkedPairs);
    }

    public static void reduceInferredToShortestLinks(List<SvLinkedPair> linkedPairs, List<SvChain> chains)
    {
        // any linked pair used in a chain must be kept
        List<SvLinkedPair> reqLinkedPairs = Lists.newArrayList();
        for(SvChain chain : chains)
        {
            reqLinkedPairs.addAll(chain.getLinkedPairs());
        }

        // now remove mutually exclusive linked sections by using the shortest first (they are already ordered)
        int i = 0;
        while(i < linkedPairs.size())
        {
            final SvLinkedPair pair = linkedPairs.get(i);

            if(reqLinkedPairs.contains(pair))
            {
                ++i;
                continue;
            }

            boolean removeFirst = false;

            // search for another pair with a matching breakend
            for(int j = i+1; j < linkedPairs.size();)
            {
                final SvLinkedPair pair2 = linkedPairs.get(j);

                if((pair.first().equals(pair2.first()) && pair.firstLinkOnStart() == pair2.firstLinkOnStart())
                || (pair.first().equals(pair2.second()) && pair.firstLinkOnStart() == pair2.secondLinkOnStart())
                || (pair.second().equals(pair2.first()) && pair.secondLinkOnStart() == pair2.firstLinkOnStart())
                || (pair.second().equals(pair2.second()) && pair.secondLinkOnStart() == pair2.secondLinkOnStart()))
                {
                    // to avoid logging unlikely long TIs
                    //LOGGER.debug("removing duplicate linked pair({} len={}) vs shorter({} len={})",
                    //        pair2.toString(), pair2.length(), pair.toString(), pair.length());

                    if(reqLinkedPairs.contains(pair2))
                    {
                        removeFirst = true;
                        break;
                    }

                    // remove the longer pair with a duplicate breakend
                    linkedPairs.remove(j);
                }
                else
                {
                    ++j;
                }
            }

            if(removeFirst)
            {
                // LOGGER.debug("duplicate linked pair({} len={})", pair.toString(), pair.length(), pair.first().getTransSvLinks());
                linkedPairs.remove(i);
            }
            else
            {
                ++i;
            }
        }
    }

    public void logStats()
    {
        mChainFinder.getContinuousFinderPc().logStats(false);
        mChainFinder.getRecursiveFinderPc().logStats(false);
    }

}
