
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.tokenizer.languages.swedish;

import java.io.IOException;
import java.util.Set;

import org.carrot2.core.linguistic.Language;
import org.carrot2.core.linguistic.LanguageTokenizer;
import org.carrot2.core.linguistic.Stemmer;
import org.carrot2.stemming.snowball.SnowballStemmersFactory;
import org.carrot2.util.WordLoadingUtils;

import org.carrot2.util.tokenizer.languages.StemmedLanguageBase;
import org.carrot2.util.tokenizer.parser.WordBasedParserFactory;

/**
 * An implementation of {@link Language} interface
 * for Swedish.
 */
public class Swedish extends StemmedLanguageBase {

    /**
     * A set of stopwords for this language.
     */
    private final static Set stopwords;
    
    /*
     * Load stopwords from an associated resource.
     */
    static {
        String resourceName = "stopwords.sv";
        try {
            stopwords = WordLoadingUtils.loadWordSet(
                    resourceName, Swedish.class.getResourceAsStream(resourceName));
		} catch (IOException e) {
            throw new RuntimeException("Could not load the required" +
                    "resource: " + resourceName);
		}
    }

    
    /**
     * Public constructor. 
     */
    public Swedish() {
        super.setStopwords(stopwords);
    }
    
	/**
     * Creates a new instance of a {@link LanguageTokenizer} for 
     * this language.
     * 
	 * @see org.carrot2.util.tokenizer.languages.StemmedLanguageBase#createTokenizerInstanceInternal()
	 */
	protected LanguageTokenizer createTokenizerInstanceInternal() {
        return WordBasedParserFactory.Default.borrowParser();
	}

	/**
     * @return Language code: <code>sv</code>
	 * @see org.carrot2.core.linguistic.Language#getIsoCode()
	 */
	public String getIsoCode() {
        return "sv";
	}

    /** 
     * Return an instance of a stemmer for this language
     * from the Snowball component.
     *  
     * @see org.carrot2.util.tokenizer.languages.LanguageBase#createStemmerInstance()
     */
    protected Stemmer createStemmerInstance() {
        return SnowballStemmersFactory.getInstance(getIsoCode());
    }


}