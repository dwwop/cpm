package cz.muni.fi.cpm.deserialization.mou.transform;

import cz.muni.fi.cpm.deserialization.constants.PbmFactory;
import cz.muni.fi.cpm.deserialization.mou.schema.DiagnosisMaterial;
import cz.muni.fi.cpm.deserialization.mou.schema.Patient;
import cz.muni.fi.cpm.deserialization.mou.schema.Tissue;
import cz.muni.fi.cpm.model.ICpmProvFactory;
import org.openprovenance.prov.model.Document;
import org.openprovenance.prov.model.Other;
import org.openprovenance.prov.model.ProvFactory;
import org.openprovenance.prov.model.ValueConverter;

import java.util.ArrayList;
import java.util.List;


public abstract class PatientTransformer {

    public static final String BBMRI_NS = "http://www.bbmri.cz/schemas/biobank/data";
    public static final String BBMRI_PREFIX = "bbmri";

    public static final String SUFFIX_TEMPLATE = "-patient-%s-%s";

    protected final ProvFactory pF;
    protected final ICpmProvFactory cPF;
    protected final PbmFactory pbmF;

    public PatientTransformer(ProvFactory pF, ICpmProvFactory cPF, PbmFactory pbmF) {
        this.pF = pF;
        this.cPF = cPF;
        this.pbmF = pbmF;
    }

    public List<Document> toDocuments(Patient patient) {
        List<Document> docs = new ArrayList<>();
        for (Tissue tissue : patient.getLts().getTissues()) {
            String suffix = String.format(SUFFIX_TEMPLATE, patient.getId(), tissue.getSampleId());
            Document doc = createBB(patient, suffix);

            addTissueDSToDoc(doc, suffix, tissue);
            docs.add(doc);
        }

        for (DiagnosisMaterial dM : patient.getSts().getDiagnosisMaterials()) {
            String suffix = String.format(SUFFIX_TEMPLATE, patient.getId(), dM.getSampleId());
            Document doc = createBB(patient, suffix);

            addDMDSToDoc(doc, suffix, dM);
            docs.add(doc);
        }
        return docs;
    }

    protected abstract Document createBB(Patient patient, String suffix);

    protected abstract void addTissueDSToDoc(Document doc, String suffix, Tissue tissue);

    protected abstract void addDMDSToDoc(Document doc, String suffix, DiagnosisMaterial dM);


    protected Other newOther(String qNLocal, Object o) {
        return pF.newOther(
                pF.newQualifiedName(BBMRI_NS, qNLocal, BBMRI_PREFIX),
                o,
                new ValueConverter(pF).getXsdType(o));
    }
}
