package cz.muni.fi.cpm.bindings;

import cz.muni.fi.cpm.constants.CpmAttributeConstants;
import cz.muni.fi.cpm.vannila.CpmFactory;
import org.junit.Assert;
import org.junit.Test;
import org.openprovenance.prov.model.*;
import org.openprovenance.prov.vanilla.QualifiedName;

import java.util.List;

public class CpmAgentTest {
    public static final String TEST_AGENT = "TEST";

    @Test
    public void testToStatements_basic() {
        TestAgent agent = new TestAgent();
        QualifiedName id = new QualifiedName("uri", "agentExample", "ex");
        agent.setId(id);

        List<Statement> statements = agent.toStatements(new CpmFactory());

        Assert.assertNotNull(statements);
        Assert.assertEquals(1, statements.size());

        Statement statement = statements.getFirst();
        Assert.assertTrue(statement instanceof Agent);

        Agent resultAgent = (Agent) statement;
        Assert.assertEquals(id, resultAgent.getId());

        Assert.assertNotNull(resultAgent.getType());
        Assert.assertEquals(1, resultAgent.getType().size());
        Type type = resultAgent.getType().getFirst();
        Assert.assertEquals(TEST_AGENT, ((QualifiedName) type.getValue()).getLocalPart());
    }

    @Test
    public void testToStatements_withContactIdPid() {
        TestAgent agent = new TestAgent();
        agent.setId(new QualifiedName("uri", "agentExample", "ex"));
        String contactIdPid = "contact123";
        agent.setContactIdPid(contactIdPid);

        List<Statement> statements = agent.toStatements(new CpmFactory());
        Agent resultAgent = (Agent) statements.getFirst();

        List<Other> otherAttributes = resultAgent.getOther();
        Assert.assertNotNull(otherAttributes);
        Assert.assertEquals(1, otherAttributes.size());

        Attribute contactIdAttr = otherAttributes.getFirst();
        Assert.assertTrue(contactIdAttr.getValue() instanceof LangString);
        Assert.assertEquals(CpmAttributeConstants.CONTACT_ID_PID, contactIdAttr.getElementName().getLocalPart());
        Assert.assertEquals(contactIdPid, ((LangString) contactIdAttr.getValue()).getValue());
    }

    private class TestAgent extends CpmAgent {
        @Override
        public String getType() {
            return TEST_AGENT;
        }
    }
}
