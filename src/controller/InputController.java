package controller;

import enums.Type;
import model.*;
import view.file.FileInputProcessor;
import view.fxml.DrawCircuit;

import java.util.ArrayList;

public class InputController {
    private static InputController instance;

    public static InputController getInstance() {
        if(instance == null) {
            instance = new InputController();
        }
        return instance;
    }

    private final ArrayList<Node> nodes = new ArrayList<>();
    private final ArrayList<Union> unions = new ArrayList<>();
    private final ArrayList<Resistor> resistors = new ArrayList<>();
    private final ArrayList<Capacitor> capacitors = new ArrayList<>();
    private final ArrayList<Inductor> inductors = new ArrayList<>();
    private final ArrayList<Diode> diodes = new ArrayList<>();
    private final ArrayList<CurrentSource> currentSources = new ArrayList<>();
    private final ArrayList<VoltageSource> voltageSources = new ArrayList<>();
    private final ArrayList<Element> elements = new ArrayList<>();
    private final ArrayList<Source> sources = new ArrayList<>();
    private Branch finalSuperiorBranch;
    private Node ground;

    private double deltaV = 0;
    private double deltaI = 0;
    private double deltaT = 0;
    private double tranTime = 0;

    public Node pDiode;
    public Node nDiode;

    //ADD METHODS
    public void addDiode(String name, String node1, String node2) {
        addElement(name, node1, node2, 0, Type.DIODE);
    }

    public void addElement(String name, String node1, String node2, double value, Type type) {
        Node nodeP = findNode(node1) == null ? new Node(node1) : findNode(node1);
        Node nodeN = findNode(node2) == null ? new Node(node2) : findNode(node2);
        if (!nodes.contains(nodeP)) nodes.add(nodeP);
        if (!nodes.contains(nodeN)) nodes.add(nodeN);
        if (!nodeP.getNeighborNodes().contains(nodeN)) {
            nodeP.getNeighborNodes().add(nodeN);
        }
        if (!nodeN.getNeighborNodes().contains(nodeP)) {
            nodeN.getNeighborNodes().add(nodeP);
        }
        if (type.equals(Type.RESISTOR)) {
            Resistor resistor = new Resistor(name, nodeP, nodeN, value);
            resistors.add(resistor);
            elements.add(resistor);
            nodeP.getElements().add(resistor);
            nodeN.getElements().add(resistor);
        } else if (type.equals(Type.CAPACITOR)) {
            Capacitor capacitor = new Capacitor(name, nodeP, nodeN, value);
            capacitors.add(capacitor);
            elements.add(capacitor);
            nodeP.getElements().add(capacitor);
            nodeN.getElements().add(capacitor);
        } else if (type.equals(Type.INDUCTOR)) {
            Inductor inductor = new Inductor(name, nodeP, nodeN, value);
            inductors.add(inductor);
            elements.add(inductor);
            nodeP.getElements().add(inductor);
            nodeN.getElements().add(inductor);
        } else if (type.equals(Type.DIODE)) {
            Diode diode = new Diode(name, nodeP, nodeN);
            diodes.add(diode);
            elements.add(diode);
            nodeP.getElements().add(diode);
            nodeN.getElements().add(diode);
            pDiode = nodeP; nDiode = nodeN;
        }
    }

    public void addSource(String name, String node1, String node2, double value,
                          double amplitude, double frequency, double phase, Type type) {
        Node nodeP = findNode(node1) == null ? new Node(node1) : findNode(node1);
        Node nodeN = findNode(node2) == null ? new Node(node2) : findNode(node2);
        if (!nodes.contains(nodeP)) nodes.add(nodeP);
        if (!nodes.contains(nodeN)) nodes.add(nodeN);
        if (!nodeP.getNeighborNodes().contains(nodeN)) {
            nodeP.getNeighborNodes().add(nodeN);
        }
        if (!nodeN.getNeighborNodes().contains(nodeP)) {
            nodeN.getNeighborNodes().add(nodeP);
        }
        if (type.equals(Type.CURRENT_SOURCE)) {
            CurrentSource currentSource = new CurrentSource(name, nodeP, nodeN, value, amplitude, frequency, phase);
            currentSources.add(currentSource);
            sources.add(currentSource);
            nodeP.getSources().add(currentSource);
            nodeN.getSources().add(currentSource);
        } else if (type.equals(Type.VOLTAGE_SOURCE)) {
            VoltageSource voltageSource = new VoltageSource(name, nodeP, nodeN, value, amplitude, frequency, phase);
            voltageSources.add(voltageSource);
            sources.add(voltageSource);
            nodeP.getSources().add(voltageSource);
            nodeN.getSources().add(voltageSource);
        }
    }

    public void addVoltageControlledSource(String name, String node1, String node2,
                                           double gain, String controllerNode1, String controllerNode2, Type type) {
        Node nodeP = findNode(node1) == null ? new Node(node1) : findNode(node1);
        Node nodeN = findNode(node2) == null ? new Node(node2) : findNode(node2);
        if (!nodes.contains(nodeP)) nodes.add(nodeP);
        if (!nodes.contains(nodeN)) nodes.add(nodeN);
        if (!nodeP.getNeighborNodes().contains(nodeN)) {
            nodeP.getNeighborNodes().add(nodeN);
        }
        if (!nodeN.getNeighborNodes().contains(nodeP)) {
            nodeN.getNeighborNodes().add(nodeP);
        }
        Node controllerNodeP = findNode(controllerNode1);
        Node controllerNodeN = findNode(controllerNode2);
        if (type.equals(Type.V_C_C_S)) {
            VoltageControlledCurrentSource V_C_C_S;
            V_C_C_S = new VoltageControlledCurrentSource(name, nodeP, nodeN, gain, controllerNodeP, controllerNodeN);
            currentSources.add(V_C_C_S);
            sources.add(V_C_C_S);
            nodeP.getSources().add(V_C_C_S);
            nodeN.getSources().add(V_C_C_S);
        } else if (type.equals(Type.V_C_V_S)) {
            VoltageControlledVoltageSource V_C_V_S;
            V_C_V_S = new VoltageControlledVoltageSource(name, nodeP, nodeN, gain, controllerNodeP, controllerNodeN);
            voltageSources.add(V_C_V_S);
            sources.add(V_C_V_S);
            nodeP.getSources().add(V_C_V_S);
            nodeN.getSources().add(V_C_V_S);
        }
    }

    public void addCurrentControlledSource(String name, String node1, String node2,
                                           double gain, String s_Branch, Type type) {
        Node nodeP = findNode(node1) == null ? new Node(node1) : findNode(node1);
        Node nodeN = findNode(node2) == null ? new Node(node2) : findNode(node2);
        if (!nodes.contains(nodeP)) nodes.add(nodeP);
        if (!nodes.contains(nodeN)) nodes.add(nodeN);
        if (!nodeP.getNeighborNodes().contains(nodeN)) {
            nodeP.getNeighborNodes().add(nodeN);
        }
        if (!nodeN.getNeighborNodes().contains(nodeP)) {
            nodeN.getNeighborNodes().add(nodeP);
        }
        Branch branch;
        if (findElement(s_Branch) != null) {
            branch = findElement(s_Branch);
        } else {
            branch = findSource(s_Branch);
        }
        if (type.equals(Type.C_C_C_S)) {
            CurrentControlledCurrentSource C_C_C_S;
            C_C_C_S = new CurrentControlledCurrentSource(name, nodeP, nodeN, gain, branch);
            currentSources.add(C_C_C_S);
            sources.add(C_C_C_S);
            nodeP.getSources().add(C_C_C_S);
            nodeN.getSources().add(C_C_C_S);
        } else if (type.equals(Type.C_C_V_S)) {
            CurrentControlledVoltageSource C_C_V_S;
            C_C_V_S = new CurrentControlledVoltageSource(name, nodeP, nodeN, gain, branch);
            voltageSources.add(C_C_V_S);
            sources.add(C_C_V_S);
            nodeP.getSources().add(C_C_V_S);
            nodeN.getSources().add(C_C_V_S);
        }
    }

    //SEARCH METHODS
    public Node findNode(String name) {
        for (Node node : nodes) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    public Resistor findResistor(String name) {
        for (Resistor resistor : resistors) {
            if (resistor.getName().equals(name)) {
                return resistor;
            }
        }
        return null;
    }

    public Capacitor findCapacitor(String name) {
        for (Capacitor capacitor : capacitors) {
            if (capacitor.getName().equals(name)) {
                return capacitor;
            }
        }
        return null;
    }

    public Inductor findInductor(String name) {
        for (Inductor inductor : inductors) {
            if (inductor.getName().equals(name)) {
                return inductor;
            }
        }
        return null;
    }

    public Diode findDiode(String name) {
        for (Diode diode : diodes) {
            if (diode.getName().equals(name)) {
                return diode;
            }
        }
        return null;
    }

    public CurrentSource findCurrentSource(String name) {
        for (CurrentSource currentSource : currentSources) {
            if (currentSource.getName().equals(name)) {
                return currentSource;
            }
        }
        return null;
    }

    public VoltageSource findVoltageSource(String name) {
        for (VoltageSource voltageSource : voltageSources) {
            if (voltageSource.getName().equals(name)) {
                return voltageSource;
            }
        }
        return null;
    }

    public Element findElement(String name) {
        for (Element element : elements) {
            if (element.getName().equals(name)) {
                return element;
            }
        }
        return null;
    }

    public Source findSource(String name) {
        for (Source source : sources) {
            if (source.getName().equals(name)) {
                return source;
            }
        }
        return null;
    }

    //HELPING METHODS

    public double getValueOfString(String string) {
        char unit = string.charAt(string.length() - 1);
        double factor = getUnit(unit);
        double value;
        try {
            if (factor == -2) {
                return -1;
            } else if (factor == -1) {
                value = Double.parseDouble(string);
            } else {
                value = Double.parseDouble(string.substring(0, string.length() - 1)) * factor;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        if (value < 0) {
            return -1;
        }
        return value;
    }

    public double getUnit(char unit) {
        if (unit == 'p') {
            return Math.pow(10, -12);
        } else if (unit == 'n') {
            return Math.pow(10, -9);
        } else if (unit == 'u') {
            return Math.pow(10, -6);
        } else if (unit == 'm') {
            return Math.pow(10, -3);
        } else if (unit == 'k') {
            return Math.pow(10, 3);
        } else if (unit == 'M') {
            return Math.pow(10, 6);
        } else if (unit == 'G') {
            return Math.pow(10, 9);
        } else if (unit == '0' || unit == '1' || unit == '2' || unit == '3' || unit == '4' ||
                unit == '5' || unit == '6' || unit == '7' || unit == '8' || unit == '9') {
            return -1;
        } else {
            return -2;
        }
    }

    public void restartProgram() {
        nodes.clear();
        unions.clear();
        resistors.clear();
        capacitors.clear();
        inductors.clear();
        diodes.clear();
        currentSources.clear();
        voltageSources.clear();
        elements.clear();
        sources.clear();
        DrawCircuit.getAllBranchesTemp().clear();
        DrawCircuit.getNewSeriesBranches().clear();
        DrawCircuit.getNewTempBranch().clear();

        deltaV = 0;
        deltaI = 0;
        deltaT = 0;
        tranTime = 0;

        Solver.time = 0;
        Solver.step = 0;
        Solver.output.delete(0, Solver.output.length());

        FileInputProcessor.commandLine = 0;
        FileInputProcessor.getSaveLines().clear();
    }

    //GETTERS AND SETTERS

    public ArrayList<Resistor> getResistors() {
        return resistors;
    }

    public ArrayList<Capacitor> getCapacitors() {
        return capacitors;
    }

    public ArrayList<Inductor> getInductors() {
        return inductors;
    }

    public ArrayList<Diode> getDiodes() {
        return diodes;
    }

    public ArrayList<CurrentSource> getCurrentSources() {
        return currentSources;
    }

    public ArrayList<VoltageSource> getVoltageSources() {
        return voltageSources;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Union> getUnions() {
        return unions;
    }

    public ArrayList<Element> getElements() {
        return elements;
    }

    public ArrayList<Source> getSources() {
        return sources;
    }

    public double getDeltaV() {
        return deltaV;
    }

    public double getDeltaI() {
        return deltaI;
    }

    public double getDeltaT() {
        return deltaT;
    }

    public double getTranTime() {
        return tranTime;
    }

    public Branch getFinalSuperiorBranch() { return finalSuperiorBranch; }

    public Node getGround() { return ground; }

    public void setGround(Node ground) { this.ground = ground; }

    public void setFinalSuperiorBranch(Branch finalSuperiorBranch) { this.finalSuperiorBranch = finalSuperiorBranch; }

    public void setAllNodesNotVisited() {
        for (Node node : nodes) {
            node.setNotVisited();
        }
    }

    public void setAllNodesVisited() {
        for (Node node : nodes) {
            node.setVisited();
        }
    }

    public void setDeltaV(double deltaV) {
        this.deltaV = deltaV;
    }

    public void setDeltaI(double deltaI) {
        this.deltaI = deltaI;
    }

    public void setDeltaT(double deltaT) {
        this.deltaT = deltaT;
    }

    public void setTranTime(double tranTime) {
        this.tranTime = tranTime;
    }
}
