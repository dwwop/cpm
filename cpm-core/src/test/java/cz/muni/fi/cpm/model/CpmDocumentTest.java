package cz.muni.fi.cpm.model;

import cz.muni.fi.cpm.constants.CpmTypeConstants;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.ProvFactory;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;

public class CpmDocumentTest {
    private DatatypeFactory datatypeFactory;
    private ProvFactory pF;
    private CpmFactory cF;

    @Before
    public void setUp() throws Exception {
        pF = new ProvFactory();
        cF = new CpmFactory(pF);
        datatypeFactory = DatatypeFactory.newInstance();
    }


    @Test
    public void testConstructor_nullDocument() {
        Assert.assertThrows(IllegalArgumentException.class, () -> new CpmDocument(null, pF));
    }

    @Test
    public void testConstructor_withoutBundle() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        Assert.assertThrows(IllegalArgumentException.class, () -> new CpmDocument(document, pF));
    }


    @Test
    public void testConstructor_namespace() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        CpmDocument doc = new CpmDocument(document, pF);

        Assert.assertEquals(document.getNamespace().getNamespaces(), doc.getNamespaces().getNamespaces());
    }

    @Test
    public void testConstructor_mainActivity() {
        Document document = pF.newDocument();
        document.setNamespace(cF.newCpmNamespace());

        QualifiedName id = pF.newQualifiedName("uri", "bundle", "ex");
        Bundle bundle = pF.newNamedBundle(id, new ArrayList<>());
        document.getStatementOrBundle().add(bundle);

        QualifiedName activityId = pF.newQualifiedName("uri", "activity", "ex");
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        String type = CpmTypeConstants.MAIN_ACTIVITY;
        Collection<Attribute> attributes = new ArrayList<>();
        Activity activity = cF.newCpmActivity(activityId, startTime, endTime, type, attributes);
        bundle.getStatement().add(activity);

        CpmDocument doc = new CpmDocument(document, pF);

        Assert.assertNotNull(doc.getNode(activityId));
        Assert.assertFalse(doc.getBackbone().isEmpty());
        Assert.assertNotNull(doc.getMainActivity());
        Assert.assertEquals(activity, doc.getNode(activityId).getElement());
    }
}