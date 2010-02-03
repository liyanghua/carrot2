
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.linguistic;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Tokenizer;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.util.MutableCharArray;

/**
 * A default language model stores a set of stop-words, a set of regular expressions for
 * hand-tuning unwanted cluster labels
 */
final class DefaultLanguageModel implements ILanguageModel
{
    private final LanguageCode languageCode;
    private final Tokenizer tokenizer;
    private final IStemmer stemmer;
    private final Set<MutableCharArray> stopwords;
    private final List<Pattern> stoplabels;

    /** Internal buffer for lookups in {@link #stopwords}. */
    private final MutableCharArray buffer = new MutableCharArray("");

    /**
     * Creates a new language model based on provided resources.
     */
    DefaultLanguageModel(LanguageCode languageCode, LexicalResources lexicalResources,
        IStemmer stemmer, Tokenizer tokenizer)
    {
        this.languageCode = languageCode;
        this.stopwords = lexicalResources.stopwords;
        this.stoplabels = lexicalResources.stoplabels;
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
    }

    public LanguageCode getLanguageCode()
    {
        return languageCode;
    }

    public Tokenizer getTokenizer()
    {
        return tokenizer;
    }

    public IStemmer getStemmer()
    {
        return stemmer;
    }

    public boolean isCommonWord(CharSequence sequence)
    {
        if (sequence instanceof MutableCharArray)
        {
            return stopwords.contains((MutableCharArray) sequence);
        }
        else
        {
            buffer.reset(sequence);
            return stopwords.contains(buffer);
        }
    }

    public boolean isStopLabel(CharSequence formattedLabel)
    {
        for (Pattern pattern : stoplabels)
        {
            if (pattern.matcher(formattedLabel).matches())
            {
                return true;
            }
        }

        return false;
    }
}
