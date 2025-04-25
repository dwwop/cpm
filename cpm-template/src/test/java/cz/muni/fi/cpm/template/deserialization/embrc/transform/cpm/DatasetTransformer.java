package cz.muni.fi.cpm.template.deserialization.embrc.transform.cpm;

import cz.muni.fi.cpm.model.ICpmProvFactory;
import cz.muni.fi.cpm.template.mapper.ITemplateProvMapper;
import cz.muni.fi.cpm.template.mapper.TemplateProvMapper;
import org.openprovenance.prov.model.*;

public abstract class DatasetTransformer {
    protected static final String BLANK_NS = "https://openprovenance.org/blank#";
    protected static final String BLANK_PREFIX = "_";
    protected static final String GEN_NS = "gen";
    protected static final String GEN_PREFIX = "gen";
    protected final ProvFactory pF;
    protected final ICpmProvFactory cPF;
    protected final ProvUtilities u;
    protected final ITemplateProvMapper mapper;

    public DatasetTransformer(ProvFactory pF, ICpmProvFactory cPF) {
        this.pF = pF;
        this.cPF = cPF;
        this.u = new ProvUtilities();
        this.mapper = new TemplateProvMapper(cPF);
    }

    public Document toDocument(Document dsDoc) {
        IndexedDocument indexedDS = new IndexedDocument(pF, dsDoc);
        modifyDS(indexedDS);
        Document ti = createTI(indexedDS);
        Document updatedDs = indexedDS.toDocument();

        ((Bundle) ti.getStatementOrBundle().getFirst()).getStatement().addAll(u.getStatement(updatedDs));
        ti.getNamespace().extendWith(updatedDs.getNamespace());
        return ti;
    }

    protected abstract Document createTI(IndexedDocument indexedDS);

    protected abstract void modifyDS(IndexedDocument indexedDS);

    protected QualifiedName newQNWithBlankNS(String localPart) {
        return pF.newQualifiedName(BLANK_NS, localPart, BLANK_PREFIX);
    }

    protected QualifiedName newQnWithGenNS(String localPart) {
        return pF.newQualifiedName(GEN_NS, localPart, GEN_PREFIX);
    }
}
