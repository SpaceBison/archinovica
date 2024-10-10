package archinovica;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class PitchSet extends SemioticGroup<PitchClass> implements Cloneable {
    public PitchClass[] pitchArray, projectedPitches;
    public int totalProjectedPitches;
    public PitchClass center;
    public ArrayList<IntervalSet> myIntervalSets;

    public PitchSet() {
        projectedPitches = new PitchClass[12];
        totalProjectedPitches = 0;
        myIntervalSets = new ArrayList<IntervalSet>();
    }

    public PitchSet(PitchClass[] pP) {
        projectedPitches = pP;
        totalProjectedPitches = 0;
        ArrayList<PitchClass> projectedList = new ArrayList<PitchClass>();
        for (int i = 0; i < 12; i++) {
            if (projectedPitches[i] != null) {
                projectedList.add(projectedPitches[i]);
                totalProjectedPitches++;
            }
        }
        myIntervalSets = analyseIntervals(projectedList);
    }

    public PitchSet(PitchSet ps) {
        //super();
        myIntervalSets = ps.myIntervalSets;
        for (PitchClass pc : ps) {
            add(pc.clone());
        }
        //pitchArray = new archinovica.PitchClass[12];
    }

    public ArrayList<IntervalSet> analyseIntervals(ArrayList<PitchClass> pitches) {
        ArrayList<IntervalSet> intervalSets = new ArrayList<IntervalSet>();
        for (int i = 0; i < pitches.size(); i++) {
            intervalSets.add(new IntervalSet(pitches, i));
        }
        return intervalSets;
    }

    public abstract boolean isProjected(PitchClass rsp);
    /*public archinovica.PitchSet transformSet(archinovica.PitchClass[] pP, int setting){
    //clear(); // why is this here?
    return this;
    }

     */

    /*@Override
    public boolean generativeSearch(){
    //System.out.println("GENERATIVE SEARCH: " + this);
    /*if(size() == 0){
    return size() == totalProjectedPitches;
    }

    if(size() > 0)
    generations++;
    for(int i = 0; i < size(); i++){
    if(((SubSpace)get(i)).generateNeighbors()){
    for(archinovica.SemioticFunction s: this){
    ((SubSpace)s).declareIndependence();
    }
    resetSpaceCode(); // is this fully necessary? is there a work around?

    return size() == totalProjectedPitches;
    }

    //System.out.println("NEIGBORS GENERATED");
    }
    return size() == totalProjectedPitches;
    }
     */

    public PitchClass getCenter() {
        if (center != null) {
            return center;
        }
        int minimumDistances = Integer.MAX_VALUE;
        for (PitchClass p : this) {
            int totalDistances = 0;
            for (PitchClass p1 : this) {
                if (p != p1) {
                    totalDistances += p.getDistance(p1);
                }
            }
            if (totalDistances < minimumDistances) {
                minimumDistances = totalDistances;
                center = p;
            }
        }
        if (center == null) {
            System.out.println("NO CENTER FOUND!");
            center = Archinovica.ORIGIN;
        }
        return center;
    }

    public void transpose(Interval interval) {
        //System.out.println("TRANSPOSING SET: " + interval);
        for (PitchClass p : this) {
            p.transpose(interval);
        }
    }

    public PitchClass[] getArrayBySignified() {
        if (pitchArray != null) {
            return pitchArray;
        }
        pitchArray = new PitchClass[12];
        for (PitchClass pc : this) {
            pitchArray[pc.signifier] = pc;
        }
        return pitchArray;
    }

    public PitchClass[] getArray() {
        if (pitchArray != null) {
            return pitchArray;
        }
        pitchArray = new PitchClass[12];
        if (size() == 0) {
            return pitchArray;
        }

        PitchClass referencePitch = get(0);
        IntervalSet referenceSet = new IntervalSet(this, 0);
        Interval transposition = null;

        //System.out.println("REFERENCE SET:" + referenceSet);
        //System.out.println("MYINTERVAL SETS: " + myIntervalSets);

        for (IntervalSet is : myIntervalSets) {
            if (referenceSet.equals(is)) {
                //System.out.println("PRP: " + is.projectedReferencePitch);
                //System.out.println("IS: " + is);
                transposition = referencePitch.getInterval(is.projectedReferencePitch);
                break;
            }
        }
        //System.out.println("TRANSPOSITION: " + transposition);
        for (PitchClass pc : this) {
            pitchArray[(pc.signifier + transposition.signifier) % 12] = pc;
        }
        return pitchArray;
    }

    @Override
    public void clear() {
        center = null;
        pitchArray = null;
        super.clear();
    }

    /*public archinovica.HorizontalSearcher randomTransformationCheck(){
    archinovica.PitchClass[] pitchBinary = new archinovica.PitchClass[12];
    for(int i = 0; i < 12; i++){
    if(Math.random() > 0.5)
    pitchBinary[i] = new archinovica.PitchClass(i);
    }
    archinovica.HorizontalSearcher hs = new archinovica.HorizontalSearcher(pitchBinary, this);
    System.out.println(hs);
    print();
    archinovica.Archinovica.gui.clearSigns();
    archinovica.Archinovica.gui.displaySigns(this);
    return hs;
    }
     */

    public void print() {
        System.out.println(this);
        printPitchArray();
    }

    public void printPitchArray() {
        System.out.println(Arrays.asList(getArray()));
    }

    @Override
    public String toString() {
        return "archinovica.PitchSet | PROJECTED: " + totalProjectedPitches + " " + Arrays.asList(projectedPitches) + " | FOUND: " + size() + " " + super.toString();
    }

    @Override
    public PitchSet clone() {
        PitchSet ps = copyArray();
        ps.projectedPitches = projectedPitches;
        ps.myIntervalSets = myIntervalSets;
        ps.totalProjectedPitches = totalProjectedPitches;
        return ps;
    }

    public abstract PitchSet copyArray();

    public String getSetRecord() {
        String a = "";
        for (PitchClass pc : this) {
            a += "fifths\n";
            a += pc.signified[0] + "\n";
            a += "thirds\n";
            a += pc.signified[1] + "\n";
        }

        return a.substring(0, a.length() - 1);
    }

    /*public class archinovica.VerticalSearcher extends archinovica.GenerativeSearcher
    {

    public int totalProjectedPitches;

    public archinovica.VerticalSearcher(archinovica.PitchSet ps, archinovica.PitchClass[] pP){
    super(ps);
    projectedPitches = pP;
    totalProjectedPitches = 0;
    archinovica.PitchSet.this.myIntervalSets = new ArrayList<archinovica.IntervalSet>();
    for(int i = 0; i < 12; i++)
    if(pP[i] != null){
    archinovica.PitchSet.this.myIntervalSets.add(new archinovica.IntervalSet(pP, i));
    totalProjectedPitches++;
    }
    System.out.println(myIntervalSets);

    add(new SubSpace(this));

    //System.out.println("initialCASE: " + this);
    //System.out.println("With INITIAL INTERVAL SET: " + myIntervalSets.get(0));

    while(size() < totalProjectedPitches){
    //System.out.println("GS!!!! " + this);
    generativeSearch();
    }
    archinovica.PitchSet.this.addAll((ArrayList<E>)this);
    //setArray();
    // archinovica.Archinovica.gui.displayPitches(myPitchSet.getArray());

    }

    public boolean pitchFound(SubSpace s){
    //System.out.println("SUBSPACEFOUND VERTICALLY?");
    if(getSubSpaces(s) < size() || contains(s))
    return false;
    //System.out.println("Doesn't contain: " + s);
    int i = 0;
    for(i = 0; i < size(); i++)
    if(!s.isHigher(get(i)))
    break;
    add(i,s);
    if(isValidSet()){

    return true;
    }
    //System.out.println("INVALID");
    remove(i);
    return false;
    }

    public boolean isValidSet(){
    archinovica.IntervalSet overlayedSet = null;
    for(int i = 0; i < size(); i++){
    archinovica.IntervalSet is = new archinovica.IntervalSet(this, i);
    boolean acceptablePitch = false;
    for(archinovica.IntervalSet projectedSet: archinovica.PitchSet.this.myIntervalSets){
    overlayedSet = projectedSet;
    if(is.overlays(projectedSet)){
    acceptablePitch = true;
    break;
    }
    }
    if(!acceptablePitch)
    return false;
    }
    //System.out.println("PROJECTED INTERVAL SET: " + overlayedSet);
    return true;
    }

    }
     */

}
