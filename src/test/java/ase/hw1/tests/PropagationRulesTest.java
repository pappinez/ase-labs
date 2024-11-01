package ase.hw1.tests;

import org.junit.jupiter.api.Test;
import tools.refinery.generator.ModelSemantics;
import tools.refinery.generator.standalone.StandaloneRefinery;
import tools.refinery.language.model.problem.Problem;
import tools.refinery.logic.term.truthvalue.TruthValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static ase.hw1.tests.RefineryFileTests.*;
import static org.junit.jupiter.api.Assertions.*;

class PropagationRulesTest {

    private String getStage(String fileName) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(fileName);
        List<String> lines = toLines(resourceAsStream);
        return String.join("\n",lines);
    }

    private String getStages(String... fileNames) {
        List<String> listOfStages = Arrays.stream(fileNames).map(this::getStage).toList();
        return String.join("\n",listOfStages);
    }

    /**
     * Tests the consistency of a symbol in a problem
     * @param problem The problem
     * @param symbolName the name of the symbol
     * @return whether the solution is consistent or not
     */
    private boolean testConsistency(String problem, String symbolName) {
        Problem parsedProblem = null;
        try {
            parsedProblem = StandaloneRefinery.getProblemLoader().loadString(problem);
        } catch (IOException e) {
            fail("Failed to load the problem");
        }
        ModelSemantics semantics = StandaloneRefinery.getSemanticsFactory().tryCreateSemantics(parsedProblem);

        for(var i : semantics.getProblemTrace().getRelationTrace().keySet())
            System.out.println(i.getName());

        var relation = semantics.getProblemTrace().getPartialRelation(symbolName);

        var cursor = semantics.getPartialInterpretation(relation).getAll();
        while (cursor.move()) {
            if (cursor.getValue() == TruthValue.ERROR) {
                return false;
            }
        }
        return true;
    }

    /*
    @Test
    void testRule1() {
        testConsistency(getStages("base"),"");
    }*/

    @Test
    void testRule2() {
        assertTrue(testConsistency(getStages(
                "base", "oneEntryInRegion"),"entryInRegion"));
        assertTrue(testConsistency(getStages(
                "base", "oneEntryInRegion"),"multipleEntryInRegion"));
        assertFalse(testConsistency(getStages(
                "base", "oneEntryInRegion", "twoEntryInRegion"),"multipleEntryInRegion"));
        assertFalse(testConsistency(getStages(
                "base", "oneEntryInRegion", "twoEntryInRegion", "entryInRegionPropRule"),"entryInRegion"));
    }

    /*
    @Test
    void testRule3() {
        testConsistency(getStages("incomingToEntryRule"),"");
    }*/

}
