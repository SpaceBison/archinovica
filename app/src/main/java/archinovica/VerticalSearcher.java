package archinovica;

 
import java.util.ArrayList;
import java.util.Collections;

/**
 * Aggiungi qui una descrizione della classe archinovica.VerticalSearcher
 *
 * @author (il tuo nome)
 * @version (un numero di versione o una data)
 */
public class VerticalSearcher extends GenerativeSearcher {
    public VerticalSearcher(PitchSet unlimitedSet,  RecursiveSearchPoint.GenerateNeighborsCallback callback) {
        super(unlimitedSet, unlimitedSet, callback);
    }

    public PitchSet limitSet(int setting, RecursiveSearchPoint.GenerateNeighborsCallback callback) {
        while (!((VerticalSet) mySourceSet).limited()) {
            //System.out.println("SEARCHING for vertical results");
            findNextPitches();
            for (PotentialPitch pp : myGrid) {
                if (pp.getSources().size() == mySourceSet.size()) {
                    mySourceSet.add(new RecursiveSearchPoint(pp));
                    resetSearch();
                    myProjectedSet = mySourceSet;
                    break;
                }
            }
            //System.out.println(mySourceSet);
        }
        //System.out.println("FINAL ARRAY: " + Arrays.asList(mySourceSet.getArray()));
        return mySourceSet;
    }

    private int getMinimumPotentialDistance(PotentialPitch pp) {
        int mpd = pp.getDistanceSearched();
        ArrayList<RecursiveSearchPoint> sources = pp.getSources();
        for (PitchClass pc : mySourceSet) {
            if (!sources.contains(pc)) {
                mpd += ((RecursiveSearchPoint) pc).getDistanceSearched();
            }
        }
        return mpd;
    }

    private int getMinMPD() {
        int min = Integer.MAX_VALUE;
        for (PotentialPitch pp : myGrid) {
            min = Math.min(min, getMinimumPotentialDistance(pp));
        }
        return min;
    }

    public PitchSet getSearchableSources() {
        VerticalSet searchableSources = new VerticalSet();
        searchableSources.addAll(mySourceSet);
        Collections.sort(searchableSources, new Excentricity(mySourceSet.getCenter()));
        /*int minMPD = getMinMPD();
        int maxSearchablePitches = 0;
        Hashtable<archinovica.RecursiveSearchPoint, Integer> sourceMPDCode = new Hashtable<archinovica.RecursiveSearchPoint, Integer>();
        for(archinovica.PotentialPitch pp: myGrid)
        if(getMinimumPotentialDistance(pp) == minMPD)
        for(archinovica.RecursiveSearchPoint source: pp.getSources()){
        int numberOfSearchablePitches = sourceMPDCode.get(source) + 1;
        maxSearchablePitches = Math.max(numberOfSearchablePitches, maxSearchablePitches);
        sourceMPDCode.put(source, numberOfSearchablePitches);
        }
        int minSearchablePitches = Integer.MAX_VALUE;
        for(Integer searchablePitches: sourceMPDCode.values())
        minSearchablePitches = Math.min(minSearchablePitches, searchablePitches);
        if(minSearchablePitches == maxSearchablePitches || myGrid.size() == 0){
        searchableSources.addAll(mySourceSet);
        Collections.sort(searchableSources, new Excentricity(mySourceSet.getCenter()));
        System.out.println("sorceMPDCode is USELESS");
        }
        else{
        for(archinovica.PitchClass pc: mySourceSet)
        if(sourceMPDCode.get((archinovica.RecursiveSearchPoint)pc) == maxSearchablePitches){
        int insertionIndex = Collections.binarySearch(searchableSources, pc, new Centricity(mySourceSet.getCenter()));
        searchableSources.add(pc);
        }
        System.out.println("sorceMPDCode is USEFUL!!!!!!!");
        }
         */
        return searchableSources;
    }

}
