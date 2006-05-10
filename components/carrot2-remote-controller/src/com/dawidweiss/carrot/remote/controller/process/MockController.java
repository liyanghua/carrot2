
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

package com.dawidweiss.carrot.remote.controller.process;


import com.dawidweiss.carrot.remote.controller.process.scripted.*;
import java.io.*;
import java.util.*;


/**
 * Mock controller object.
 */
public class MockController
    implements Controller
{
    private boolean cachesInput;
    private boolean sentResponse;
    private boolean usesCachedInput;
    private List usedInputs = new ArrayList();
    private List usedOutputs = new ArrayList();
    private List usedFilters = new ArrayList();

    /* Getters for diagnostics */
    public boolean getWritesCache()
    {
        return cachesInput;
    }


    public boolean getSentsResponse()
    {
        return sentResponse;
    }


    public boolean getUsesCachedInput()
    {
        return usesCachedInput;
    }


    public List getUsedInputs()
    {
        return this.usedInputs;
    }


    public List getUsedFilters()
    {
        return this.usedFilters;
    }


    public List getUsedOutputs()
    {
        return this.usedOutputs;
    }


    /**
     * Do not cache input (never)
     */
    public boolean getDoCacheInput()
    {
        return false;
    }


    /**
     * Ignore if the component wants to cache input
     */
    public void setDoCacheInput(boolean doCacheInput)
    {
        this.cachesInput = doCacheInput;
    }


    /**
     * Ignore. response is not really important.
     */
    public void sendResponse(InputStream data)
        throws java.io.IOException
    {
        this.sentResponse = true;
    }


    public InputStream invokeInputComponent(String componentId, Query query, Map optionalParams)
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        this.usedInputs.add(componentId);

        return new ByteArrayInputStream(new byte [] {  });
    }


    public InputStream invokeInputComponent(String componentId, Query query)
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        return invokeInputComponent(componentId, query, null);
    }


    public void setUseCachedInput(boolean newValue)
    {
        this.usesCachedInput = newValue;
    }


    /**
     * Claim to never use cached input.
     */
    public boolean getUseCachedInput()
    {
        return false;
    }


    public InputStream invokeOutputComponent(
        String componentId, InputStream data, Map optionalParams
    )
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        this.usedOutputs.add(componentId);

        return data;
    }


    public InputStream invokeOutputComponent(String componentId, InputStream data)
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        return invokeOutputComponent(componentId, data, null);
    }


    public InputStream invokeFilterComponent(
        String componentId, InputStream data, Map optionalParams
    )
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        this.usedFilters.add(componentId);

        return data;
    }


    public InputStream invokeFilterComponent(String componentId, InputStream data)
        throws com.dawidweiss.carrot.remote.controller.process.scripted.ComponentFailureException, 
            java.io.IOException
    {
        return invokeFilterComponent(componentId, data, null);
    }
}