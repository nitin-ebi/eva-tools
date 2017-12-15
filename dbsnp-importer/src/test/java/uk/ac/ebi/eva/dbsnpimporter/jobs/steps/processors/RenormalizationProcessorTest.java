/*
 * Copyright 2017 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.eva.dbsnpimporter.jobs.steps.processors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.ebi.eva.commons.core.models.pipeline.Variant;
import uk.ac.ebi.eva.dbsnpimporter.models.LocusType;
import uk.ac.ebi.eva.dbsnpimporter.models.Orientation;
import uk.ac.ebi.eva.dbsnpimporter.models.SubSnpCoreFields;
import uk.ac.ebi.eva.dbsnpimporter.sequence.FastaSequenceReader;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RenormalizationProcessorTest {

    private static FastaSequenceReader fastaSequenceReader;

    private static RenormalizationProcessor renormalizer;

    @BeforeClass
    public static void setUpClass() throws Exception {
        fastaSequenceReader = new FastaSequenceReader(Paths.get("src/test/resources/Gallus_gallus-5.0.test.fa"));
        renormalizer = new RenormalizationProcessor(fastaSequenceReader);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        fastaSequenceReader.close();
    }

    @Test
    public void insertion() throws Exception {
        int position = 11;
        Variant variant = new Variant("22", position, position, "", "G");
        Variant renormalized = renormalizer.process(variant);
        assertNotNull(renormalized);
        assertEquals(position - 1, renormalized.getStart());
    }
    @Test
    public void deletion() throws Exception {
        int position = 11;
        Variant variant = new Variant("22", position, position, "G", "");
        Variant renormalized = renormalizer.process(variant);
        assertNotNull(renormalized);
        assertEquals(position - 1, renormalized.getStart());
    }

    @Test
    public void longerDeletion() throws Exception {
        int position = 36 * 60 + 27;  // first position of "TTCT" in chicken fasta
        Variant variant = new Variant("22", position, position, "TCT", "");
        Variant renormalized = renormalizer.process(variant);
        assertNotNull(renormalized);
        assertEquals(position - 1, renormalized.getStart());
        assertEquals("TTC", renormalized.getReference());
        assertEquals("", renormalized.getAlternate());
    }

    @Test
    public void snpsRemainUnchanged() throws Exception {
        int position = 11;
        Variant variant = new Variant("22", position, position, "G", "T");
        Variant renormalized = renormalizer.process(variant);
        assertNotNull(renormalized);
        assertEquals(position, renormalized.getStart());
    }


}