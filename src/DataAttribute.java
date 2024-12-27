import java.util.LinkedList;

public class DataAttribute {

    String name;
    int frequency;
    LinkedList<DataAttribute> subAttributes;

    public DataAttribute(String name, int frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public DataAttribute(String name, int frequency, LinkedList<DataAttribute> subAttributes) {
        this.name = name;
        this.frequency = frequency;
        this.subAttributes = subAttributes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedList<DataAttribute> getSubAttributes() {
        return subAttributes;
    }

    public void setSubAttributes(LinkedList<DataAttribute> subAttributes) {
        this.subAttributes = subAttributes;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    @Override
    public String toString() {
        return "DataAttribute{" +
                "name='" + name + '\'' +
                ", frequency=" + frequency +
                ", subAttributes=" + subAttributes +
                '}';
    }
}
