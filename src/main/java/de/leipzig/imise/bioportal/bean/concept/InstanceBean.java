package de.leipzig.imise.bioportal.bean.concept;


public class InstanceBean extends AbstractConceptBean {
    // property to set instance types
    private InstanceTypesList instanceType;

    public InstanceTypesList getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(InstanceTypesList instanceType) {
        this.instanceType = instanceType;
    }
}
