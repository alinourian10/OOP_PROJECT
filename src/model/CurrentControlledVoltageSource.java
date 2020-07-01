package model;

public class CurrentControlledVoltageSource extends VoltageSource {
    private final double gain;
    private final Branch branch;

    public CurrentControlledVoltageSource(String name, Node nodeIn, Node nodeOut,
                                          double gain, Branch branch) {
        super(name, nodeIn, nodeOut);
        this.gain = gain;
        this.branch = branch;
    }

    @Override
    public double getVoltage(Node node, double time) {
        double value;
        if (branch instanceof Element) {
            Element element = (Element)branch;
            value = gain * Math.abs(element.getCurrent(element.getNodeP()));
        } else {
            Source source = (Source)branch;
            value = gain * Math.abs(source.getCurrent(source.getNodeP(), time));
        }
        if (node.getName().equals(nodeP.getName())) {
            return value;
        } else {
            return -1 * value;
        }
    }

    @Override
    public double getValue(Node node, double time) {
        return getVoltage(node, time);
    }

    @Override
    public String getType() {
        return "currentControlledVoltageSource";
    }
}
