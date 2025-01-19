package cz.muni.fi.cpm.divided.ordered;

import cz.muni.fi.cpm.model.CpmDocumentTest;

public class CpmOrderedDocumentTest extends CpmDocumentTest {
    public CpmOrderedDocumentTest() throws Exception {
        super(new CpmOrderedFactory());
    }
}
