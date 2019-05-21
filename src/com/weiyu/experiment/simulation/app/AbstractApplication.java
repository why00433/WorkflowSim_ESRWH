package com.weiyu.experiment.simulation.app;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import org.griphyn.vdl.dax.ADAG;

import com.weiyu.experiment.simulation.util.Distribution;

/**
 * @author Shishir Bharathi
 */
public abstract class AbstractApplication implements Application {
    
    protected ADAG dax;
    protected int id;
    protected Map<String, Distribution> distributions;
    
    protected AbstractApplication() {
        this.dax = new ADAG();
        this.id = 0;
        this.distributions = new HashMap<String, Distribution>();
    }
    
    protected Map<String, Distribution> getDistributions() {
        return this.distributions;
    }

    protected double generateDouble(String key) {
        Distribution dist = this.distributions.get(key);
        if (dist == null) {
            throw new RuntimeException("No such distribution: "+key);
        }
        return dist.getDouble();
    }

    protected long generateLong(String key) {
        return (long) generateDouble(key);
    }

    protected int generateInt(String key) {
        return (int) generateDouble(key);
    }

    protected abstract void populateDistributions();
    
    protected String getNewJobID() {
        return String.format("ID%05d", this.id++);
    }
    
    @Override
    public void printWorkflow(OutputStream os) throws Exception {
        this.dax.toXML(new OutputStreamWriter(os), "", null);
    }
    
    public ADAG getDAX() {
        return this.dax;
    }
    
    public void generateWorkflow(String... args) throws Exception {
        populateDistributions();
        processArgs(args);
        constructWorkflow();
    }
    
    protected abstract void processArgs(String[] args);
    protected abstract void constructWorkflow();
}
