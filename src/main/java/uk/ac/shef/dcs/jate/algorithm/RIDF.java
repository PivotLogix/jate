package uk.ac.shef.dcs.jate.algorithm;

import uk.ac.shef.dcs.jate.JATEException;
import uk.ac.shef.dcs.jate.feature.AbstractFeature;
import uk.ac.shef.dcs.jate.feature.FrequencyTermBased;
import uk.ac.shef.dcs.jate.model.JATETerm;

import java.util.*;
import org.apache.log4j.Logger;

/**
 * Residual IDF, see
 * Church, K. and Gale, W. 1995a. Inverse Document Frequency (IDF): A Measure of Deviation from Poisson. In Proceedings of the 3rd Workshop on Very Large Corpora. Cambridge, Massachusetts, USA, pp.121-30.
 */
public class RIDF extends Algorithm{
    private static final Logger LOG = Logger.getLogger(RIDF.class.getName());
    @Override
    public List<JATETerm> execute(Collection<String> candidates) throws JATEException {
        AbstractFeature feature = features.get(FrequencyTermBased.class.getName());
        validateFeature(feature, FrequencyTermBased.class);
        FrequencyTermBased fFeature = (FrequencyTermBased) feature;

        double totalDocs = (double) fFeature.getTotalDocs();
        List<JATETerm> result = new ArrayList<>();

        StringBuilder msg = new StringBuilder("Beginning computing RIDF values,");
        msg.append(", total terms=" + candidates.size());
        LOG.info(msg.toString());
        for(String tString: candidates){
            JATETerm term = new JATETerm(tString);
            /*int ttf = fFeature.getTTF(tString);
            double cf_over_N = (double) ttf / totalDocs;
            double exponential = Math.exp(0 - cf_over_N);
            double nominator = totalDocs * (1 - exponential);
            double denominator = (double) fFeature.getTermFrequencyInDocument(tString).size();

            if (denominator == 0) {
                denominator=1; //this shouldnt occur. a term that is firstly extracted from the corpus must have a source
            }
            double ridf = Math.log(nominator / denominator) / Math.log(2.0);
            */
            int ttf = fFeature.getTTF(tString);
            double attf = (double) ttf / totalDocs;
            double pi = Math.exp(0 - attf);
            double eidf = 0-(Math.log(1-pi)/Math.log(2));//expected idf
            double df = (double) fFeature.getTermFrequencyInDocument(tString).size();
            double idf= Math.log(totalDocs / df);

            double ridf = idf-eidf;
            term.setScore(ridf);
            result.add(term);
        }
        Collections.sort(result);
        LOG.info("Complete");
        return result;
    }
}
