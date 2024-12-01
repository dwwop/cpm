package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityTest {

    @Test
    public void testToStatements_basic() throws Exception {
        MainActivity mainActivity = new MainActivity();
        QualifiedName id = new QualifiedName("uri", "activityExample", "ex");
        mainActivity.setId(id);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());

        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        Assert.assertTrue(statement instanceof Activity);

        Activity activity = (Activity) statement;
        Assert.assertEquals(id, activity.getId());

        Assert.assertNotNull(activity.getType());
        Assert.assertEquals(1, activity.getType().size());
        Type type = activity.getType().getFirst();
        Assert.assertEquals(CpmType.MAIN_ACTIVITY.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void testToStatements_withReferencedMetaBundleId() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));
        QualifiedName referencedMetaBundleId = new QualifiedName("uri", "metaBundleId", "ex");
        mainActivity.setReferencedMetaBundleId(referencedMetaBundleId);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());
        Activity activity = (Activity) statements.getFirst();

        Assert.assertNotNull(activity.getOther());
        Assert.assertEquals(1, activity.getOther().size());
        Assert.assertEquals(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, activity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(referencedMetaBundleId, activity.getOther().getFirst().getValue());
    }

    @Test
    public void testToStatements_withHasPart() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));
        QualifiedName hasPart = new QualifiedName("uri", "hasPart", "ex");
        mainActivity.setHasPart(hasPart);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());
        Activity activity = (Activity) statements.getFirst();

        Assert.assertNotNull(activity.getOther());
        Assert.assertEquals(1, activity.getOther().size());
        Assert.assertEquals(DctAttributeConstants.HAS_PART, activity.getOther().getFirst().getElementName().getLocalPart());
        Assert.assertEquals(hasPart, activity.getOther().getFirst().getValue());
    }

    @Test
    public void testToStatements_withUsed() {
        MainActivity mainActivity = new MainActivity();
        QualifiedName activity = new QualifiedName("uri", "activityExample", "ex");
        mainActivity.setId(activity);

        QualifiedName uId = new QualifiedName("uri", "usedId1", "ex");
        QualifiedName uBc = new QualifiedName("uri", "used1", "ex");
        MainActivityUsed mainActivityUsed = new MainActivityUsed();
        mainActivityUsed.setId(uId);
        mainActivityUsed.setBcId(uBc);

        QualifiedName uBc2 = new QualifiedName("uri", "used2", "ex");
        MainActivityUsed mainActivityUsed2 = new MainActivityUsed();
        mainActivityUsed2.setBcId(uBc2);
        List<MainActivityUsed> usedList = Arrays.asList(mainActivityUsed, mainActivityUsed2);
        mainActivity.setUsed(usedList);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());

        Assert.assertEquals(3, statements.size());
        Assert.assertTrue(statements.get(1) instanceof org.openprovenance.prov.model.Used);
        Assert.assertEquals(activity, ((Used) statements.get(1)).getActivity());
        Assert.assertEquals(usedList.getFirst().getBcId(), ((Used) statements.get(1)).getEntity());
        Assert.assertEquals(usedList.getFirst().getId(), ((Used) statements.get(1)).getId());
        Assert.assertTrue(statements.get(2) instanceof org.openprovenance.prov.model.Used);
        Assert.assertEquals(activity, ((Used) statements.get(2)).getActivity());
        Assert.assertEquals(usedList.getLast().getBcId(), ((Used) statements.get(2)).getEntity());
        Assert.assertEquals(usedList.getLast().getId(), ((Used) statements.get(2)).getId());
    }

    @Test
    public void testToStatements_withGenerated() {
        MainActivity mainActivity = new MainActivity();
        QualifiedName activity = new QualifiedName("uri", "activityExample", "ex");
        mainActivity.setId(activity);

        List<org.openprovenance.prov.model.QualifiedName> generatedList = new ArrayList<>();
        generatedList.add(new QualifiedName("uri", "generated1", "ex"));
        generatedList.add(new QualifiedName("uri", "generated2", "ex"));
        mainActivity.setGenerated(generatedList);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());

        Assert.assertEquals(3, statements.size());
        Assert.assertTrue(statements.get(1) instanceof WasGeneratedBy);
        Assert.assertEquals(activity, ((WasGeneratedBy) statements.get(1)).getActivity());
        Assert.assertEquals(generatedList.getFirst(), ((WasGeneratedBy) statements.get(1)).getEntity());
        Assert.assertTrue(statements.get(2) instanceof WasGeneratedBy);
        Assert.assertEquals(activity, ((WasGeneratedBy) statements.get(2)).getActivity());
        Assert.assertEquals(generatedList.getLast(), ((WasGeneratedBy) statements.get(2)).getEntity());
    }

    @Test
    public void testToStatements_withStartAndEndTime() throws Exception {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        mainActivity.setStartTime(startTime);
        mainActivity.setEndTime(endTime);

        List<Statement> statements = mainActivity.toStatements(new CpmFactory());

        Activity activity = (Activity) statements.get(0);
        Assert.assertEquals(startTime, activity.getStartTime());
        Assert.assertEquals(endTime, activity.getEndTime());
    }
}
