
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

package com.mwroblewski.carrot.filter.termsfilter;


import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Element;

import com.dawidweiss.carrot.util.Dom4jUtils;
import com.mwroblewski.carrot.lexical.LexicalElement;
import com.mwroblewski.carrot.utils.FrequencyHashMap;


/**
 * Class containing methods used by {@link com.mwroblewski.carrot.filter.termsfilter.TermsFilter}
 * for reading and writing data from/to XML document. Each object of this class is associated with
 * one filter's XML root document and performs it's methods on this document.
 *
 * @author Michał Wróblewski
 */
public class TermsFilterData
{
    /** root of filter's XML document (the <code>&lt;searchresult&gt;</code> element) */
    protected Element root;
    protected String [] snippets;
    protected String [] titles;
    protected String [] ids;

    /** mapping from terms (without stop-words) to their stems */
    protected HashMap stemsMap;
    protected FrequencyHashMap inverseStemsMap;
    protected HashMap stopWordsMap;
    protected String stemmedQuery;
    public Vector stemmedQueryVector;

    public void addStem(String term, String stem)
    {
        if (!stemsMap.containsKey(term))
        {
            stemsMap.put(term, stem);
        }
    }


    public void addForm(String stem, String form)
    {
        inverseStemsMap.put(stem, form);
    }

    /**
     * Constructor creating an object of this class associated with given XML document.
     *
     * @param root root of filter's XML document (the <code>&lt;searchresult&gt;</code> element)
     */
    public TermsFilterData(Element root)
    {
        List docsList;

        this.root = root;
        docsList = root.elements("document");

        // loading snippets & titles
        snippets = new String[docsList.size()];
        titles = new String[docsList.size()];
        ids = new String[docsList.size()];

        Iterator docsIterator = docsList.iterator();

        for (int i = 0; docsIterator.hasNext(); i++)
        {
            Element document = (Element) docsIterator.next();

            Element snippet = document.element("snippet");
            Element title = document.element("title");
            ids[i] = document.attributeValue("id");

            if (snippet != null)
            {
                snippets[i] = snippet.getText().toLowerCase();
            }
            else
            {
                snippets[i] = "";
            }

            if (title != null)
            {
                titles[i] = title.getText().toLowerCase();
            }
            else
            {
                titles[i] = "";
            }
        }

        // loading lexical information
        stemsMap = new HashMap();
        inverseStemsMap = new FrequencyHashMap();
        stopWordsMap = new HashMap();

        Iterator stemsIterator = root.elements("l").iterator();

        while (stemsIterator.hasNext())
        {
            Element lexicalElement = (Element) stemsIterator.next();

            String term = lexicalElement.attributeValue("t");
            String stem = lexicalElement.attributeValue("s");

            // adding a new stemmed form of this term
            addStem(term, stem);

            // adding information whether this term is a stop-word
            if (lexicalElement.attribute("sw") != null)
            {
                stopWordsMap.put(term, new Boolean(true));
            }
        }

        // loadind query element
        Element queryElement = root.element("query");

        stemmedQuery = "";
        stemmedQueryVector = new Vector();

        if (queryElement != null)
        {
            StringTokenizer queryTokenizer = new StringTokenizer(queryElement.getText());

            for (int i = 0; queryTokenizer.hasMoreTokens(); i++)
            {
                String queryToken = queryTokenizer.nextToken();

                if (isStopWord(queryToken))
                {
                    continue;
                }

                String stemmedQueryToken = getStem(queryToken);

                if (stemmedQueryToken == null)
                {
                    stemmedQueryToken = queryToken;
                }

                if (i > 0)
                {
                    stemmedQuery += " ";
                }

                stemmedQuery += stemmedQueryToken;
                stemmedQueryVector.add(queryToken);
            }
        }
    }

    /**
     * Loads a list of snippets from associated filter's XML document. Snippets are created as a
     * concatenation of both <code>&lt;snippet&gt;</code> and <code>&lt;title&gt;</code>
     * subelements of all <code>&lt;document&gt;</code> subelements contained in the
     * <code>&lt;searchresult&gt;</code> element.
     *
     * @return array containing snippets extracted from documents
     */
    public String [] getSnippets()
    {
        return snippets;
    }


    public String [] getTitles()
    {
        return titles;
    }


    /**
     * Obtains a stemmed form of given term from {@link TermsFilterData#stemsMap}.
     *
     * @param term term for which we want to obtain a stem
     *
     * @return either stemmed form of given term or <code>null</code> if there is no stemmed form
     *         for this term (which means it is possibly on the stop list) or unchanged term, if
     *         no stems are available,
     */
    public String getStem(String term)
    {
        // this will return null if this term is not present in the map
        return (String) stemsMap.get(term);
    }


    // term - not stemmed !!!
    public boolean isStopWord(String term)
    {
        Boolean isStopWord = (Boolean) stopWordsMap.get(term);

        if ((isStopWord != null) && (isStopWord.equals(Boolean.TRUE)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public String getStemmedQuery()
    {
        return stemmedQuery;
    }


    public void removeGroups()
    {
        Dom4jUtils.removeChildren(root, "group");
    }


    public void removeLexicalInformation()
    {
        Dom4jUtils.removeChildren(root, "l");
    }


    /**
     * Saves a list of terms and their weights in documents. For each entry in the
     * <code>terms</code> parameter, creates a <code>&lt;term&gt;</code> subelement of the
     * <code>&lt;searchresult&gt;</code> element with two subelements:
     * 
     * <ul>
     * <li>
     * <code>&lt;term&gt;</code> the term itself, and
     * </li>
     * <li>
     * <code>&lt;weight&gt;</code> this term's weight (float number) from corresponding entry in
     * the <code>termWeights</code> array
     * </li>
     * </ul>
     */
    public void saveLexicalElements(Vector lexicalElements, float [][] termsWeights)
    {
        for (int i = 0; i < lexicalElements.size(); i++)
        {
            LexicalElement lexicalElement = (LexicalElement) lexicalElements.elementAt(i);

            Element term = lexicalElement.toXML();

            // adding information about inflected form of this lexical element
            String form = (String) inverseStemsMap.get(lexicalElement.toString());
            term.addAttribute("form", form);

            // saving weights of this lexical element in documents
            for (int j = 0; j < termsWeights[0].length; j++)
            {
                if (termsWeights[i][j] > 0)
                {
                    
                    Element docElement = term.addElement("doc");
                    docElement.addAttribute("id", ids[j]);
                    docElement.addAttribute("weight", termsWeights[i][j] + "");
                }
            }

            root.add(term);
        }
    }
}