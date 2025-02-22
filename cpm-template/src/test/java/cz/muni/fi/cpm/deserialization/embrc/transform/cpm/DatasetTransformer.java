package cz.muni.fi.cpm.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.*;

public abstract class DatasetTransformer {
    protected static final String UNKNOWN_NS = "_";
    protected static final String UNKNOWN_PREFIX = "_";
    protected static final String GEN_NS = "gen";
    protected static final String GEN_PREFIX = "gen";
    protected final ProvFactory pF;
    protected final ICpmProvFactory cPF;
    protected final ProvUtilities u;

    public DatasetTransformer(ProvFactory pF, ICpmProvFactory cPF) {
        this.pF = pF;
        this.cPF = cPF;
        this.u = new ProvUtilities();
    }

    public Document toDocument(Document dsDoc) {
        IndexedDocument indexedDS = new IndexedDocument(pF, dsDoc);
        modifyDS(indexedDS);
        Document bb = createBB(indexedDS);
        Document updatedDs = indexedDS.toDocument();

        ((Bundle) bb.getStatementOrBundle().getFirst()).getStatement().addAll(u.getStatement(updatedDs));
        bb.getNamespace().extendWith(updatedDs.getNamespace());
        return bb;
    }

    protected abstract Document createBB(IndexedDocument indexedDS);

    protected abstract void modifyDS(IndexedDocument indexedDS);

    protected QualifiedName newQNWithUnknownNS(String localPart) {
        return pF.newQualifiedName(UNKNOWN_NS, localPart, UNKNOWN_PREFIX);
    }

    protected QualifiedName newQnWithGenNS(String localPart) {
        return pF.newQualifiedName(GEN_NS, localPart, GEN_PREFIX);
    }
}
