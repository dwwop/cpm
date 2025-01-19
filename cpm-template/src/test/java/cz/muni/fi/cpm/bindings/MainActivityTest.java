package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.constants.CpmType;
import cz.muni.fi.cpm.constants.DctAttributeConstants;
import cz.muni.fi.cpm.vanilla.CpmProvFactory;
import org.junit.jupiter.api.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MainActivityTest {

    @Test
    public void toStatements_basicIdSet_returnsOneStatement() {
        MainActivity mainActivity = new MainActivity();
        QualifiedName id = new QualifiedName("uri", "activityExample", "ex");
        mainActivity.setId(id);

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());

        assertNotNull(statements);
        assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        assertInstanceOf(Activity.class, statement);

        Activity activity = (Activity) statement;
        assertEquals(id, activity.getId());

        assertNotNull(activity.getType());
        assertEquals(1, activity.getType().size());
        Type type = activity.getType().getFirst();
        assertEquals(CpmType.MAIN_ACTIVITY.toString(), ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void toStatements_withReferencedMetaBundleId_returnsMetaBundleId() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));
        QualifiedName referencedMetaBundleId = new QualifiedName("uri", "metaBundleId", "ex");
        mainActivity.setReferencedMetaBundleId(referencedMetaBundleId);

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());
        Activity activity = (Activity) statements.getFirst();

        assertNotNull(activity.getOther());
        assertEquals(1, activity.getOther().size());
        assertEquals(CpmAttributeConstants.REFERENCED_META_BUNDLE_ID, activity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(referencedMetaBundleId, activity.getOther().getFirst().getValue());
    }

    @Test
    public void toStatements_withHasPart_returnsCorrectHasPart() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));
        QualifiedName hasPart = new QualifiedName("uri", "hasPart", "ex");
        mainActivity.setHasPart(hasPart);

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());
        Activity activity = (Activity) statements.getFirst();

        assertNotNull(activity.getOther());
        assertEquals(1, activity.getOther().size());
        assertEquals(DctAttributeConstants.HAS_PART, activity.getOther().getFirst().getElementName().getLocalPart());
        assertEquals(hasPart, activity.getOther().getFirst().getValue());
    }

    @Test
    public void toStatements_withUsed_returnsCorrectUsed() {
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

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());

        assertEquals(3, statements.size());
        assertInstanceOf(Used.class, statements.get(1));
        assertEquals(activity, ((Used) statements.get(1)).getActivity());
        assertEquals(usedList.getFirst().getBcId(), ((Used) statements.get(1)).getEntity());
        assertEquals(usedList.getFirst().getId(), ((Used) statements.get(1)).getId());
        assertInstanceOf(Used.class, statements.get(2));
        assertEquals(activity, ((Used) statements.get(2)).getActivity());
        assertEquals(usedList.getLast().getBcId(), ((Used) statements.get(2)).getEntity());
        assertEquals(usedList.getLast().getId(), ((Used) statements.get(2)).getId());
    }

    @Test
    public void toStatements_withGenerated_returnsCorrectGenerated() {
        MainActivity mainActivity = new MainActivity();
        QualifiedName activity = new QualifiedName("uri", "activityExample", "ex");
        mainActivity.setId(activity);

        List<org.openprovenance.prov.model.QualifiedName> generatedList = new ArrayList<>();
        generatedList.add(new QualifiedName("uri", "generated1", "ex"));
        generatedList.add(new QualifiedName("uri", "generated2", "ex"));
        mainActivity.setGenerated(generatedList);

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());

        assertEquals(3, statements.size());
        assertInstanceOf(WasGeneratedBy.class, statements.get(1));
        assertEquals(activity, ((WasGeneratedBy) statements.get(1)).getActivity());
        assertEquals(generatedList.getFirst(), ((WasGeneratedBy) statements.get(1)).getEntity());
        assertInstanceOf(WasGeneratedBy.class, statements.get(2));
        assertEquals(activity, ((WasGeneratedBy) statements.get(2)).getActivity());
        assertEquals(generatedList.getLast(), ((WasGeneratedBy) statements.get(2)).getEntity());
    }

    @Test
    public void toStatements_withTimes_returnsCorrectTimes() throws Exception {
        MainActivity mainActivity = new MainActivity();
        mainActivity.setId(new QualifiedName("uri", "activityExample", "ex"));

        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        XMLGregorianCalendar startTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T10:00:00");
        XMLGregorianCalendar endTime = datatypeFactory.newXMLGregorianCalendar("2024-11-13T12:00:00");
        mainActivity.setStartTime(startTime);
        mainActivity.setEndTime(endTime);

        List<Statement> statements = mainActivity.toStatements(new CpmProvFactory());

        Activity activity = (Activity) statements.get(0);
        assertEquals(startTime, activity.getStartTime());
        assertEquals(endTime, activity.getEndTime());
    }
}
