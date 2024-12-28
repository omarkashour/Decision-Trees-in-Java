import java.util.ArrayList;
import java.util.LinkedList;

public class DataAttribute {

    String name;
    long frequency;
    ArrayList<DataAttribute> subAttributes;
    long positiveCount;
    long negativeCount;

    public DataAttribute(String name, long frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public DataAttribute(String name, long frequency, ArrayList<DataAttribute> subAttributes, long positiveCount, long negativeCount) {
        this.name = name;
        this.frequency = frequency;
        this.subAttributes = subAttributes;
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public ArrayList<DataAttribute> getSubAttributes() {
        return subAttributes;
    }

    public void setSubAttributes(ArrayList<DataAttribute> subAttributes) {
        this.subAttributes = subAttributes;
    }

    public long getPositiveCount() {
        return positiveCount;
    }

    public void setPositiveCount(long positiveCount) {
        this.positiveCount = positiveCount;
    }

    public long getNegativeCount() {
        return negativeCount;
    }

    public void setNegativeCount(long negativeCount) {
        this.negativeCount = negativeCount;
    }

    @Override
    public String toString() {
        return "DataAttribute{" +
                "name='" + name + '\'' +
                ", frequency=" + frequency +
                ", subAttributes=" + subAttributes +
                ", positiveCount=" + positiveCount +
                ", negativeCount=" + negativeCount +
                '}';
    }
}
